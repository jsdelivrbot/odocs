exports.config = {
	framework: 'jasmine2',

	seleniumServerJar: 'node_modules/grunt-protractor-runner/node_modules/protractor/selenium/selenium-server-standalone-2.45.0.jar',
	chromeDriver: 'node_modules/grunt-protractor-runner/node_modules/protractor/selenium/chromedriver',
	capabilities: {
		'browserName': 'chrome',
		'chromeOptions': {
			'args': ['no-sandbox']
		}
	},

	specs: [
		'../../test/e2e/**/*spec.js'
	]
};
