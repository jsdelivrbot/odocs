'use strict';

angular
  .module('docs.utils')
  .factory('moment', function($window) {
    return $window.moment;
  });
