'use strict';

angular
  .module('docs.utils')
  .directive('spinner', function() {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        active: '='
      },
      template: '<span class="glyphicon glyphicon-refresh glyphicon-spin" ng-show="active"></span>'
    };
  });
