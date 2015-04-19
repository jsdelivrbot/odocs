'use strict';

angular
	.module('docs.settings')
	.controller('SelectFeedModal', function($scope, $modalInstance, _, feedService) {
		$scope.close = $modalInstance.dismiss;
		$scope.select = $modalInstance.close;
		$scope.categories = [];
		$scope.selectedCategory = {};
		$scope.isActive = function(category) {
			return category === $scope.selectedCategory;
		};
		$scope.selectCategory = function(category) {
			$scope.selectedCategory = category;
		};

		feedService.listFeeds().then(function(categories) {
			$scope.categories = categories;
			$scope.selectedCategory = _.first($scope.categories);
		});
	});
