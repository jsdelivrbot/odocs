'use strict';
(function angularJqueryMatchers(window, jasmine) {
  beforeEach(function () {
    jasmine.addMatchers({
      toBeNgVisible: function() {
        return {
          compare: function(actual) {
            return { pass: !$(actual).hasClass('ng-hide') }
          }
        };
      },

      toBeNgHidden: function() {
        return {
          compare: function(actual) {
            return { pass: $(actual).hasClass('ng-hide' )}
          }
        };
      }
    });
  });
})(window, window.jasmine);
