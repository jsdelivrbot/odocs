function xrunParametrizedTest() {}

function runParametrizedTest(testNameGenerator, testData, testFunction) {
  _.each(testData, function(param) {
    var passParamsAsArray = _.isArray(param);

    var testName = _.isFunction(testNameGenerator)
      ? passParamsAsArray ? testNameGenerator.apply(this, param) : testNameGenerator(param)
      : testNameGenerator;

    var testMethod = passParamsAsArray
      ? function() { testFunction.apply(this, param) }
      : _.partial(testFunction, param);

    it(testName, testMethod);
  });
}
