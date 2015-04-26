'use strict';
var MainPage = require('./pages/Main.page.js');

describe('Main page spec', function() {
	it('should open main page', function() {
		var mainPage = new MainPage();
		mainPage.get();

		expect(mainPage.logo().getText()).toEqual('Docs');
	});
});