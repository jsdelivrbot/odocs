'use strict';

angular
  .module('docs.viewer')
  .controller('Viewer', function($scope, $state, DOCS, _, viewerService) {
    var versionId = $state.params.versionId;
    var deploymentInfo = {};

    $scope.versionUrl = '';
    $scope.currentUrl = '';

    initialize();

    function initialize() {
      viewerService.deploymentInfo(versionId).then(function(deploymentInfoResponse) {
        deploymentInfo = deploymentInfoResponse;
        $scope.$emit(DOCS.onVersionSelect, versionId);

        $scope.versionUrl = rootUrl() + _.maybe($state.params.url).orElse('/');

        $scope.$watch('currentUrl', function() {
          $state.go('viewer', { versionId: versionId, url: url() });
        });
      });
    }

    function rootUrl() {
      return deploymentInfo.protocol + '://' + deploymentInfo.host + ':' + deploymentInfo.port +
        _.maybe(deploymentInfo.initialDirectory)
          .map(function(dir) { return '/' + dir; })
          .orElse('');
    }

    function url() {
      return $scope.currentUrl.replace(rootUrl(), '');
    }
  });
