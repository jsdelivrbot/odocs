'use strict';

angular
  .module('docs.settings')
  .controller('PendingDownloads', function($scope, downloadsService) {
    $scope.downloadingItems = [];
    $scope.toMegabytes = function(bytes) {
      return bytes/1024/1024;
    };
    $scope.removeItem = function(item) {
      if(item.isPending()) {
        downloadsService.requestAbort(item.id);
      } else {
        downloadsService.requestRemove(item.id);
      }
    };

    initialize();

    function initialize() {
      downloadsService
        .downloadingItems()
        .then(function(items) {
          $scope.downloadingItems = items;
        });
    }
  });
