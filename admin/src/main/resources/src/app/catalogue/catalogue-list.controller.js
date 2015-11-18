'use strict';

angular.module('giavacms-catalogue')

    .config(function ($stateProvider, $urlRouterProvider, APP) {

        // Create a state for our seed test page
        $stateProvider.state( APP.BASE + 'catalogue', {
            // set the url of this page
            url: '/catalogue',
            // set the html template to show on this page
            templateUrl: 'app/catalogue/catalogue-list.html',
            // set the controller to load for this page
            controller: 'CatalogueListController'
        });

    })

    .run(function(MenuService, APP) {
        MenuService.addLink(APP.BASE + "catalogue", 'Product list', 1, 'fa fa-list', APP.CATALOGUE.toggle);
    })

    .controller('CatalogueListController', function ($scope) {

    })

;