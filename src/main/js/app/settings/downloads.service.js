'use strict';

angular
	.module('docs.settings')
	.factory('downloadsService', function($rootScope, $q, $log, _, httpClient, stomp, documentationChangeService) {
		var downloadingItemsDeferred = $q.defer();
		var downloadingItems = [];
		var eventHandlers = {};
		var eventListners = {};

		initialize();

		return {
			downloadingItems: _.constant(downloadingItemsDeferred.promise),
			requestRemove: _.partial(requestAction, 'REMOVE', 'removeRequested'),
			requestAbort: _.partial(requestAction, 'ABORT', 'abortRequested')
		};

		function initialize() {
			initializeEventHandlers();
			initializeEventListeners();
			synchronizeWithBackend();
		}

		function requestAction(actionType, propertyToUpdate, itemId) {
			var item = _.findWhere(downloadingItems, {id: itemId});
			item[propertyToUpdate] = true;
			return httpClient
				.post('/feeds/downloads/' + itemId + '/actions', {action: actionType})
				.then(function() {
					return item;
				});
		}

		function onDownloadEvent(event) {
			executeEventHandler(event);
			executeEventListener(event);

			function executeEventListener(event) {
				var listener = eventListners[event.eventType];
				if(listener) {
					listener(event);
				}
			}

			function executeEventHandler(event) {
				var eventHandler = eventHandlers[event.eventType];
				if(eventHandler) {
					eventHandler(event);
				} else {
					$log.error('No handler for event ' + event);
				}
			}
		}

		function initializeEventListeners() {
			eventListners.FINISH = function() {
				documentationChangeService.documentationChange();
			};
		}

		function initializeEventHandlers() {
			var simpleStateUpdatingEvents = ['START', 'PROGRESS', 'ABORT', 'FINISH', 'ERROR'];

			_.forEach(simpleStateUpdatingEvents, function(eventType) {
				eventHandlers[eventType] = function(event) {
					findDownloadingItem(event, _.partial(assignAllEventDataToItem, event));
				};
			});
			eventHandlers.SUBMIT = function(submitEvent) {
				var item = convertToDownloadingItem({
					latestEvent: submitEvent
				});
				assignAllEventDataToItem(submitEvent, item);
				downloadingItems.push(item);
			};
			eventHandlers.REMOVE = function(removeEvent) {
				findDownloadingItem(removeEvent, function(item) {
					var itemIndex = downloadingItems.indexOf(item);
					if(itemIndex >= 0) {
						downloadingItems.splice(itemIndex, 1);
					}
				});
			};

			function findDownloadingItem(event, callback) {
				var result = _.findWhere(downloadingItems, {id: event.id});
				if(result) {
					result.latestEvent = event;
					callback(result);
				}
			}

			function assignAllEventDataToItem(event, item) {
				_.assign(item, _.omit(event, 'eventType'));
				if(!$rootScope.$$phase) {
					$rootScope.$digest();
				}
			}

			function convertToDownloadingItem(item) {
				function isInState(states) {
					return _.chain([states])
						.flatten()
						.includes(item.latestEvent.eventType)
						.value();
				}

				item.isWaiting = _.partial(isInState, ['SUBMIT', 'START']);
				item.isInProgress = _.partial(isInState, 'PROGRESS');
				item.isPending = _.partial(isInState, ['SUBMIT', 'START', 'PROGRESS']);
				item.isAborted = _.partial(isInState, 'ABORT');
				item.hasSuccess = _.partial(isInState, 'FINISH');
				item.hasError = _.partial(isInState, 'ERROR');
				item.isFinished = _.partial(isInState, ['ABORT', 'FINISH', 'ERROR']);
				item.getProgress = function() {
					return item.progress * 100.0;
				};

				return item;
			}
		}

		function synchronizeWithBackend() {
			httpClient
				.get('/feeds/downloads')
				.then(transformAndExecuteAsEventStream)
				.then(applyJobStateFlags)
				.then(stomp.connect)
				.then(function(websocket) {
					websocket.subscribe('/feeds/downloads/events', function(response) {
						var downloadEvent = JSON.parse(response.body);
						onDownloadEvent(downloadEvent);
					});
				})
				.then(function() {
					downloadingItemsDeferred.resolve(downloadingItems);
				});

			function applyJobStateFlags(items) {
				var itemsById = _.indexBy(items, 'id');
				_.forEach(downloadingItems, function(item) {
					var itemFromBackend = itemsById[item.id];
					item.abortRequested = itemFromBackend.abortRequested;
					item.removeRequested = itemFromBackend.removeRequested;
				});
			}

			function transformAndExecuteAsEventStream(items) {
				_.forEach(items, function(item) {
					_.chain([
						item.submitEvent, item.startEvent,
						item.progressEvent,
						item.abortEvent, item.finishEvent, item.errorEvent,
						item.removeEvent
					])
						.compact()
						.map(function(event) {
							return _.assign(event, {id: item.id});
						})
						.forEach(onDownloadEvent)
						.run();
				});

				return items;
			}
		}
	});
