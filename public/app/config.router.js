﻿﻿﻿﻿﻿'use strict';
var routeApp = angular.module('app')
    .run(
        [
            '$rootScope', '$state', '$stateParams', '$cookieStore',
            function($rootScope, $state, $stateParams, $cookieStore) {
                $rootScope.$state = $state;
                $rootScope.$stateParams = $stateParams;
            }
        ]
    )
    .config(
        [
            '$stateProvider', '$urlRouterProvider',
            function($stateProvider, $urlRouterProvider) {

                $urlRouterProvider
                    .otherwise('/app/blank');
                $stateProvider
                    .state('app', {
                        abstract: true,
                        url: '/app',
                        templateUrl: 'views/layout.html'
                    })
                    .state('login', {
                        url: '/login',
                        templateUrl: 'views/login.html',
                        ncyBreadcrumb: {
                            label: 'Login'
                        },
                        resolve: {
                            deps: [
                                '$ocLazyLoad',
                                function($ocLazyLoad) {
                                    return $ocLazyLoad.load({
                                        serie: true,
                                        files: [
                                            'app/controllers/login.js'
                                        ]
                                    });
                                }
                            ]
                        }
                    })
                    .state('error404', {
                        url: '/error404',
                        templateUrl: 'views/error-404.html',
                        ncyBreadcrumb: {
                            label: 'Error 404 - The page not found'
                        }
                    })
                    .state('error500', {
                        url: '/error500',
                        templateUrl: 'views/error-500.html',
                        ncyBreadcrumb: {
                            label: 'Error 500 - something went wrong'
                        }
                    })
                    .state('app.blank', {
                        url: '/blank',
                        templateUrl: 'views/welcome.html',
                        ncyBreadcrumb: {
                            label: 'Welcome'
                        }
                    })
                    .state('app.menus', {
             		    url: '/menus?title&desc&url&files&pkid',
             		    templateUrl: function ($stateParams){
             		    	return $stateParams.url;
             		    },
                        ncyBreadcrumb: {
                            label: ' '
                        },
                        resolve: {
                            deps: [
                                '$ocLazyLoad','$stateParams',
                                function($ocLazyLoad,$stateParams) {
                                	if($stateParams.files != undefined){
                                		var loadfiles=$stateParams.files.split(","); // 字符串分割
                                		return $ocLazyLoad.load(loadfiles);
                                	}else{
                                		return null;
                                	}
                                }
                            ]
                        }
             		});
            }
        ]
    );