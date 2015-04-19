'use strict';

angular
	.module('docs.settings')
	.filter('toMb', function(numberFilter) {
		return function(input, precision) {
			var mb = parseInt(input) / 1024 / 1024;
			return precision
				? numberFilter(mb, precision)
				: mb;
		};
	});
