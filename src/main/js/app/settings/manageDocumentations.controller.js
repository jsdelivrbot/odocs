'use strict';

angular
  .module('docs.settings')
  .controller('ManageDocumentations', function($scope, $modal, _, documentationService, feedService) {
    $scope.documentations = [];
    $scope.addNewDocumentation = _.partial(editDocumentation, null);
    $scope.editDocumentation = editDocumentation;
    $scope.addNewVersion = _.partial(editVersion, _, null);
    $scope.addPredefinedVersion = addPredefinedVersion;
    $scope.editVersion = editVersion;
    $scope.removeVersion = removeVersion;
    $scope.removeDocumentation = removeDocumentation;

    $scope.canMoveVersionUp = _.partial(canMoveVersion, _.first);
    $scope.canMoveVersionDown = _.partial(canMoveVersion, _.last);
    $scope.moveVersionUp = _.partial(moveVersion, documentationService.version.moveUp);
    $scope.moveVersionDown = _.partial(moveVersion, documentationService.version.moveDown);

    $scope.canMoveDocumentationUp = _.partial(canMoveDocumentation, _.first);
    $scope.canMoveDocumentationDown = _.partial(canMoveDocumentation, _.last);
    $scope.moveDocumentationDown = _.partial(moveDocumentation, documentationService.documentation.moveDown);
    $scope.moveDocumentationUp = _.partial(moveDocumentation, documentationService.documentation.moveUp);

    initialize();

    function initialize() {
      loadDocumentationsList();
    }

    function loadDocumentationsList(selectedDoc, selectedVersion) {
      documentationService
        .documentation.list()
        .then(function(docs) {
          var selectedDocId = (selectedDoc || {}).id;
          var selectedVersionId = (selectedVersion || {}).id;

          $scope.documentations = _.map(docs, function(doc) {
            _.assign(doc, { isOpen: selectedDocId === doc.id });
            doc.versions = _.map(doc.versions, function(version) {
              return _.assign(version, { isOpen: selectedVersionId === version.id});
            });
            return doc;
          });
        });
    }

    function removeVersion(doc, version) {
      documentationService.version.remove(doc.id, version.id)
        .then(_.partial(loadDocumentationsList, doc, null));
    }

    function removeDocumentation(doc) {
      documentationService.documentation.remove(doc.id)
        .then(loadDocumentationsList);
    }

    function editDocumentation(doc) {
      $modal.open({
        templateUrl: 'settings/custom/addDocumentation.modal.controller.html',
        controller: 'AddDocumentationModal',
        resolve: {
          doc: _.constant(doc)
        }}).result.then(loadDocumentationsList);
    }

    function addPredefinedVersion() {
      var feed = null;

      $modal
        .open({
          templateUrl: 'settings/feed/selectFeed.modal.controller.html',
          controller: 'SelectFeedModal'
        })
        .result
        .then(function(selectedFeed) {
          feed = selectedFeed;
          return $modal.open({
            templateUrl: 'settings/feed/selectDocumentation.modal.controller.html',
            controller: 'SelectDocumentationModal'
          }).result;
        })
        .then(function(selectedDocumentation) {
          feedService.saveVersion(selectedDocumentation.id, feed);
        });
    }

    function editVersion(doc, version) {
      $modal.open({
        templateUrl: 'settings/custom/addVersion.modal.controller.html',
        controller: 'AddVersionModal',
        resolve: {
          doc: _.constant(doc),
          version: _.constant(version)
        }
      }).result.then(_.partial(loadDocumentationsList, doc));
    }

    function moveDocumentation(moveFn, doc) {
      moveFn(doc.id)
        .then(_.partial(loadDocumentationsList, doc));
    }

    function canMoveDocumentation(checkFn, doc) {
      return checkFn($scope.documentations).id !== doc.id;
    }

    function moveVersion(moveFn, doc, version) {
      moveFn(doc.id, version.id)
        .then(_.partial(loadDocumentationsList, doc, version));
    }

    function canMoveVersion(checkFn, doc, version) {
      return checkFn(doc.versions).id !== version.id;
    }
  });
