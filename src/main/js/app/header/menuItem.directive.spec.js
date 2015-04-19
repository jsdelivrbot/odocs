'use strict';

describe('menuItem.directive.spec.js', function() {
	var $compile;

	var _;
	var scope;

	var menuService;

	beforeEach(function() {
		module('docs.header', 'header/menuItem.directive.html', function($provide) {
			menuService = jasmine.createSpy('viewableDocumentations');
			menuService.isActive = jasmine.createSpy('viewableDocumentations.isActive');
			menuService.isSelected = jasmine.createSpy('viewableDocumentations.isSelected');
			$provide.value('menuService', menuService);
		});

		inject(function(_$compile_, $rootScope, ___) {
			$compile = _$compile_;
			_ = ___;
			scope = $rootScope.$new();
		});
	});

	runParametrizedTest('should add active class if item is activated',
		[[true], [false]],
		function(isActive) {
			scope.item = {id: '1'};

			//when
			menuService.isActive.and.returnValue(isActive);

			//then
			expect(createDirective().hasClass('active')).toEqual(isActive);
		});

	it('main version should be set to first versions item', function() {
		scope.item = {
			id: '1', name: 'any name',
			versions: [createVersion('main'), createVersion('other')]
		};

		//when
		var menuItem = createDirective();

		//then
		expect(menuItem.isolateScope().mainVersion().id).toEqual('main');
	});

	it('should use documentation name as item display name', function() {
		scope.item = {
			id: '1', name: 'documentation',
			version: [createVersion('main')]
		};

		var menuItem = createDirective();

		expect(menuItem.find('a:eq(0)').text().trim()).toEqual('documentation');
	});

	runParametrizedTest('should set active version in dropdown menu when is selected',
		[[true], [false]],
		function(isSelected) {
			scope.item = {
				id: '1', name: 'name',
				versions: [createVersion('selected'), createVersion('other')]
			};
			menuService.isSelected.and.callFake(function(versionId) {
				return isSelected && versionId === 'selected';
			});

			var menuItem = createDirective();

			var firstVersion = menuItem.find('ul').find('a:eq(0)');
			var secondVersion = menuItem.find('ul').find('a:eq(1)');

			expect(firstVersion.hasClass('active')).toEqual(isSelected);
			expect(secondVersion.hasClass('active')).toBeFalsy();
		});

	function createVersion(name) {
		return {id: name, name: name};
	}

	function createDirective() {
		_.defaults(scope, {
			item: {id: '1'}
		});
		var directive = $compile('<menu-item item="item"></menu-item>')(scope);
		scope.$apply();
		return directive;
	}
});
