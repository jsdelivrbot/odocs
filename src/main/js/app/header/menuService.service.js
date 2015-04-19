'use strict';

angular
	.module('docs.header')
	.factory('menuService', function($q, $rootScope, DOCS, httpClient, _) {
		var documentations = [];
		var activeItemId = '';
		var listDeferred = $q.defer();

		loadDocumentationsList();
		function loadDocumentationsList() {
			httpClient.get('/menu/documentations')
				.then(function(docs) {
					clearList();
					_.each(docs, function(doc) {
						documentations.push(doc);
					});
					listDeferred.resolve(documentations);
				})
				.catch(listDeferred.reject);
		}

		function clearList() {
			while(documentations.length) {
				documentations.pop();
			}
		}

		function fetchList() {
			return listDeferred.promise;
		}

		$rootScope.$on(DOCS.onDocumentationUpdate, loadDocumentationsList);
		$rootScope.$on(DOCS.onVersionSelect, function(event, newVersionId) {
			activeItemId = newVersionId;
		});

		fetchList.isActive = function(documentation) {
			return _.contains(_.pluck(documentation.versions, 'id'), activeItemId);
		};

		fetchList.isSelected = function(versionId) {
			return versionId === activeItemId;
		};

		fetchList.deactivateAll = function() {
			activeItemId = null;
		};

		return fetchList;
	});
