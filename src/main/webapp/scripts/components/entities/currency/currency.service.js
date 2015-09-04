'use strict';

angular.module('hnbappApp')
    .factory('Currency', function ($resource, DateUtils) {
        return $resource('api/currencys/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'queryRates': {
                method: 'GET',
                url: 'api/currencys/rates',
                isArray: true,
                params: {
                    start: 'start',
                    end: 'end',
                    resolution: 'resolution',
                    currencies: 'currencies'
                },
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.date = DateUtils.convertLocaleDateFromServer(data.date);
                    return data;
                }
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.date = DateUtils.convertLocaleDateFromServer(data.date);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.date = DateUtils.convertLocaleDateToServer(data.date);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.date = DateUtils.convertLocaleDateToServer(data.date);
                    return angular.toJson(data);
                }
            }
        });
    });
