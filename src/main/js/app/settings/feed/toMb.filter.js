'use strict';

angular
  .module('docs.settings')
  .filter('toMb', function(numberFilter) {
    return function(input, precision) {
      var mb = parseInt(input)/1024/1024;
      if(precision) {
        return numberFilter(mb, precision);
      } else {
        return mb;
      }
    };
  });
