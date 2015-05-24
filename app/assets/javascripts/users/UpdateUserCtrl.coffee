class UpdateUserCtrl

  constructor: (@$log, @$location, @$routeParams, @UserService) ->
      @$log.debug "constructing UpdateUserController"
      @user = {}

      #在这里就调用了函数
      @findUser()
      @$log.debug "？？() #{angular.toJson(@user, true)}"

  #这里的user的确不是findUser中赋值的，是在view中通过submit提交的值。
  updateUser: () ->
      @UserService.updateUser(@$routeParams.userName, @$routeParams.email, @user)
      .then(
          (data) =>
            @$log.debug "Promise returned #{data} User"
            @user = data
            @$location.path("/")
        ,
        (error) =>
            @$log.error "Unable to update User: #{error}"
      )


#这个方法用来确定相应的对象存在，并不是给user赋值
  findUser: () ->
      # route params must be same name as provided in routing url in app.coffee
      userName = @$routeParams.userName
      email = @$routeParams.email
      @$log.debug "findUser route params: #{userName} #{password}"

      @UserService.listUsers()
      .then(
        (data) =>
          @$log.debug "Promise returned #{data.length} Users"

          # find a user with the name of userName and password
          # as filter returns an array, get the first object in it, and return it
          @user = (data.filter (user) -> user.userName is userName and user.email is email)[0]
          @$log.debug "filter user #{angular.toJson(@user, true)}"

      ,
        (error) =>
          @$log.error "Unable to get Users: #{error}"
      )

#（服务名，构造函数）
controllersModule.controller('UpdateUserCtrl', UpdateUserCtrl)