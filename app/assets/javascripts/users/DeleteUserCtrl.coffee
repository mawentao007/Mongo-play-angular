
class DeleteUserCtrl

    constructor: (@$log, @$location,@$routeParams, @UserService) ->
        @$log.debug "constructing DeleteUserController"
        @deleteUser()

    deleteUser: () ->
        @$log.debug "deleteUser()"
        userName = @$routeParams.userName
        @$log.debug "#{@$routeParams}"
        email = @$routeParams.email
        @UserService.deleteUser(userName,email)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data} User"
                @$location.path("/")
            ,
            (error) =>
                @$log.error "Unable to delete User: #{error}"
                alert(error)
                @$location.path("/")
            )

controllersModule.controller('DeleteUserCtrl', DeleteUserCtrl)