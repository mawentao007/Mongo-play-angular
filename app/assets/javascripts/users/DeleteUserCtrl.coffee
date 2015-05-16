
class DeleteUserCtrl

    constructor: (@$log, @$location,@$routeParams, @UserService) ->
        @$log.debug "constructing CreateUserController"
        @deleteUser()

    deleteUser: () ->
        @$log.debug "deleteUser()"
        firstName = @$routeParams.firstName
        lastName = @$routeParams.lastName
        @UserService.deleteUser(firstName,lastName)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data} User"
                @$location.path("/")
            ,
            (error) =>
                @$log.error "Unable to delete User: #{error}"
            )

controllersModule.controller('DeleteUserCtrl', DeleteUserCtrl)