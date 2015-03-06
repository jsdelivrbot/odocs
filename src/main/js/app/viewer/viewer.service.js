'use strict';

angular
  .module('docs.viewer')
  .factory('viewerService', function(httpClient) {
    return {
      deploymentInfo: deploymentInfo
    };

    function deploymentInfo(versionId) {
      return httpClient.get('/deployment/status/' + versionId);
    }
  });
