'use strict';

angular
	.module('docs.header')
	.controller('Header', function($scope, $state, menuService) {
		$scope.documentations = [];
		$scope.goHome = function() {
			menuService.deactivateAll();
			$state.go('root.home');
		};

		$scope.openSettings = function() {
			menuService.deactivateAll();
			$state.go('manageDocumentations');
		};

		initialize();

		function initialize() {
			loadDocumentations();
		}

		function loadDocumentations() {
			menuService().then(function(documentations) {
				$scope.documentations = documentations;
			});
		}
	});
