'use strict';

angular
	.module('docs.settings')
	.controller('SelectDocumentationModal', function($scope, $modalInstance, documentationService) {
		$scope.documentations = [];
		$scope.newDocumentationName = '';
		$scope.close = $modalInstance.dismiss;
		$scope.select = $modalInstance.close;
		$scope.saveNewDocumentation = saveNewDocumentation;

		initialize();
		function initialize() {
			documentationService.documentation
				.list()
				.then(function(docs) {
					$scope.documentations = docs;
				});
		}

		function saveNewDocumentation() {
			documentationService.documentation
				.save(null, {name: $scope.newDocumentationName})
				.then($modalInstance.close);
		}
	});
