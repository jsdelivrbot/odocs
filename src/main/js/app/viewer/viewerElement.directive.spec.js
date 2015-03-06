'use strict';

describe('viewerElement.directive.spec.js', function() {
  var mockedElements;
  var windowMock;

  var $sce;
  var $compile;
  var $rootScope;

  var scope;

  beforeEach(function() {
    windowMock = { innerHeight: 1000 };
    module('docs.viewer', function($provide) {
      $provide.value('$window', windowMock);
    });

    inject(function(_$compile_, _$rootScope_, _$sce_) {
      $sce = _$sce_;
      $compile = _$compile_;
      $rootScope = _$rootScope_;
    });

    mockedElements = {
      mock: _.noop
    };
    spyOn(angular, 'element').and.callFake(function() {
      var elementSelector = arguments[0];
      var result = mockedElements[elementSelector] || mockedElements.mock(arguments[0]);
      return result || $(arguments[0], arguments[1]);
    });
  });

  it('should update iframe src on url change', function() {
    spyOn($sce, 'trustAsResourceUrl').and.callThrough();
    var directive = createDirective();

    //when
    scope.$apply(function() {
      directive.isolateScope().url = 'new url';
    });

    //then
    expect($sce.trustAsResourceUrl).toHaveBeenCalledWith('new url');
  });

  describe('current iframe url watch', function() {
    var $interval;
    var iframeRefresh;
    var locationMock;
    var iframeMock;

    beforeEach(function() {
      inject(function(_$interval_, viewerElementConstant) {
        $interval = _$interval_;
        iframeRefresh = viewerElementConstant.iframeUrlRefreshInterval;
      });

      locationMock = { href: 'about:blank' };
      iframeMock = {
        contents: _.constant( {get: _.constant( { location: locationMock })} )
      };

      mockedElements.mock = function(element) {
        return $(element).prop('tagName') === 'IFRAME'
          ? iframeMock
          : null;
      };

      createDirective({ currentUrl: '' });
    });

    it('should watch and update currentUrl on iframe url change', function() {
      //when
      updateUrl('http://example.com');

      //then
      expect(scope.currentUrl).toEqual('http://example.com');

      //when
      updateUrl('http://other.com');

      //then
      expect(scope.currentUrl).toEqual('http://other.com');
    });

    it('should stop watching iframe url on scope destory', function() {
      updateUrl('http://old.com');
      //Order is important o avoid mock reset because it will be called upon initialization
      spyOn(iframeMock.contents(), 'get').and.callThrough();

      //when
      destroyScope();
      updateUrl('http://new.com');

      expect(iframeMock.contents().get).not.toHaveBeenCalled();
    });

    function updateUrl(newUrl) {
      locationMock.href = newUrl;
      $interval.flush(iframeRefresh);
    }
  });

  describe('iframe height calculations', function() {
    var additionalSpacing;
    var navbarHeight;
    var expectedIframeHeight;

    beforeEach(function() {
      inject(function(viewerElementConstant) {
        additionalSpacing = viewerElementConstant.additionalSpacing;
      });

      navbarHeight = 100;
      windowMock.innerHeight = 1000;
      mockedElements['.navbar'] = { outerHeight: _.constant(navbarHeight) };
      expectedIframeHeight = windowMock.innerHeight - navbarHeight - additionalSpacing;
    });

    it('should recalculate iframe height on window resize', function() {
      var downSizeValue = 500;
      var directive = createDirective();

      //when
      triggerWindowResize(windowMock.innerHeight - downSizeValue);

      //then
      expectedIframeHeight = expectedIframeHeight - downSizeValue;
      expect(getIframeHeight(directive)).toEqual(expectedIframeHeight + 'px');
    });

    it('should calculate iframe height on directive start', function() {
      //when
      var directive = createDirective();

      //then
      expect(getIframeHeight(directive)).toEqual(expectedIframeHeight + 'px');
    });

    it('should stop watching of window size after scope destroy', function() {
      var directive = createDirective();
      var iframeHeightBeforeSizeChange = expectedIframeHeight;
      destroyScope();

      //when
      triggerWindowResize(windowMock.innerHeight - windowMock.innerHeight);

      //then
      expect(getIframeHeight(directive)).toEqual(iframeHeightBeforeSizeChange + 'px');
    });

    function triggerWindowResize(newHeight) {
      windowMock.innerHeight = newHeight;
      $(windowMock).trigger('resize');
      scope.$apply();
    }

    function getIframeHeight(directive) {
      return $(directive).attr('height');
    }
  });

  function destroyScope() {
    scope.$broadcast('$destroy');
  }

  function createDirective(options) {
    var defaultOptions = {
      url: 'http://example.com',
      currentUrl: ''
    };

    scope = _.assign(
      $rootScope.$new(),
      _.defaults(options || {}, defaultOptions));

    var directive = $compile('<viewer-element url="versionUrl" current-url="currentUrl"></viewer-element>')(scope);
    scope.$apply();
    return directive;
  }
});
