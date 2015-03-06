'use strict';

angular
  .module('docs.utils')
  .factory('_', function($window) {
    var _ = $window._;
    var sprintf = $window.sprintf;
    var vsprintf = $window.vsprintf;

    function maybe(value) {
      var obj = null;

      function isEmpty() {
        return _.isNull(value) || _.isUndefined(value);
      }

      obj = {
        map: function (f) { return isEmpty() ? obj : maybe(f(value)); },
        orElse: function (n) { return isEmpty() ? n : value; },
        isEmpty: isEmpty,
        nonEmpty: _.negate(isEmpty)
      };
      return obj;
    }

    function fn(value) {
      return function() {
        return value;
      };
    }

    _.mixin({ 'maybe': maybe });
    _.mixin({ 'fn': fn });
    _.mixin({ 'fmt': sprintf });
    _.mixin({ 'vfmt': vsprintf });

    return _;
  });
