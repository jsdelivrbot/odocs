'use strict';

describe('viewer.controller.spec.js', function() {
  var versionId = '1';
  var notDefinedUrl;
  var DOCS;

  var viewerServiceMock;

  var scope;
  var deferredHelper;
  var $controller;
  var stateMock;

  beforeEach(function() {
    module('docs.test', 'docs.viewer');

    inject(function(_$controller_, $rootScope, _deferredHelper_, _DOCS_) {
      deferredHelper = _deferredHelper_;
      $controller = _$controller_;
      scope = $rootScope.$new();
      DOCS = _DOCS_;
    });

    viewerServiceMock = {
      deploymentInfo: deferredHelper.createFn()
    };

    stateMock = {
      go: jasmine.createSpy('$state.go')
    };
  });

  it('should emit event with currently selected version', function() {
    spyOn(scope, '$emit').and.callThrough();
    createController({ versionId: versionId });

    //when
    viewerServiceMock.deploymentInfo.deferred.resolveAndApply({});

    //then
    expect(scope.$emit).toHaveBeenCalledWith(DOCS.onVersionSelect, versionId);
  });

  runParametrizedTest(
    'should fetch deploymentInfo and set versionUrl to be displayed',
    [
      [{ protocol: 'http', host: 'example.com', port: '111'}, notDefinedUrl, 'http://example.com:111/'],
      [{ protocol: 'http', host: 'example.com', port: '111'}, '/api/a', 'http://example.com:111/api/a'],
      [{ protocol: 'http', host: 'example.com', port: '111', initialDirectory: 'dir' }, notDefinedUrl, 'http://example.com:111/dir/' ],
      [{ protocol: 'http', host: 'example.com', port: '111', initialDirectory: 'dir' }, '/api/a', 'http://example.com:111/dir/api/a' ]
    ],
    function(deploymentInfo, urlParam, expectedUrl) {
      createController({ url: urlParam });

      viewerServiceMock.deploymentInfo.deferred.resolveAndApply(deploymentInfo);

      expect(scope.versionUrl).toEqual(expectedUrl);
    });

  it('should watch currentUrl and go to state with new url', function() {
    createController();

    viewerServiceMock.deploymentInfo.deferred.resolveAndApply({protocol: 'http', host: 'example.com', port: '8000'});

    expect(stateMock.go.calls.mostRecent().args).toEqual(['viewer', {versionId: versionId, url: ''}]);

    scope.currentUrl = 'http://example.com:8000/new/url';
    scope.$apply();

    expect(stateMock.go.calls.mostRecent().args).toEqual(['viewer', {versionId: versionId, url: '/new/url'}]);
  });

  function createController(maybeParams) {
    var defaultParams = {
      versionId: versionId,
      url: notDefinedUrl
    };

    stateMock.params = _.defaults(maybeParams || {}, defaultParams);
    $controller('Viewer', {
      $scope: scope,
      $state: stateMock,
      viewerService: viewerServiceMock
    });
  }
});
