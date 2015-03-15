'use strict';

describe('selectFeed.modal.controller.spec.js', function() {
  var $controller;
  var scope;

  var modalInstanceMock;
  var deferredHelper;
  var feedServiceMock;

  beforeEach(function() {
    module('docs.test', 'docs.settings');
    inject(function(_$q_, _$controller_, $rootScope, _deferredHelper_, modalInstanceMockFactory) {
      $controller = _$controller_;
      scope = $rootScope.$new();
      deferredHelper = _deferredHelper_;
      modalInstanceMock = modalInstanceMockFactory();
    });

    feedServiceMock = {
      listFeeds: deferredHelper.createFn()
    };
  });

  it('should list all categories and select first', function() {
    createController();

    feedServiceMock.listFeeds.deferred.resolveAndApply(['1', '2']);

    expect(scope.categories).toEqual(['1', '2']);
    expect(scope.selectedCategory).toEqual('1');
  });

  it('should close modal with user feed', function() {
    var feed = {feedUrl: 'http://example.com/file.zip'};
    createController();
    feedServiceMock.listFeeds.deferred.resolveAndApply([feed]);

    //when
    scope.select(feed);

    //then
    expect(modalInstanceMock.close).toHaveBeenCalledWith(feed);
  });

  function createController() {
    return $controller('SelectFeedModal', {
      $modalInstance: modalInstanceMock,
      $scope: scope,
      feedService: feedServiceMock
    });
  }
});
