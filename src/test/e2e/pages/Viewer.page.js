'use strict';

module.exports = function() {
	var that = this;

	that.selectViewer = selectViewer;

	function selectViewer() {
		return browser.driver.switchTo().frame(element(by.css('iframe')).getWebElement());
	}
};
