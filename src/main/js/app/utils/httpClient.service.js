'use strict';

angular
  .module('docs.utils')
  .factory('httpClient', function($q, $http, $upload, API, _) {
    return {
      'get': function(url, params) {
        return unwrapHttpResponse($http.get(API.url(url), { params: params }));
      },
      put: function(url, data) {
        return unwrapHttpResponse($http.put(API.url(url), data));
      },
      post: function(url, data) {
        return unwrapHttpResponse($http.post(API.url(url), data));
      },
      'delete': function(url) {
        return unwrapHttpResponse($http.delete(API.url(url)));
      },
      uploadFile: uploadFile
    };

    function unwrapHttpResponse(httpDeferred) {
      return httpDeferred
        .then(function(response) {
          return response.data;
        });
    }

    function uploadFile(url, options) {
      var deferred = $q.defer();
      $upload
        .upload(_.assign(options, { url: API.url(url) }))
        .success(deferred.resolve)
        .error(deferred.reject);

      return deferred.promise;
    }
  });
