function createPromiseResolver(scope) {
  function applyInScope(fn) { scope.$apply(fn); }

  return {
    resolve: function(deferred, data) {
      applyInScope(_.partial(deferred.resolve, data));
    },
    reject: function(deferred, data) {
      applyInScope(_.partial(deferred.reject, data));
    }
  }
}
