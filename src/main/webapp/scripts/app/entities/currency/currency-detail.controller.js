'use strict';

angular.module('hnbappApp')
    .controller('CurrencyDetailController', function ($scope, $rootScope, $stateParams, entity, Currency) {
        $scope.currency = entity;
        $scope.load = function (id) {
            Currency.get({id: id}, function(result) {
                $scope.currency = result;
            });
        };
        $rootScope.$on('hnbappApp:currencyUpdate', function(event, result) {
            $scope.currency = result;
        });
    });
