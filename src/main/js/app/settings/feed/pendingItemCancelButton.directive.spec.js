'use strict';

describe('pendingItemCancelButton.directive.spec.js', function() {
  var pending = true;
  var finished = true;

  var $compile;
  var scope;
  var _;

  var button;
  var buttonDirective;

  beforeEach(function() {
    module('docs.settings', 'settings/feed/pendingItemCancelButton.directive.html');

    inject(function(_$compile_, $rootScope, ___) {
      $compile = _$compile_;
      scope = $rootScope.$new();
      _ = ___;
    });
  });

  runParametrizedTest(
    'should set valid class for action type',
    [
      ['cancel', pending, !finished, 'btn-danger'],
      ['remove', !pending, finished, 'btn-warning']
    ],
    function(type, pending, finished, expectedClass) {
      createDirective({
        item: createItem(_.constant(pending), _.constant(finished)),
        type: type
      });

      expect(button).toHaveClass(expectedClass);
    });

  runParametrizedTest(
    'should set valid item text for action type',
    [
      ['cancel', pending, !finished, 'Cancel'],
      ['remove', !pending, finished, 'Remove']
    ],
    function(type, pending, finished, expectedText) {
      createDirective({
        item: createItem(_.constant(pending), _.constant(finished)),
        type: type
      });

      expect(button).toHaveText(expectedText);
    });

  runParametrizedTest(
    'should show spinner if action is pending',
    [
      [ {abortRequested: true}, 'cancel', pending, !finished],
      [ {removeRequested: true}, 'remove', !pending, finished],
    ],
    function(itemProperties, type, pending, finished) {
      var item = _.assign(
        createItem(_.constant(pending), _.constant(finished)),
        itemProperties);
      createDirective({
        item: item,
        type: type
      });

      expect(spinner()).toBeNgVisible();
      expect(trashIcon()).toBeNgHidden();
    });

  runParametrizedTest(
    'should show trash icon if action is not pending',
    [
      [ {abortRequested: false}, 'cancel', pending, !finished],
      [ {removeRequested: false}, 'remove', !pending, finished],
    ],
    function(itemProperties, type, pending, finished) {
      var item = _.assign(
        createItem(_.constant(pending), _.constant(finished)),
        itemProperties);
      createDirective({
        item: item,
        type: type
      });

      expect(trashIcon()).toBeNgVisible();
      expect(spinner()).toBeNgHidden();
    });

  it('should trigger onRemove action with item as param', function() {
    var onRemove = jasmine.createSpy('onRemove');
    var item = pendingItem();
    createDirective({
      item: item,
      type: 'cancel',
      onRemove: onRemove
    });

    //when
    button.click();

    //then
    expect(onRemove).toHaveBeenCalledWith(item);
  });

  it('should hide cancel button if item finished', function() {
    createDirective({
      item: finishedItem(),
      type: 'cancel'
    });

    expect(button).toBeNgHidden();
  });

  it('should hide remove button if item is pending', function() {
    createDirective({
      item: pendingItem(),
      type: 'remove'
    });

    expect(button).toBeNgHidden();
  });

  function createDirective(options) {
    var defaultOptions = {
      type: 'cancel',
      item: pendingItem,
      onRemove: angular.noop
    };

    _.assign(scope, _.defaults(options || {}, defaultOptions));

    var html = '<pending-item-cancel-button type="' + scope.type + '" item="item" on-remove="onRemove(item)"></pending-item-cancel-button>';
    buttonDirective = $compile(html)(scope);
    scope.$apply();

    button = buttonDirective.find('button');
    return buttonDirective;
  }

  function trashIcon() {
    return buttonDirective.find('.glyphicon-trash');
  }
  function spinner() {
    return buttonDirective.find('span.glyphicon-spin');
  }

  function pendingItem() {
    return createItem(_.constant(pending), _.constant(!finished));
  }

  function finishedItem() {
    return createItem(_.constant(!pending), _.constant(finished));
  }

  function createItem(pendingFn, finishedFn) {
    return {
      isPending: pendingFn,
      isFinished: finishedFn
    };
  }
});
