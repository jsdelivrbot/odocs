'use strict';

angular
	.module('docs.test')
	.factory('deferredHelper', function($q, $rootScope) {
		return {
			create: create,
			createFn: createFunction
		};

		function createFunction() {
			var deferred = null;
			var result = jasmine.createSpy('deferred fn').and.callFake(function() {
				deferred = create();
				result.deferred = deferred;
				return deferred.promise;
			});
			result.deferred = deferred;

			return result;
		}

		function create() {
			var deferred = $q.defer();

			deferred.resolveAndApply = function(data) {
				deferred.resolve(data);
				$rootScope.$apply();
			};

			deferred.rejectAndApply = function(data) {
				deferred.reject(data);
				$rootScope.$apply();
			};

			return deferred;
		}
	});
