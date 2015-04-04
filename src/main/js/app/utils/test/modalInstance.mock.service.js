'use strict';

angular
  .module('docs.test')
  .factory('modalInstanceMockFactory', function() {
    return function() {
      return {
        close: jasmine.createSpy('modal close'),
        dismiss: jasmine.createSpy('modal dismiss')
      }
    }
  });
