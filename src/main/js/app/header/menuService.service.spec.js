'use strict';

describe('menuService.service.spec.js', function() {
	var _;
	var $httpBackend;
	var $rootScope;

	var DOCS;
	var apiUrl;

	var menuService;

	beforeEach(function() {
		module('docs.header');
		inject(function(_$httpBackend_, _$rootScope_, API, _DOCS_, _menuService_, ___) {
			_ = ___;
			$httpBackend = _$httpBackend_;
			$rootScope = _$rootScope_;
			DOCS = _DOCS_;
			menuService = _menuService_;
			apiUrl = _.partial(API.url, '/menu/documentations');
		});
	});

	afterEach(function() {
		$httpBackend.verifyNoOutstandingExpectation();
		$httpBackend.verifyNoOutstandingRequest();
	});

	it('should return documentations', function() {
		function fetchData() {
			menuService().then(function(docs) {
				expect(docs).toEqual(sampleResponse());
			});
		}

		$httpBackend.expectGET(apiUrl()).respond(200, sampleResponse());

		fetchData();
		fetchData();  //second request should be ignored and served from cache

		$httpBackend.flush();
	});

	it('should set activeItem onVersionSelect event', function() {
		initializeService();
		selectVersion('v1');

		expect(menuService.isSelected('v1')).toBeTruthy();
	});

	it('should reload documentation list onDocumentationUpdate', function() {
		initializeService();
		$httpBackend
			.expectGET(apiUrl())
			.respond(200, sampleResponse());

		//when
		documentationUpdate();

		//then
		$httpBackend.flush();
	});

	it('should update documentations list without changing array reference', function() {
		initializeService();
		var itemList;
		menuService().then(function(docs) {
			itemList = docs;
		});

		$httpBackend
			.expectGET(apiUrl())
			.respond(200, ['new item']);
		documentationUpdate();
		$httpBackend.flush();

		menuService().then(function(newDocs) {
			expect(newDocs).toEqual(['new item']);
			expect(newDocs === itemList);
		});
	});

	it('should detect active documentation by version id', function() {
		var selectedDocumentation = sampleResponse()[1];
		var otherDocumentation = sampleResponse()[0];
		initializeService();
		selectVersion('v2');

		expect(menuService.isActive(selectedDocumentation)).toBeTruthy();
		expect(menuService.isActive(otherDocumentation)).toBeFalsy();
	});

	it('should deactive selected version', function() {
		initializeService();
		selectVersion('v1');

		menuService.deactivateAll();

		_.each(sampleResponse(), function(doc) {
			expect(menuService.isActive(doc)).toBeFalsy();
		});
	});

	function documentationUpdate() {
		$rootScope.$emit(DOCS.onDocumentationUpdate);
	}

	function selectVersion(versionId) {
		$rootScope.$emit(DOCS.onVersionSelect, versionId);
	}

	function initializeService() {
		$httpBackend
			.expectGET(apiUrl())
			.respond(200, sampleResponse());
		menuService();
		$httpBackend.flush();
	}

	function createVersion(name) {
		return {id: name, name: name};
	}

	function sampleResponse() {
		return [
			{
				id: '1', name: 'doc1',
				versions: [createVersion('v1')]
			}, {
				id: '2', name: 'doc2',
				versions: [createVersion('v2')]
			}];
	}
});
