'use strict';

angular.module('hnbappApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


