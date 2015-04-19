'use strict';

describe('documentation.service.spec.js', function() {
	var withoutId;
	var directionUp = 'up';
	var directionDown = 'down';
	var versionId = 'versionId';
	var docId = 'docId';

	var rulesManagementUrl = '/manage/documentations/' + docId + '/versions/' + versionId + '/rules';

	var documentationService;

	var DOCS;
	var $rootScope;
	var $httpBackend;
	var apiUrl;

	beforeEach(module('docs.settings'));
	beforeEach(inject(function(_$httpBackend_, API, _DOCS_, _documentationService_, _$rootScope_) {
		documentationService = _documentationService_;
		DOCS = _DOCS_;
		$httpBackend = _$httpBackend_;
		$rootScope = _$rootScope_;
		apiUrl = API.url;
	}));

	afterEach(function() {
		$httpBackend.verifyNoOutstandingExpectation();
		$httpBackend.verifyNoOutstandingRequest();
	});

	describe('version spec', function() {
		it('should upload version file', function() {
			setupVersionFileUploadExpectation(200);

			documentationService.version.uploadFile(docId, versionId, 'file content');

			$httpBackend.flush();
		});

		it('should move version up', function() {
			setupMoveVersionExpectation(directionUp, 200);

			documentationService.version.moveUp(docId, versionId);

			$httpBackend.flush();
		});

		it('should move version down', function() {
			setupMoveVersionExpectation(directionDown, 200);

			documentationService.version.moveDown(docId, versionId);

			$httpBackend.flush();
		});

		it('should save new version', function() {
			setupNewVersionSaveExpectation(200, {
				name: 'name',
				initialDirectory: 'dir',
				rootDirectory: 'dir'
			});

			documentationService.version.save(docId, {
				name: 'name',
				initialDirectory: 'dir',
				rootDirectory: 'dir'
			});

			$httpBackend.flush();
		});

		it('should update version', function() {
			setupVersionUpdateExpectation(200, {
				id: versionId,
				name: 'name',
				initialDirectory: 'dir',
				rootDirectory: 'dir'
			});

			documentationService.version.save(docId, {
				id: versionId,
				name: 'name',
				initialDirectory: 'dir',
				rootDirectory: 'dir'
			});

			$httpBackend.flush();
		});

		it('should remove version', function() {
			setupVersionRemovalExpectation(200);

			documentationService.version.remove(docId, versionId);

			$httpBackend.flush();
		});
	});

	describe('documentation spec', function() {
		it('should list all documentations', function() {
			$httpBackend
				.expectGET(apiUrl('/manage/documentations'))
				.respond(200);

			documentationService.documentation.list();

			$httpBackend.flush();
		});

		it('should update documentation', function() {
			setupDocumentationUpdateExpectation(200, {
				id: docId,
				name: 'name'
			});

			documentationService.documentation.save(docId, {id: docId, name: 'name'});

			$httpBackend.flush();
		});

		it('should save new documentation', function() {
			setupDocumentationSaveExpectation(200, {name: 'name'});

			documentationService.documentation.save(withoutId, {name: 'name'});

			$httpBackend.flush();
		});

		it('should remove documentation', function() {
			setupDocumentationRemoveExpectation(200);

			documentationService.documentation.remove(docId);

			$httpBackend.flush();
		});

		it('should move documentation up', function() {
			setupMoveDocumentationExpectation(directionUp, 200);

			documentationService.documentation.moveUp(docId);

			$httpBackend.flush();
		});

		it('should move documentation down', function() {
			setupMoveDocumentationExpectation(directionDown, 200);

			documentationService.documentation.moveDown(docId);

			$httpBackend.flush();
		});
	});

	describe('version url rewrite rules spec', function() {
		it('should list ulr rewrite rules', function() {
			$httpBackend
				.expectGET(apiUrl(rulesManagementUrl))
				.respond(200);

			documentationService.version.listRewriteRules(docId, versionId);

			$httpBackend.flush();
		});

		it('should update rewrite rules', function() {
			function sampleRewriteRules() {
				return [
					{regexp: 'exp1', replacement: 'replacement1'},
					{regexp: 'exp2', replacement: 'replacement2'}];
			}

			setupUrlRewriteRulesUpdateExpectation(200, sampleRewriteRules());

			documentationService.version.updateRewriteRules(docId, versionId, sampleRewriteRules());

			$httpBackend.flush();
		});
	});

	describe('onDocumentationUpdate emit spec', function() {
		beforeEach(function() {
			spyOn($rootScope, '$emit').and.callThrough();
		});

		function allActionOperations(responseCode) {
			return [
				[_.partial(setupNewVersionSaveExpectation, responseCode), newVersionSave],
				[_.partial(setupVersionUpdateExpectation, responseCode), versionUpdate],
				[_.partial(setupVersionFileUploadExpectation, responseCode), versionFileUpload],
				[_.partial(setupVersionRemovalExpectation, responseCode), versionRemove],
				[_.partial(setupMoveVersionExpectation, directionUp, responseCode), versionMoveUp],
				[_.partial(setupMoveVersionExpectation, directionDown, responseCode), versionMoveDown],
				[_.partial(setupUrlRewriteRulesUpdateExpectation, responseCode), urlRewriteRulesUpdate],
				[_.partial(setupDocumentationSaveExpectation, responseCode), documentationSave],
				[_.partial(setupDocumentationUpdateExpectation, responseCode), documentationUpdate],
				[_.partial(setupDocumentationRemoveExpectation, responseCode), documentationRemove],
				[_.partial(setupMoveDocumentationExpectation, directionUp, responseCode), documentationMoveUp],
				[_.partial(setupMoveDocumentationExpectation, directionDown, responseCode), documentationMoveDown],
			];
		}

		function executeActionAndFlush(action) {
			action();
			$httpBackend.flush();
		}

		runParametrizedTest('should not emit onDocumentationChange event when httpRequestFails',
			allActionOperations(500),
			function(backendExpectation, actionExecutor) {
				backendExpectation();

				executeActionAndFlush(actionExecutor);

				expect($rootScope.$emit).not.toHaveBeenCalled();
			});

		runParametrizedTest('should emit event onDocumentationChange on modifying',
			allActionOperations(200),
			function(backendExpectation, actionExecutor) {
				backendExpectation();

				executeActionAndFlush(actionExecutor);

				expect($rootScope.$emit).toHaveBeenCalledWith(DOCS.onDocumentationUpdate);
			});

		function documentationMoveUp() {
			documentationService.documentation.moveUp(docId);
		}

		function documentationMoveDown() {
			documentationService.documentation.moveDown(docId);
		}

		function versionMoveUp() {
			documentationService.version.moveUp(docId, versionId);
		}

		function versionMoveDown() {
			documentationService.version.moveDown(docId, versionId);
		}

		function urlRewriteRulesUpdate() {
			documentationService.version.updateRewriteRules(docId, versionId, []);
		}

		function documentationRemove() {
			documentationService.documentation.remove(docId);
		}

		function documentationUpdate() {
			documentationService.documentation.save(docId, {});
		}

		function documentationSave() {
			documentationService.documentation.save(withoutId, {name: 'name'});
		}

		function versionRemove() {
			documentationService.version.remove(docId, versionId);
		}

		function versionFileUpload() {
			documentationService.version.uploadFile(docId, versionId, 'file content');
		}

		function versionUpdate() {
			documentationService.version.save(docId, {id: versionId, name: 'update'});
		}

		function newVersionSave() {
			documentationService.version.save(docId, {name: 'new version'});
		}
	});

	function setupMoveDocumentationExpectation(direction, responseCode) {
		$httpBackend
			.expectPUT(apiUrl('/manage/documentations/' + docId + '/move-' + direction))
			.respond(responseCode);
	}

	function setupMoveVersionExpectation(direction, responseCode) {
		$httpBackend
			.expectPUT(apiUrl('/manage/documentations/' + docId + '/versions/' + versionId + '/move-' + direction))
			.respond(responseCode);
	}

	function setupDocumentationSaveExpectation(responseCode, maybeRequestPayload) {
		$httpBackend
			.expectPOST(apiUrl('/manage/documentations'), maybeRequestPayload)
			.respond(responseCode);
	}

	function setupDocumentationUpdateExpectation(responseCode, maybeRequestPayload) {
		$httpBackend
			.expectPOST(apiUrl('/manage/documentations/' + docId), maybeRequestPayload)
			.respond(responseCode);
	}

	function setupNewVersionSaveExpectation(responseCode, maybeRequestPayload) {
		$httpBackend
			.expectPOST(apiUrl('/manage/documentations/' + docId + '/versions'), maybeRequestPayload)
			.respond(responseCode);
	}

	function setupVersionUpdateExpectation(responseCode, maybeRequestPayload) {
		$httpBackend
			.expectPOST(apiUrl('/manage/documentations/' + docId + '/versions/' + versionId), maybeRequestPayload)
			.respond(responseCode);
	}

	function setupDocumentationRemoveExpectation(responseCode) {
		$httpBackend
			.expectDELETE(apiUrl('/manage/documentations/' + docId))
			.respond(responseCode);
	}

	function setupVersionFileUploadExpectation(responseCode) {
		$httpBackend
			.expectPOST(apiUrl('/manage/documentations/' + docId + '/versions/' + versionId + '/file'))
			.respond(responseCode);
	}

	function setupVersionRemovalExpectation(responseCode) {
		$httpBackend
			.expectDELETE(apiUrl('/manage/documentations/' + docId + '/versions/' + versionId))
			.respond(responseCode);
	}

	function setupUrlRewriteRulesUpdateExpectation(responseCode, maybeRequestPayload) {
		$httpBackend
			.expectPOST(apiUrl(rulesManagementUrl), maybeRequestPayload)
			.respond(responseCode);
	}
});
