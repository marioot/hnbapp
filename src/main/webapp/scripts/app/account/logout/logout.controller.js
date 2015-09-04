'use strict';

angular.module('hnbappApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
