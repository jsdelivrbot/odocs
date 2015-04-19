'use strict';

angular
	.module('docs.test')
	.factory('stomp', function(deferredHelper) {
		var stompConnectionDeferred = deferredHelper.create();
		var result = function() {
			return stompConnectionDeferred.promise;
		};
		result.deferred = stompConnectionDeferred;

		return {
			connect: result
		};
	});
