'use strict';

describe('selectDocumentation.modal.controller.spec.js', function() {
  var documentationServiceMock;
  var modalInstanceMock;

  var $controller;

  var scope;

  var deferredHelper;
  var documentationListDeferred;
  var documentationSaveDeferred;

  beforeEach(function() {
    documentationServiceMock = {
      documentation: {
        list: function() {
          documentationListDeferred = deferredHelper.create();
          return documentationListDeferred.promise;
        },
        save: function() {
          documentationSaveDeferred = deferredHelper.create();
          return documentationSaveDeferred.promise;
        }
      }
    };

    module('docs.test', 'docs.settings', function($provide) {
      $provide.value('documentationService', documentationServiceMock);
    });

    inject(function($rootScope, _$controller_, _$q_, modalInstanceMockFactory, _deferredHelper_) {
      $controller = _$controller_;
      scope = $rootScope.$new();
      deferredHelper = _deferredHelper_;
      modalInstanceMock = modalInstanceMockFactory();
    })
  });

  it('should list documentations', function() {
    //when
    createController();
    documentationListDeferred.resolveAndApply(['1', '2']);

    //then
    expect(scope.documentations).toEqual(['1', '2']);
  });

  it('should select user documentation as modal result', function() {
    var documentation = {id: '1', name: '1'};
    createController();
    documentationListDeferred.resolveAndApply([documentation]);

    //when
    scope.select(documentation);

    //then
    expect(modalInstanceMock.close).toHaveBeenCalledWith(documentation);
  });

  it('should save new documentation', function() {
    createController();

    //when
    scope.newDocumentationName = 'new doc';
    scope.saveNewDocumentation();
    documentationSaveDeferred.resolveAndApply({id: '1'});

    //then
    expect(modalInstanceMock.close).toHaveBeenCalledWith({id: '1'});
  });

  function createController() {
    $controller('SelectDocumentationModal', {
      $scope: scope,
      documentationService: documentationServiceMock,
      $modalInstance: modalInstanceMock
    });
  }
});
