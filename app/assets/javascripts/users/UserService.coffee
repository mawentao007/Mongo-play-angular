
class UserService

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q) ->
        @$log.debug "constructing UserService"

    listUsers: () ->
        @$log.debug "listUsers()"
        deferred = @$q.defer()

#和server端通信，返回数据进行处理
        @$http.get("/users")
        .success((data, status, headers) =>
                @$log.info("Successfully listed Users - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to list Users - status #{status}")
                deferred.reject(data)
            )
        deferred.promise

    createUser: (user) ->
        @$log.debug "createUser #{angular.toJson(user, true)}"
        deferred = @$q.defer()

        @$http.post('/user', user)
        .success((data, status, headers) =>
                @$log.info("Successfully created User - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to create user - status #{status}")
                alert("user exist")
                deferred.reject(data)
            )
        deferred.promise

    updateUser: (userName, email, user) ->
      @$log.debug "updateUser #{angular.toJson(user, true)}"
      deferred = @$q.defer()

      @$http.put("/user/#{userName}/#{email}", user)
      .success((data, status, headers) =>
              @$log.info("Successfully updated User - status #{status}")
              deferred.resolve(data)
            )
      .error((data, status, header) =>
              @$log.error("Failed to update user - status #{status}")
              deferred.reject(data)
            )
      deferred.promise

    deleteUser: (userName, email) ->

      deferred = @$q.defer()

      @$http.delete("/users/#{userName}/#{email}")
      .success((data, status, headers) =>
        @$log.info("Successfully updated User - status #{status}")
        deferred.resolve(data)
      )
      .error((data, status, header) =>
        @$log.error("Failed to update user - status #{status}")
        deferred.reject(data)
      )
      deferred.promise

servicesModule.service('UserService', UserService)