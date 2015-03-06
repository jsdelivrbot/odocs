'use strict';

describe('addVersion.modal.controller.spec.js', function() {
  var _;
  var $q;
  var scope;
  var $controller;
  var titles;

  var promiseResolver;
  var saveDeferred;
  var uploadDeferred;
  var rewriteRulesListDeferred;
  var rewriteRulesSaveDeferred;
  var modalInstanceMock;
  var documentationServiceMock;

  var anyDoc = {id: 'id', name: 'doc'};
  var withoutVersion;
  var anyVersion = {
    id: 'id',
    name: 'name',
    rootDirectory: 'root',
    initialDirectory: 'initial'
  };

  beforeEach(module('docs.settings'));
  beforeEach(inject(function(_$controller_, _$q_, $rootScope, AddVersionModalConstant, ___) {
    _ = ___;
    $q = _$q_;
    $controller = _$controller_;
    scope = $rootScope.$new();
    titles = AddVersionModalConstant.title;
    promiseResolver = createPromiseResolver(scope);
  }));

  beforeEach(function() {
    documentationServiceMock = {
      version: {
        uploadFile: jasmine.createSpy('upload version file').and.callFake(function() {
          uploadDeferred = $q.defer();
          return uploadDeferred.promise;
        }),
        save: jasmine.createSpy('save version').and.callFake(function() {
          saveDeferred = $q.defer();
          return saveDeferred.promise;
        }),
        listRewriteRules: jasmine.createSpy('list rewrite rules').and.callFake(function() {
          rewriteRulesListDeferred = $q.defer();
          return rewriteRulesListDeferred.promise;
        }),
        updateRewriteRules: jasmine.createSpy('save rewrite rules').and.callFake(function() {
          rewriteRulesSaveDeferred = $q.defer();
          return rewriteRulesSaveDeferred.promise;
        })
      }
    };

    modalInstanceMock = createModalInstance();
  });

  describe('save spec', function() {
    it('should save version file if present and close modal with saved version', function() {
      createController({
        doc: anyDoc,
        version: anyVersion
      });

      scope.version.files = ['file'];
      scope.save();
      promiseResolver.resolve(saveDeferred, 'first response');
      promiseResolver.resolve(uploadDeferred);

      expect(modalInstanceMock.close).toHaveBeenCalledWith('first response');
    });

    it('should not upload files if no new were set', function() {
      createController({
        doc: anyDoc,
        version: anyVersion
      });

      scope.save();
      promiseResolver.resolve(saveDeferred, 'response');

      expect(documentationServiceMock.version.save).toHaveBeenCalled();
      expect(documentationServiceMock.version.uploadFile).not.toHaveBeenCalled();
      expect(modalInstanceMock.close).toHaveBeenCalledWith('response');
    });

    it('should save url rewrite rules and close modal with saved version file', function() {
      createController({
        doc: anyDoc,
        version: anyVersion
      });
      scope.urlRewriteRules = ['rule1', 'rule2'];

      scope.save();
      promiseResolver.resolve(saveDeferred, 'saved version');
      promiseResolver.resolve(rewriteRulesSaveDeferred);

      expect(documentationServiceMock.version.save).toHaveBeenCalled();
      expect(documentationServiceMock.version.updateRewriteRules).toHaveBeenCalled();
      expect(modalInstanceMock.close).toHaveBeenCalledWith('saved version');
    });
  });

  describe('url rewrite rules', function() {
    var rule1;
    var rule2;

    beforeEach(function() {
      rule1 = createRewriteRule('1');
      rule2 = createRewriteRule('2');
    });

    it('should list url rewrite rules', function() {
      createControllerWithRules(rule1);

      expect(scope.urlRewriteRules).toEqual([rule1]);
      expect(documentationServiceMock.version.listRewriteRules).toHaveBeenCalledWith(anyDoc.id, anyVersion.id);
    });

    it('should move selected rule up', function() {
      createControllerWithRules(rule1, rule2);

      //when
      scope.moveRuleUp(rule2);
      //then
      expect(scope.urlRewriteRules).toEqual([rule2, rule1]);

      //when
      scope.moveRuleUp(rule1);
      //then
      expect(scope.urlRewriteRules).toEqual([rule1, rule2]);
    });

    it('should move selected rule down', function() {
      createControllerWithRules(rule1, rule2);

      //when
      scope.moveRuleDown(rule1);
      //then
      expect(scope.urlRewriteRules).toEqual([rule2, rule1]);

      //when
      scope.moveRuleDown(rule2);
      //then
      expect(scope.urlRewriteRules).toEqual([rule1, rule2]);
    });

    it('should be impossible to move rule if is first', function() {
      createControllerWithRules(rule1, rule2);

      expect(scope.isFirst(rule1)).toBeTruthy();
      expect(scope.isFirst(rule2)).toBeFalsy();
    });

    it('should be impossible to move rule down if is last', function() {
      createControllerWithRules(rule1, rule2);

      expect(scope.isLast(rule2)).toBeTruthy();
      expect(scope.isLast(rule1)).toBeFalsy();
    });

    it('should remove rule', function() {
      createControllerWithRules(rule1, rule2);

      //when
      scope.removeRule(rule1);
      //then
      expect(scope.urlRewriteRules).toEqual([rule2]);

      //when
      scope.removeRule(rule2);
      //then
      expect(scope.urlRewriteRules).toEqual([]);
    });

    it('should save rules only if changed', function() {
      createControllerWithRules(rule1, rule2);

      //when
      scope.save();
      promiseResolver.resolve(saveDeferred, 'response');

      //then
      expect(documentationServiceMock.version.updateRewriteRules).not.toHaveBeenCalled();

      //when
      scope.moveRuleDown(rule1);
      scope.save();
      promiseResolver.resolve(saveDeferred, 'response');

      //then
      expect(documentationServiceMock.version.updateRewriteRules).toHaveBeenCalled();
    });

    function createControllerWithRules() {
      createController({
        doc: anyDoc,
        version: anyVersion
      });
      promiseResolver.resolve(rewriteRulesListDeferred, _.flatten([arguments]));
    }

    function createRewriteRule(val) {
      return {
        regexp: val,
        replacement: val
      };
    }
  });

  describe('new version creation', function() {
    var withoutId;

    it('should create controller for adding new documentation', function() {
      createController({
        doc: anyDoc,
        version: withoutVersion
      });

      expect(scope.windowTitle).toEqual(titles.addVersion);
      expect(scope.version).toEqual({
        id: withoutId,
        name: '',
        rootDirectory: '',
        initialDirectory: '',
        files: []
      });
    });

    it('should save new version with files', function() {
      var savedVersion = { id: 'savedId'};

      createController({
        doc: anyDoc,
        version: withoutVersion
      });

      scope.version.name = 'version';
      scope.version.rootDirectory = 'dir';
      scope.version.initialDirectory = 'dir';
      scope.version.files = ['file'];
      scope.save();

      expect(documentationServiceMock.version.save)
        .toHaveBeenCalledWith(anyDoc.id, {
          id: withoutId,
          name: 'version',
          rootDirectory: 'dir',
          initialDirectory: 'dir'
        });
      promiseResolver.resolve(saveDeferred, savedVersion);

      expect(documentationServiceMock.version.uploadFile)
        .toHaveBeenCalledWith(anyDoc.id, savedVersion.id, 'file');
    });
  });

  describe('documentation edit', function() {
    it('should create controller for editing existing documentation', function() {
      createController({
        doc: anyDoc,
        version: anyVersion
      });

      expect(scope.windowTitle).toEqual(titles.editVersion);
      expect(scope.version).toEqual({
        id: anyVersion.id,
        name: anyVersion.name,
        initialDirectory: anyVersion.initialDirectory,
        rootDirectory: anyVersion.rootDirectory,
        files: []
      });
    });

    it('should update version with new content and files', function() {
      createController({
        doc: anyDoc,
        version: anyVersion
      });

      scope.version.name = 'new name';
      scope.version.rootDirectory = 'new dir';
      scope.version.initialDirectory = 'new dir';
      scope.version.files = ['file'];
      scope.save();

      expect(documentationServiceMock.version.save)
        .toHaveBeenCalledWith(anyDoc.id, {
          id: anyVersion.id,
          name: 'new name',
          rootDirectory: 'new dir',
          initialDirectory: 'new dir'
        });
      promiseResolver.resolve(saveDeferred, anyVersion);

      expect(documentationServiceMock.version.uploadFile)
        .toHaveBeenCalledWith(anyDoc.id, anyVersion.id, 'file');
    });
  });

  function createController(options) {
    $controller('AddVersionModal', {
      $scope: scope,
      $modalInstance: modalInstanceMock,
      documentationService: documentationServiceMock,
      doc: options.doc,
      version: options.version
    });
  }
});
