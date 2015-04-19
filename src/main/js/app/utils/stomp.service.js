'use strict';

angular
	.module('docs.utils')
	.factory('stomp', function($q, $log, API, _, Stomp, SockJS) {
		var connectDeferred = $q.defer();
		var stompClient = Stomp.over(new SockJS(API.url('/ws'), {debug: true}));
		stompClient.debug = function(str) {
			$log.debug(str);
		};
		stompClient.connect({},
			_.partial(connectDeferred.resolve, stompClient),
			connectDeferred.reject);

		return {
			connect: _.constant(connectDeferred.promise)
		};
	});
