'use strict';

angular
  .module('docs.constants')
  .constant('API', {
    url: function(url) {
      return './api' + url;
    }
  });
