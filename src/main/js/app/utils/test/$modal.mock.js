function createModalMock($q) {
  var openedModalDeferred;
  var modal = {
    open: jasmine.createSpy('modal.open').and.callFake(function() {
      openedModalDeferred = $q.defer();
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
}
