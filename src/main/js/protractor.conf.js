exports.config = {
	seleniumAddress: 'http://localhost:4444/wd/hub',
	seleniumServerJar: 'node_modules/grunt-protractor-runner/node_modules/protractor/selenium/selenium-server-standalone-2.45.0.jar',
	chromeDriver: 'node_modules/grunt-protractor-runner/node_modules/protractor/selenium/chromedriver',
	specs: [
		'../../test/e2e/**/*spec.js'
	]
};
