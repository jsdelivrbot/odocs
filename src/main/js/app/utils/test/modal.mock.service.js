'use strict';

angular
  .module('docs.test')
  .factory('modalMock', function(deferredHelper) {
    var openedModalDeferred;
    var modal = {
      open: jasmine.createSpy('modal.open').and.callFake(function() {
        openedModalDeferred = deferredHelper.create();
        return {
          result: openedModalDeferred.promise
        };
      })
    };

    return {
      instance: modal,
      openDeferred: function() {
        return openedModalDeferred;
      }
    }
  });
