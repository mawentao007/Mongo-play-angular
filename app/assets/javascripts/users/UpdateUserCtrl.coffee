class UpdateUserCtrl

  constructor: (@$log, @$location, @$routeParams, @UserService) ->
      @$log.debug "constructing UpdateUserController"
      @user = {}

      #在这里就调用了函数
      @findUser()
      @$log.debug "？？() #{angular.toJson(@user, true)}"

  #这里的user的确不是findUser中赋值的，是在view中通过submit提交的值。
  updateUser: () ->
      @$log.debug "!!!!() #{angular.toJson(@user, true)}"
      @user.active = true
      @UserService.updateUser(@$routeParams.firstName, @$routeParams.lastName, @user)
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
      firstName = @$routeParams.firstName
      lastName = @$routeParams.lastName
      @$log.debug "findUser route params: #{firstName} #{lastName}"

      @UserService.listUsers()
      .then(
        (data) =>
          @$log.debug "Promise returned #{data.length} Users"

          # find a user with the name of firstName and lastName
          # as filter returns an array, get the first object in it, and return it
          @user = (data.filter (user) -> user.firstName is firstName and user.lastName is lastName)[0]
          @$log.debug "filter user #{angular.toJson(@user, true)}"

      ,
        (error) =>
          @$log.error "Unable to get Users: #{error}"
      )

#（服务名，构造函数）
controllersModule.controller('UpdateUserCtrl', UpdateUserCtrl)