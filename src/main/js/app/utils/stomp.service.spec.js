'use strict';

describe('stomp.service.spec.js', function() {
	var stompConnectSuccessCallback;
	var stompConnectErrorCallback;
	var connectUrl;
	var stomp;

	var $rootScope;
	beforeEach(function() {
		var stompConnect = function(headers, successCallback, errorCallback) {
			stompConnectSuccessCallback = successCallback;
			stompConnectErrorCallback = errorCallback;
		};
		module('docs.utils', function($provide) {
			$provide.value('Stomp', {
				over: function() {
					return {connect: stompConnect};
				}
			});
			$provide.value('SockJS', function(url) {
				connectUrl = url;
			});
		});

		inject(function(_stomp_, _$rootScope_) {
			stomp = _stomp_;
			$rootScope = _$rootScope_;
		});
	});

	it('should resolve stomp promise after successful connect', function() {
		var promiseResolved = false;

		stomp.connect()
			.then(function(client) {
				expect(client).toBeDefined();
				promiseResolved = true;
			})
			.catch(expectNoAction);

		$rootScope.$apply(stompConnectSuccessCallback);
		expect(promiseResolved).toEqual(true);
	});

	it('should reject stomp promise after connection failure', function() {
		var promiseResolved = false;

		stomp.connect()
			.then(expectNoAction)
			.catch(function(err) {
				expect(err).toBeDefined();
				promiseResolved = true;
			});

		$rootScope.$apply(stompConnectErrorCallback);
		expect(promiseResolved).toEqual(true);
	});

	function expectNoAction() {
		expect(true).toEqual(false);
	}
});
