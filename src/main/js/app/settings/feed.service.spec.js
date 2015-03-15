'use strict';

describe('feed.service.spec.js', function() {
  var $httpBackend;
  var feedService;
  var apiUrl;

  beforeEach(function() {
    module('docs.settings');

    inject(function(_$httpBackend_, _feedService_, API) {
      $httpBackend = _$httpBackend_;
      feedService = _feedService_;
      apiUrl = API.url;
    });
  });

  afterEach(function() {
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('should list ala available api', function() {
    $httpBackend
      .expectGET(apiUrl('/feeds'))
      .respond(200);

    feedService.listFeeds();

    $httpBackend.flush();
  });

  it('should start feed download', function() {
    var docId = 'docId';
    var feedUrl = 'http://example.com/file.zip';
    var feedName = 'feedName';
    $httpBackend
      .expectPOST(apiUrl('/feeds/downloads'), {docId: docId, feedName: feedName, feedUrl: feedUrl})
      .respond(200);

    feedService.saveVersion(docId, { feedFile: feedUrl, name: feedName});

    $httpBackend.flush();
  });
});
