'use strict';

module.exports = function () {
	var that = this;

	that.get = function() {
		browser.get('/');
	};

	that.logo = function() {
		return element(by.css('.navbar-brand'));
	};
};