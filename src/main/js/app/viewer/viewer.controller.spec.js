'use strict';

describe('viewer.controller.spec.js', function() {
  var versionId = '1';
  var notDefinedUrl;
  var DOCS;

  var viewerServiceMock;
  var promiseResolver;

  var scope;
  var $q;
  var $controller;
  var stateMock;

  var deploymentInfoDeferred;

  beforeEach(function() {
    module('docs.viewer');

    inject(function(_$q_, _$controller_, $rootScope, _DOCS_) {
      $q = _$q_;
      $controller = _$controller_;
      scope = $rootScope.$new();
      promiseResolver = createPromiseResolver(scope);
      DOCS = _DOCS_;
    });

    viewerServiceMock = {
      deploymentInfo: jasmine.createSpy('viewerService.deploymentInfo').and.callFake(function() {
        deploymentInfoDeferred = $q.defer();
        return deploymentInfoDeferred.promise;
      })
    };

    stateMock = {
      go: jasmine.createSpy('$state.go')
    };
  });

  it('should emit event with currently selected version', function() {
    spyOn(scope, '$emit').and.callThrough();
    createController({ versionId: versionId });

    //when
    promiseResolver.resolve(deploymentInfoDeferred, {});

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

      promiseResolver.resolve(deploymentInfoDeferred, deploymentInfo);

      expect(scope.versionUrl).toEqual(expectedUrl);
    });

  it('should watch currentUrl and go to state with new url', function() {
    createController();

    promiseResolver.resolve(deploymentInfoDeferred, {protocol: 'http', host: 'example.com', port: '8000'});
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
