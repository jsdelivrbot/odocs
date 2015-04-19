'use strict';

angular
	.module('docs.header')
	.directive('menuItem', function($state, menuService, _) {
		return {
			restrict: 'E',
			replace: true,
			scope: {
				item: '='
			},
			templateUrl: 'header/menuItem.directive.html',
			link: function($scope) {
				$scope.isActive = menuService.isActive;
				$scope.isSelected = menuService.isSelected;

				$scope.mainVersion = function() {
					return _.first($scope.item.versions);
				};
			}
		};
	});
