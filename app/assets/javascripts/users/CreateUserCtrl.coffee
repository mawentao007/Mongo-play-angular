
class CreateUserCtrl

    ###$window可以刷新页面###
    constructor: (@$window,@$log, @$location,  @UserService) ->
        @$log.debug "constructing CreateUserController"
        @user = {}

    createUser: () ->
        @$log.debug "createUser()"
        @UserService.createUser(@user)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data} User"
                @user = data
                @$location.path("/")
            ,
            (error) =>
                @$log.error "Unable to create User: #{error}"
                @$window.location.href = '/login'

            )

controllersModule.controller('CreateUserCtrl', CreateUserCtrl)