'use strict';

var pages = require('./pages/pages.js');
var _ = require('lodash');

describe('Settings page spec', function() {

	beforeAll(function(done) {
		pages.settingsPage.get();
		pages.settingsPage.addDocumentation('protractor')
				.then(function() {
					return pages.settingsPage.addVersion({
						documentation: 'protractor',
						feed: 'Test feeds',
						version: 'Dynamic page with html5 mode urls'
					});
				})
				.then(pages.settingsPage.waitForDownloadToFinish)
				.then(function() {
					done();
				});
	});

	it('should open downloaded documentation using open link from progress monitor', function(done) {
		pages.settingsPage.get();

		pages.settingsPage.openDownloadedDocumentation()
				.then(pages.viewerPage.selectViewer)
				.then(function() {
					return element(by.css('.content')).getText()
				})
				.then(function(text) {
					expect(text).toEqual('Index');
					done();
				});
	});
});