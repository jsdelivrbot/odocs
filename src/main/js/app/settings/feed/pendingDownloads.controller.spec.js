'use strict';

describe('pendingDownloads.controller.spec.js', function() {
	var $controller;
	var deferredHelper;

	var scope;
	var downloadsServiceMock;

	beforeEach(function() {
		module('docs.test', 'docs.settings');

		inject(function(_$controller_, $rootScope, _$q_, _deferredHelper_) {
			$controller = _$controller_;
			scope = $rootScope.$new();
			deferredHelper = _deferredHelper_;
		});

		downloadsServiceMock = {
			downloadingItems: deferredHelper.createFn(),
			requestRemove: deferredHelper.createFn(),
			requestAbort: deferredHelper.createFn()
		};
	});

	it('should list downloading items on controller initialize', function() {
		var downloadingItems = ['item1', 'item2'];
		createController(downloadingItems);

		expect(scope.downloadingItems).toEqual(downloadingItems);
	});

	var pending = true;
	runParametrizedTest(
		'should modify item state',
		[
			[!pending, 'requestRemove'],
			[pending, 'requestAbort']
		],
		function(isPending, serviceFn) {
			var toModifyItemId = 'toModify';
			var itemToRemove = {id: toModifyItemId, isPending: _.constant(isPending)};
			var otherItem = {id: 'other'};
			createController([itemToRemove, otherItem]);

			//when
			scope.removeItem(itemToRemove);

			//then
			expect(downloadsServiceMock[serviceFn]).toHaveBeenCalledWith(toModifyItemId);
		});
	it('should remove item if not pending', function() {
		var itemToRemove = {id: 'toRemove', isPending: _.constant(false)};
		var otherItem = {id: 'other'};
		createController([itemToRemove, otherItem]);

		//when
		scope.removeItem(itemToRemove);

		//then
		expect(downloadsServiceMock.requestRemove).toHaveBeenCalledWith('toRemove');
	});

	it('should abort item if is pending', function() {
		var itemToRemove = {id: 'toAbort', isPending: _.constant(true)};
		var otherItem = {id: 'other'};
		createController([itemToRemove, otherItem]);

		//when
		scope.removeItem(itemToRemove);

		//then
		expect(downloadsServiceMock.requestAbort).toHaveBeenCalledWith('toAbort');
	});

	function createController(maybeItems) {
		$controller('PendingDownloads', {
			$scope: scope,
			downloadsService: downloadsServiceMock
		});

		if(maybeItems) {
			downloadsServiceMock.downloadingItems.deferred.resolveAndApply(maybeItems);
		}
	}
});
