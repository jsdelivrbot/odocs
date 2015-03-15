'use strict';

angular
  .module('docs.utils')
  .directive('timeDuration', function(moment) {
    return {
      restrict: 'E',
      scope: {
        from: '=',
        to: '='
      },
      template: '<span>{{getDuration()}}</span>',
      link: function($scope) {
        $scope.getDuration = function() {
          var start = moment($scope.from);
          var end = moment($scope.to);
          return moment.duration(end.unix() - start.unix(), 'seconds').humanize();
        };
      }
    };
  });
