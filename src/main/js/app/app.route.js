'use strict';

angular
	.module('docs')
	.config(function($stateProvider, $urlRouterProvider) {
		$stateProvider
			.state('root', {
				abstract: true,
				templateUrl: 'header/header.controller.html',
				controller: 'Header'
			})
			.state('root.home', {
				url: '/',
				template: 'HOME',
				controller: angular.noop
			})
			.state('root.viewerRoot', { //this state is actually responsible for rendering viewer
				url: '/view/:versionId',
				controller: 'Viewer',
				abstract: true,
				templateUrl: 'viewer/viewer.controller.html'
			})
			.state('viewer', {  //this state is necessary to change url in browser url without reloading whole page
				parent: 'root.viewerRoot',
				url: '?url',
				controller: angular.noop
			})
			.state('root.settings', {
				url: '/settings',
				controller: angular.noop,
				abstract: true,
				template: '<div ui-view></div>'
			})
			.state('root.settings.manageDocumentationsTemplate', {
				controller: 'ManageDocumentations',
				templateUrl: 'settings/manageDocumentations.controller.html'
			})
			.state('manageDocumentations', {
				parent: 'root.settings.manageDocumentationsTemplate',
				url: '/manage-documentations',
				views: {
					pendingDownloads: {
						controller: 'PendingDownloads',
						templateUrl: 'settings/feed/pendingDownloads.controller.html'
					}
				}
			});

		$urlRouterProvider.otherwise('/');
	});
