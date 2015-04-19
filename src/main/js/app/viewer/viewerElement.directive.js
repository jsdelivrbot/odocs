'use strict';

angular
	.module('docs.viewer')
	.constant('viewerElementConstant', {
		additionalSpacing: 12,
		iframeUrlRefreshInterval: 50
	})
	.directive('viewerElement', function($window, $sce, $interval, $log, viewerElementConstant) {
		return {
			restrict: 'E',
			replace: true,
			scope: {
				url: '=',
				currentUrl: '='
			},
			template: '<iframe ng-src="{{versionUrl}}" seamless="seamless" frameborder="0" width="100%" full-screen-element><p>Your browser does not support this.</p></iframe>',
			link: function($scope, element) {
				var windowElement = angular.element($window);
				var iframeElement = angular.element(element);

				$scope.versionUrl = '';

				initialize();

				function initialize() {
					initializeIFrameWatcher();
					initializeIframeUrlWatcher();

					$scope.$watch('url', function() {
						$scope.versionUrl = $sce.trustAsResourceUrl($scope.url);
					});
				}

				function initializeIframeUrlWatcher() {
					var urlCheckingPromise = $interval(function() {
						try {
							$scope.currentUrl = iframeElement.contents().get(0).location.href;
						} catch(e) {
							$log.warn('can not access iframe', e);
						}
					}, viewerElementConstant.iframeUrlRefreshInterval);

					$scope.$on('$destroy', function() {
						$interval.cancel(urlCheckingPromise);
					});
				}

				function initializeIFrameWatcher() {
					function setElementHeightToAllAvailableSpace() {
						var innerHeight = $window.innerHeight;
						innerHeight -= angular.element('.navbar').outerHeight() + viewerElementConstant.additionalSpacing;
						element.attr('height', innerHeight + 'px');
					}

					setElementHeightToAllAvailableSpace();
					windowElement.bind('resize.fullScreenElement', setElementHeightToAllAvailableSpace);
					$scope.$on('$destroy', function() {
						//can not use partial because it will be untestable. Not sure why. Just leave it as it is for now.
						windowElement.unbind('resize.fullScreenElement');
					});
				}
			}
		};
	});
