'use strict';

describe('addDocumentation.modal.controller.spec.js', function() {
  var $q;
  var scope;
  var $controller;
  var titles;

  var promiseResolver;
  var saveDeferred;
  var modalInstanceMock;
  var documentationServiceMock;

  var withoutDocumentation = { doc: null };
  var anyDocumentation = {
    doc: {
      id: 'id',
      name: 'name'
    }};

  beforeEach(module('docs.settings'));
  beforeEach(inject(function(_$controller_, _$q_, $rootScope, AddDocumentationModalConstant) {
    $q = _$q_;
    $controller = _$controller_;
    scope = $rootScope.$new();
    titles = AddDocumentationModalConstant.title;
    promiseResolver = createPromiseResolver(scope);
  }));

  beforeEach(function() {
    documentationServiceMock = {
      documentation: {
        save: jasmine.createSpy('save documentation').and.callFake(function() {
          saveDeferred = $q.defer();
          return saveDeferred.promise;
        })
      }
    };

    modalInstanceMock = createModalInstance();
  });

  describe('new documentation creation', function() {
    var withoutId;

    it('should create controller for adding new documentation', function() {
      createController(withoutDocumentation);

      expect(scope.documentationName).toEqual('');
      expect(scope.windowTitle).toEqual(titles.addDocumentation);
    });

    it('should save new documentation', function() {
      createController(withoutDocumentation);

      scope.documentationName = 'name';
      scope.save();

      expect(documentationServiceMock.documentation.save)
        .toHaveBeenCalledWith(withoutId, { name:'name' });
    });
  });

  describe('documentation edit', function() {
    it('should create controller for editing existing documentation', function() {
      createController(anyDocumentation);

      expect(scope.documentationName).toEqual('name');
      expect(scope.windowTitle).toEqual(titles.editDocumentation);
    });

    it('should update documentation', function() {
      createController(anyDocumentation);

      scope.documentationName = 'new name';
      scope.save();

      expect(documentationServiceMock.documentation.save)
        .toHaveBeenCalledWith('id', {name: 'new name'});
    });
  });

  it('should close modal instance with saved documentation', function() {
    createController(withoutDocumentation);

    scope.save();
    promiseResolver.resolve(saveDeferred, 'response');

    expect(modalInstanceMock.close).toHaveBeenCalledWith('response');
  });

  function createController(options) {
    $controller('AddDocumentationModal', {
      $scope: scope,
      $modalInstance: modalInstanceMock,
      documentationService: documentationServiceMock,
      doc: options.doc
    });
  }
});
