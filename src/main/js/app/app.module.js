'use strict';

angular
  .module('docs', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngTouch',
    'ui.bootstrap',
    'ui.router',

    'docs.header',
    'docs.settings',
    'docs.viewer'
  ]);
