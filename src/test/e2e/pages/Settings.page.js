'use strict';

var _ = require('lodash');

module.exports = function () {
	var that = this;

	that.get = open;
	that.addDocumentation = addDocumentation;
	that.addVersion = addVersion;
	that.waitForDownloadToFinish = waitForDownloadToFinish;
	that.openDownloadedDocumentation = openDownloadedDocumentation;

	function openDownloadedDocumentation() {
		return element(by.linkText('Open')).click();
	}

	function waitForDownloadToFinish() {
		var el = element(by.linkText('Open'));
		return browser.wait(function() {
			return el.isPresent().then(function(isPresent) {
				return isPresent;
			});
		}, 30*1000);
	}

	function addVersion(options) {
		return clickAddVersionButton()
				.then(clickSelectFeedButton)
				.then(clickSelectVersionButton)
				.then(clickDownloadButton)
				.then(clickSelectDocumentationButton);

		function clickAddVersionButton() {
			return element(by.buttonText('Add version')).click();
		}
		function clickSelectFeedButton() {
			return element(by.linkText(options.feed)).click();
		}
		function clickSelectVersionButton() {
			element(by.linkText(options.version)).click();
			return browser.sleep(100);
		}
		function clickDownloadButton() {
			return element
					.all(by.buttonText('Download'))
					.filter(function(element) {
						return element.isDisplayed();
					})
					.first()
					.click();
		}
		function clickSelectDocumentationButton() {
			return element(by.css('.modal-body'))
					.element(by.linkText(options.documentation))
					.click();
		}
	}

	function addDocumentation(name) {
		return clickAddDocumentation()
				.then(typeDocumentationName)
				.then(clickSaveButton);

		function clickAddDocumentation() { return element(by.buttonText('Add new documentation')).click(); };
		function typeDocumentationName() { element(by.model('documentationName')).sendKeys(name); };
		function clickSaveButton() { return element(by.buttonText('Save')).click(); };
	}

	function open() {
		browser.get('/#/settings/manage-documentations');
	}
};