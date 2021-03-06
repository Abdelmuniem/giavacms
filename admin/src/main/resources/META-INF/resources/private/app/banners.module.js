'use strict';

angular.module('giavacms-banners', ['ui.router', 'giavacms-table'])

    .config(function(APP) {
        APP.BANNERS = {
            IMAGESPATH: APP.RESOURCES + 'images'
        };
    })

    .run(function(MenuService, APP) {

        var aclName = 'BANNERS';
        var aclRoles = 'Admin, admin, Developer, Banners';
        APP.ACL[aclName] = aclRoles;

        APP.BANNERS.toggle = MenuService.addToggleWithACL(aclName, 'banners', 2200,'fa fa-film');

    })

;
