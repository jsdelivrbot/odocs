'use strict';

angular
	.module('docs.constants')
	.constant('API', {
		url: function(url) {
			//sockjs requires full api url to initialize proper websocket connection
			var port = window.location.port ? ':' + window.location.port : '';
			var rootUrl = window.location.protocol + '//' + window.location.hostname;
			return rootUrl + port + '/api' + url;
		}
	});
