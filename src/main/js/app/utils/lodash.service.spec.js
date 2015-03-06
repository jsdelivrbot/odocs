'use strict';

describe('lodash.service.spec.js', function() {
  var _;

  beforeEach(function() {
    module('docs.utils');
    inject(function(___) {
      _ = ___;
    });
  });

  describe('fn spec', function() {
    it('should mixin fn function to lodash', function() {
      expect(_.fn).toBeDefined();
      expect(_.isFunction(_.fn)).toBeTruthy();
    });

    it('should wrap value in function', function() {
      var fnValue = _.fn('a');

      expect(fnValue()).toEqual('a');
    });
  });

  describe('sprintf spec', function() {
    it('should export fmt', function() {
      expect(_.fmt).toBeDefined();
    });

    it('should export fmt', function() {
      expect(_.vfmt).toBeDefined();
    });
  });

  describe('maybe spec', function() {
    var isEmpty = true;
    var notDefined;
    var nully = null;

    it('should mixin maybe function to lodash', function() {
      expect(_.maybe).toBeDefined();
      expect(_.isFunction(_.maybe)).toBeTruthy();
    });

    runParametrizedTest('should detect nullable value', [
        [notDefined, isEmpty],
        [nully, isEmpty],
        [false, !isEmpty],
        ['xx', !isEmpty]],
      function(value, isEmpty) {
        expect(_.maybe(value).isEmpty()).toEqual(isEmpty);
        expect(_.maybe(value).nonEmpty()).toEqual(!isEmpty);
      });

    var backupValue = 'bak';
    runParametrizedTest('should map value only if present', [
      [notDefined, backupValue],
      [nully, backupValue],
      [false, 'false_map'],
      ['a', 'a_map']],
      function(value, mapResult) {
        var result = _.maybe(value)
          .map(function(val) {
            return val + '_map';
          })
          .orElse(backupValue);
        expect(result).toEqual(mapResult);
    });
  });
});
