function xrunParametrizedTest() {}

function frunParametrizedTest(testNameGenerator, testData, testFunction) {
  _runParametrizedTest(fit, testNameGenerator, testData, testFunction);
}

function runParametrizedTest(testNameGenerator, testData, testFunction) {
  _runParametrizedTest(it, testNameGenerator, testData, testFunction);
}

function _runParametrizedTest(testFn, testNameGenerator, testData, testFunction) {
  _.each(testData, function(param) {
    var passParamsAsArray = _.isArray(param);

    var testName = _.isFunction(testNameGenerator)
      ? passParamsAsArray ? testNameGenerator.apply(this, param) : testNameGenerator(param)
      : testNameGenerator;

    var testMethod = passParamsAsArray
      ? function() { testFunction.apply(this, param) }
      : _.partial(testFunction, param);

    testFn(testName, testMethod);
  });
}
