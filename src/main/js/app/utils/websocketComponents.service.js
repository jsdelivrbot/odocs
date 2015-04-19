'use strict';

angular
	.module('docs.utils')
	.factory('Stomp', function($window) {
		return $window.Stomp;
	})
	.factory('SockJS', function($window) {
		return $window.SockJS;
	});
