
class UserCtrl

    constructor: (@$log, @UserService) ->
        @$log.debug "constructing UserController"
        @users = []
        @getAllUsers()

    getAllUsers: () ->
        @$log.debug "getAllUsers()"

        @UserService.listUsers()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Users"
                @users = data
            ,
            (error) =>
                @$log.error "Unable to get Users: #{error}"
            )

    deleteUser: (userName,email) ->
        @$log.debug "deleteUser()"
        @UserService.deleteUser(userName,email)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data} User"
                @getAllUsers()
        ,
            (error) =>
                @$log.error "Unable to delete User: #{error}"
            )

#注册controller（构造函数，本地注入）
controllersModule.controller('UserCtrl', UserCtrl)