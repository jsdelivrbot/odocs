'use strict';

describe('addVersion.modal.controller.spec.js', function() {
	var _;
	var scope;
	var $controller;
	var titles;

	var deferredHelper;
	var modalInstanceMock;
	var versionServiceMock;

	var anyDoc = {id: 'id', name: 'doc'};
	var withoutVersion;
	var anyVersion = {
		id: 'id',
		name: 'name',
		rootDirectory: 'root',
		initialDirectory: 'initial'
	};

	beforeEach(module('docs.test', 'docs.settings'));
	beforeEach(inject(function(_$controller_, _$q_, $rootScope, modalInstanceMockFactory, _deferredHelper_, AddVersionModalConstant, ___) {
		_ = ___;
		$controller = _$controller_;
		scope = $rootScope.$new();
		titles = AddVersionModalConstant.title;
		deferredHelper = _deferredHelper_;
		modalInstanceMock = modalInstanceMockFactory();
	}));

	beforeEach(function() {
		versionServiceMock = {
			uploadFile: deferredHelper.createFn(),
			save: deferredHelper.createFn(),
			listRewriteRules: deferredHelper.createFn(),
			updateRewriteRules: deferredHelper.createFn()
		};
	});

	describe('save spec', function() {
		it('should save version file if present and close modal with saved version', function() {
			createController({
				doc: anyDoc,
				version: anyVersion
			});

			scope.version.files = ['file'];
			scope.save();
			versionServiceMock.save.deferred.resolveAndApply('first response');
			versionServiceMock.uploadFile.deferred.resolveAndApply();

			expect(modalInstanceMock.close).toHaveBeenCalledWith('first response');
		});

		it('should not upload files if no new were set', function() {
			createController({
				doc: anyDoc,
				version: anyVersion
			});

			scope.save();
			versionServiceMock.save.deferred.resolveAndApply('response');

			expect(versionServiceMock.save).toHaveBeenCalled();
			expect(versionServiceMock.uploadFile).not.toHaveBeenCalled();
			expect(modalInstanceMock.close).toHaveBeenCalledWith('response');
		});

		it('should save url rewrite rules and close modal with saved version file', function() {
			createController({
				doc: anyDoc,
				version: anyVersion
			});
			scope.urlRewriteRules = ['rule1', 'rule2'];

			scope.save();
			versionServiceMock.save.deferred.resolveAndApply('saved version');
			versionServiceMock.updateRewriteRules.deferred.resolveAndApply();

			expect(versionServiceMock.save).toHaveBeenCalled();
			expect(versionServiceMock.updateRewriteRules).toHaveBeenCalled();
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
			expect(versionServiceMock.listRewriteRules).toHaveBeenCalledWith(anyDoc.id, anyVersion.id);
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
			versionServiceMock.save.deferred.resolveAndApply('response');

			//then
			expect(versionServiceMock.updateRewriteRules).not.toHaveBeenCalled();

			//when
			scope.moveRuleDown(rule1);
			scope.save();
			versionServiceMock.save.deferred.resolveAndApply('response');

			//then
			expect(versionServiceMock.updateRewriteRules).toHaveBeenCalled();
		});

		function createControllerWithRules() {
			createController({
				doc: anyDoc,
				version: anyVersion
			});
			versionServiceMock.listRewriteRules.deferred.resolveAndApply(_.flatten([arguments]));
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
			var savedVersion = {id: 'savedId'};

			createController({
				doc: anyDoc,
				version: withoutVersion
			});

			scope.version.name = 'version';
			scope.version.rootDirectory = 'dir';
			scope.version.initialDirectory = 'dir';
			scope.version.files = ['file'];
			scope.save();

			expect(versionServiceMock.save)
				.toHaveBeenCalledWith(anyDoc.id, {
					id: withoutId,
					name: 'version',
					rootDirectory: 'dir',
					initialDirectory: 'dir'
				});
			versionServiceMock.save.deferred.resolveAndApply(savedVersion);

			expect(versionServiceMock.uploadFile)
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

			expect(versionServiceMock.save)
				.toHaveBeenCalledWith(anyDoc.id, {
					id: anyVersion.id,
					name: 'new name',
					rootDirectory: 'new dir',
					initialDirectory: 'new dir'
				});
			versionServiceMock.save.deferred.resolveAndApply(anyVersion);

			expect(versionServiceMock.uploadFile)
				.toHaveBeenCalledWith(anyDoc.id, anyVersion.id, 'file');
		});
	});

	function createController(options) {
		var documentationServiceMock = {
			version: versionServiceMock
		};
		$controller('AddVersionModal', {
			$scope: scope,
			$modalInstance: modalInstanceMock,
			documentationService: documentationServiceMock,
			doc: options.doc,
			version: options.version
		});
	}
});
