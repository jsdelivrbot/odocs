'use strict';

var MainPage = require('./Main.page.js');
var SettingsPage = require('./Settings.page.js');
var ViewerPage = require('./Viewer.page.js');

module.exports = {
	mainPage: new MainPage(),
	settingsPage: new SettingsPage(),
	viewerPage: new ViewerPage()
};
