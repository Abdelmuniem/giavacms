'use strict';

angular.module('giavacms-scenario')

    .service('ScenariosProductsService', function (APP, RsResource) {

        angular.extend(this, new RsService(APP, RsResource, 'products'))

    })
;