'use strict';

angular
	.module('docs')
	.run(function($document) {
		$document.get(0).domain = 'localhost';
	});
