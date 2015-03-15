'use strict';

angular
  .module('docs.settings')
  .constant('AddDocumentationModalConstant', {
    title: {
      addDocumentation: 'Add documentation',
      editDocumentation: 'Edit documentation'
    }
  })
  .controller('AddDocumentationModal', function($scope, $modalInstance, AddDocumentationModalConstant, documentationService, doc) {
    var oldDoc = doc || {};
    $scope.cancel = $modalInstance.dismiss;
    $scope.documentationName = oldDoc.name || '';
    $scope.save = save;
    $scope.windowTitle = oldDoc.id
      ? AddDocumentationModalConstant.title.editDocumentation
      : AddDocumentationModalConstant.title.addDocumentation;

    function save() {
      documentationService.documentation
        .save(oldDoc.id, { name: $scope.documentationName })
        .then($modalInstance.close);
    }
  });
