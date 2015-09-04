'use strict';

angular.module('hnbappApp').controller('CurrencyDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Currency',
        function($scope, $stateParams, $modalInstance, entity, Currency) {

        $scope.currency = entity;
        $scope.load = function(id) {
            Currency.get({id : id}, function(result) {
                $scope.currency = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('hnbappApp:currencyUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.currency.id != null) {
                Currency.update($scope.currency, onSaveFinished);
            } else {
                Currency.save($scope.currency, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
