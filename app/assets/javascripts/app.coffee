
dependencies = [
    'ngRoute',
    'ui.bootstrap',
    'myApp.filters',
    'myApp.services',
    'myApp.controllers',
    'myApp.directives',
    'myApp.common',
    'myApp.routeConfig'
]

app = angular.module('myApp', dependencies)
#创建新model


#assets的路径被指定为public，所以模版什么的在下面.应该是webjar把客户端的文件都传送到客户端的assets目录下，js再从中读取
angular.module('myApp.routeConfig', ['ngRoute'])
    .config ($routeProvider) ->
        $routeProvider
            .when('/', {
                templateUrl: '/assets/partials/view.html'
            })
            .when('/users/create', {
                templateUrl: '/assets/partials/create.html'
            })
            .when('/users/edit/:firstName/:lastName', {
                templateUrl: '/assets/partials/update.html'
            })
            .when('/users/:firstName/:lastName', {
                templateUrl: '/assets/partials/delete.html'
            })
            .otherwise({redirectTo: '/'})
    .config ($locationProvider) ->
        $locationProvider.html5Mode({
            enabled: true,
            requireBase: false
        })

#创建多个model的全局访问变量，整个app都可以通过这些变量访问相应模块，这里将所有模块融合
@commonModule = angular.module('myApp.common', [])
@controllersModule = angular.module('myApp.controllers', [])
@servicesModule = angular.module('myApp.services', [])
@modelsModule = angular.module('myApp.models', [])
@directivesModule = angular.module('myApp.directives', [])
@filtersModule = angular.module('myApp.filters', [])