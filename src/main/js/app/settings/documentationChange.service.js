'use strict';

angular
	.module('docs.settings')
	.factory('documentationChangeService', function($rootScope, DOCS) {
		return {
			documentationChange: function() {
				$rootScope.$emit(DOCS.onDocumentationUpdate);
			}
		};
	});
