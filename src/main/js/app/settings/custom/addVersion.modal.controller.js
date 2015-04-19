'use strict';

angular
	.module('docs.settings')
	.constant('AddVersionModalConstant', {
		title: {
			addVersion: 'Add version to documentation',
			editVersion: 'Edit documentation version'
		}
	})
	.controller('AddVersionModal', function($scope, $modalInstance, AddVersionModalConstant, _, documentationService, doc, version) {
		$scope.cancel = $modalInstance.dismiss;
		var oldVersion = version || {};
		var originalRules = [];
		$scope.version = {
			id: oldVersion.id,
			name: oldVersion.name || '',
			initialDirectory: oldVersion.initialDirectory || '',
			rootDirectory: oldVersion.rootDirectory || '',
			files: []
		};
		$scope.save = save;
		$scope.windowTitle = oldVersion.id
			? AddVersionModalConstant.title.editVersion
			: AddVersionModalConstant.title.addVersion;

		$scope.urlRewriteRules = [];
		$scope.addUrlRewriteRule = function() {
			$scope.urlRewriteRules.push({});
		};
		$scope.moveRuleUp = _.partial(moveRule, -1);
		$scope.isFirst = isFirst;
		$scope.moveRuleDown = _.partial(moveRule, 1);
		$scope.isLast = isLast;
		$scope.removeRule = function(rule) {
			$scope.urlRewriteRules = _.without($scope.urlRewriteRules, rule);
		};

		initialize();
		function initialize() {
			if($scope.version.id) {
				documentationService.version.listRewriteRules(doc.id, $scope.version.id)
					.then(function(rules) {
						$scope.urlRewriteRules = rules;
						originalRules = angular.copy(rules);
					});
			}
		}

		function save() {
			var saveDeferred = documentationService.version.save(doc.id, _.omit($scope.version, 'files'));

			if($scope.version.files.length) {
				saveDeferred = saveDeferred
					.then(function(savedVersion) {
						return documentationService.version
							.uploadFile(
							doc.id,
							savedVersion.id,
							$scope.version.files[0])
							.then(_.fn(savedVersion));
					});
			}

			if(!angular.equals(originalRules, $scope.urlRewriteRules)) {
				saveDeferred = saveDeferred.then(function(savedVersion) {
					return documentationService.version
						.updateRewriteRules(doc.id, savedVersion.id, $scope.urlRewriteRules)
						.then(_.fn(savedVersion));
				});
			}

			saveDeferred.then($modalInstance.close);
		}

		function moveRule(step, rule) {
			var currentIndex = _.indexOf($scope.urlRewriteRules, rule);
			$scope.urlRewriteRules.splice(currentIndex, 1);
			$scope.urlRewriteRules.splice(currentIndex + step, 0, rule);
		}

		function isFirst(rule) {
			return _.indexOf($scope.urlRewriteRules, rule) === 0;
		}

		function isLast(rule) {
			var lastRuleIndex = $scope.urlRewriteRules.length - 1;
			return _.indexOf($scope.urlRewriteRules, rule) === lastRuleIndex;
		}
	});
