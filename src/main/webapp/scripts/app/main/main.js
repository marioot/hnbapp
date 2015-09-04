'use strict';

angular.module('hnbappApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('home', {
                parent: 'site',
                url: '/',
                data: {
                    roles: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/rates/rates.html',
                        controller: 'RatesController'
                    }
                },
                resolve: {

                }
            });
    });
