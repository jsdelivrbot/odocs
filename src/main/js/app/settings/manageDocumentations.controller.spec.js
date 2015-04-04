'use strict';

describe('manageDocumentations.controller.spec.js', function() {
  var scope;
  var $controller;
  var documentationServiceMock;
  var modalMock;

  var deferredHelper;

  beforeEach(module('docs.test', 'docs.settings'));
  beforeEach(inject(function(_$controller_, _$q_, _modalMock_, _deferredHelper_, $rootScope) {
    deferredHelper = _deferredHelper_;
    modalMock = _modalMock_;
    $controller = _$controller_;
    scope = $rootScope.$new();
  }));

  beforeEach(function() {
    var moveVersion = deferredHelper.createFn();
    var moveDocumentation = deferredHelper.createFn();
    documentationServiceMock = {
      documentation: {
        list: deferredHelper.createFn(),
        remove: deferredHelper.createFn(),
        moveUp: moveDocumentation,
        moveDown: moveDocumentation
      },
      version: {
        remove: deferredHelper.createFn(),
        moveUp: moveVersion,
        moveDown: moveVersion
      }
    };
  });

  it('should load documentations on controller start and collapse all', function() {
    createController([
      documentation('1', version('1'), version('2')),
      documentation('2', version('3'), version('4'))
    ]);

    _.each(scope.documentations, function(doc) {
      expect(doc.isOpen).toBeFalsy();
    });
    _.chain(scope.documentations)
      .pluck('versions')
      .flatten()
      .forEach(function(version) {
        expect(version.isOpen).toBeFalsy();
      });
  });

  it('should remove requested version and expand documentation with removed version', function() {
    var otherVersion = version('other version');
    var versionToRemove = version('version to remove');
    var doc = documentation('1', otherVersion, versionToRemove);

    createController([doc]);

    //when
    scope.removeVersion(doc, versionToRemove);
    documentationServiceMock.version.remove.deferred.resolveAndApply();
    documentationServiceMock.documentation.list.deferred.resolveAndApply([documentation('1', otherVersion)]);

    //then
    expect(scope.documentations[0].versions).toEqual([_.assign({isOpen: false}, otherVersion)]);
    expect(_.first(scope.documentations).isOpen).toBeTruthy();
  });

  it('should remove documentation', function() {
    var docToRemove = documentation('to remove');
    createController([documentation('other'), docToRemove]);

    //when
    scope.removeDocumentation(docToRemove);
    documentationServiceMock.documentation.remove.deferred.resolveAndApply();
    documentationServiceMock.documentation.list.deferred.resolveAndApply([documentation('other')]);

    //then
    expect(_.first(scope.documentations).isOpen).toBeFalsy();
  });

  it('should edit documentation and expand edited after edition is done', function() {
    var doc = documentation('1');
    createController([doc]);

    //when
    scope.editDocumentation(doc);
    modalMock.openDeferred().resolveAndApply(doc);
    documentationServiceMock.documentation.list.deferred.resolveAndApply([doc]);

    //then
    expect(_.first(scope.documentations).isOpen).toBeTruthy();
  });

  it('should edit version and expand edited after edition is done', function() {
    var toEdit = version(1);
    var doc = documentation('1', toEdit);
    createController([doc]);

    //when
    scope.editVersion(doc, toEdit);
    modalMock.openDeferred().resolveAndApply(toEdit);
    documentationServiceMock.documentation.list.deferred.resolveAndApply([doc]);

    //then
    var editDoc = _.first(scope.documentations);
    expect(editDoc.isOpen).toBeTruthy();
    expect(_.first(editDoc.versions).isOpen).toBeTruthy();
  });

  describe('version move spec', function() {
    var firstVersion;
    var secondVersion;
    var doc;

    beforeEach(function() {
      firstVersion = version('1');
      secondVersion = version('2');
      doc = documentation('1', firstVersion, secondVersion);

      createController([doc]);
    });

    it('should disable move up and down button if version is first and last', function() {
      expect(scope.canMoveVersionUp(doc, firstVersion)).toBeFalsy();
      expect(scope.canMoveVersionUp(doc, secondVersion)).toBeTruthy();

      expect(scope.canMoveVersionDown(doc, secondVersion)).toBeFalsy();
      expect(scope.canMoveVersionDown(doc, firstVersion)).toBeTruthy();
    });

    runParametrizedTest(
      'should move version up/down and open moved version',
      [
        ['moveVersionUp', 'moveUp', function() { return secondVersion; } ],
        ['moveVersionDown', 'moveDown', function() { return firstVersion; } ]
      ],
      function(moveFnName, serviceFnName, versionProvider) {
        var versionToMove = versionProvider();

        //when
        scope[moveFnName](doc, versionToMove);
        documentationServiceMock.version[serviceFnName].deferred.resolveAndApply();
        documentationServiceMock.documentation.list.deferred.resolveAndApply([doc]);

        expect(doc.isOpen).toBeTruthy();
        expect(versionToMove.isOpen).toBeTruthy();
        expect(documentationServiceMock.version[serviceFnName]).toHaveBeenCalledWith(doc.id, versionToMove.id);
        expect(documentationServiceMock.documentation.list).toHaveBeenCalled();
      });
  });

  describe('documentation move spec', function() {
    var firstDoc;
    var secondDoc;

    beforeEach(function() {
      firstDoc = documentation('1');
      secondDoc = documentation('2');

      createController([firstDoc, secondDoc]);
    });

    it('should disable move up and down button if version is first and last', function() {
      expect(scope.canMoveDocumentationUp(firstDoc)).toBeFalsy();
      expect(scope.canMoveDocumentationUp(secondDoc)).toBeTruthy();

      expect(scope.canMoveDocumentationDown(secondDoc)).toBeFalsy();
      expect(scope.canMoveDocumentationDown(firstDoc)).toBeTruthy();
    });

    runParametrizedTest(
      'should move documentation up/down and open moved documentation',
      [
        ['moveDocumentationUp', 'moveUp', function() { return secondDoc; } ],
        ['moveDocumentationDown', 'moveDown', function() { return firstDoc; } ]
      ],
      function(moveFnName, serviceFnName, documentationProvider) {
        var documentationToMove = documentationProvider();

        //when
        scope[moveFnName](documentationToMove);
        documentationServiceMock.documentation[serviceFnName].deferred.resolveAndApply();
        documentationServiceMock.documentation.list.deferred.resolveAndApply([firstDoc, secondDoc]);

        expect(documentationToMove.isOpen).toBeTruthy();
        expect(documentationServiceMock.documentation[serviceFnName]).toHaveBeenCalledWith(documentationToMove.id);
        expect(documentationServiceMock.documentation.list).toHaveBeenCalled();
      });
  });

  function createController(maybeDocumentationList) {
    $controller('ManageDocumentations', {
      $scope: scope,
      $modal: modalMock.instance,
      documentationService: documentationServiceMock
    });

    if(maybeDocumentationList) {
      documentationServiceMock.documentation.list.deferred.resolveAndApply(maybeDocumentationList);
    }
  }

  function documentation() {
    return {
      id: _.first(arguments),
      versions: _.rest(arguments)
    };
  }
  function version(id) {
    return { id: id };
  }
});
