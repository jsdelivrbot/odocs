'use strict';

angular
	.module('docs.settings')
	.directive('pendingItemCancelButton', function() {
		return {
			restrict: 'E',
			templateUrl: 'settings/feed/pendingItemCancelButton.directive.html',
			scope: {
				item: '=',
				onRemove: '&',
				type: '@'
			},
			link: function(scope) {
				scope.buttonClass = function(item) {
					return getTypeProperties(item).actionClass;
				};
				scope.actionText = function(item) {
					return getTypeProperties(item).text;
				};
				scope.isVisible = function(item) {
					return getTypeProperties(item).isVisible();
				};
				scope.isActionAlreadyRequested = function(item) {
					return getTypeProperties(item).isRequested();
				};

				function getTypeProperties(item) {
					switch(scope.type) {
						case 'cancel':
							return {
								isRequested: function() {
									return item.abortRequested;
								},
								isVisible: item.isPending,
								actionClass: 'btn-danger',
								text: 'Cancel'
							};
						case 'remove':
							return {
								isRequested: function() {
									return item.removeRequested;
								},
								isVisible: item.isFinished,
								actionClass: 'btn-warning',
								text: 'Remove'
							}
					}
				}
			}
		}
	});
