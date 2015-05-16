'use strict';
var pages = require('./pages/pages.js');

describe('Main page spec', function() {
	it('should open main page', function() {
		pages.mainPage.get();

		expect(pages.mainPage.logo().getText()).toEqual('Docs');
	});
});