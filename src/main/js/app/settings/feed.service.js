'use strict';

angular
  .module('docs.settings')
  .factory('feedService', function(httpClient, _) {
    return {
      listFeeds: _.partial(httpClient.get, '/feeds'),
      saveVersion: function(documentationId, feed) {
        return httpClient.post('/feeds/downloads', {
          docId: documentationId,
          feedUrl: feed.feedFile,
          feedName: feed.name
        });
      }
    };
  });
