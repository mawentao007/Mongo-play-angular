package controllers

import javax.inject.{Singleton, Inject}

import models.{JsonFormats, User}
import play.api.data.Form
import play.api.data.Forms._
import services.UUIDGenerator
import play.api.Logger
import play.api.mvc._
import views.html


import scala.concurrent.Future


/**
 * Instead of declaring an object of Application as per the template project, we must declare a class given that
 * the application context is going to be responsible for creating it and wiring it up with the UUID generator service.
 * @param uuidGenerator the UUID generator service we wish to receive.
 */
@Singleton
class Application @Inject() (uuidGenerator: UUIDGenerator) extends Controller with Secured{


  val loginForm = Form(
    tuple(                           //tuple 创建一个Mapping，verifying在这个Mapping的基础上加上点对点的限制条件和错误消息
      "userName" -> nonEmptyText,           //// tuples come with built-in apply/unapply
      "password" -> nonEmptyText             //single 在单变量的时候代替tuples
    )
      verifying ("Invalid email or password", result => result match {         //两路数据绑定的构造函数
      case (userName, password) => User.authenticate(userName.toString, password.toString).isDefined
    })
  )


  def index = Action{
    implicit request =>
      Logger.info("Serving index page...")
      request.session.get("userName") match{
        case Some(s) => Ok(html.index(s))
        case None => Ok(html.index("anonymous"))
      }

  }

  def randomUUID = Action {
    Logger.info("calling UUIDGenerator...")
    Ok(uuidGenerator.generate.toString)
  }

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(    //将数据与表格进行绑定，通过验证则不会返回错误消息
      formWithErrors => BadRequest(html.login(formWithErrors)),   //表格验证失败
      user => Redirect(routes.Application.index).withSession("userName" -> user._1)
    )
  }

  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }


}

trait Secured extends Results {
  private def username(request: RequestHeader) =request.session.get("userName")    //Some("marvin")//

  private def onUnauthorized(request: RequestHeader) = BadRequest("UnAuthorized")//Results.Redirect(routes.Application.login)

  //action 合并，Authenticated返回就是一个action，里面又嵌套了一个。这是一个carrying函数，有两部分参数，如果获得了username，就将结果map到后面的action

  object IsAuthenticated {
   /* def apply(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
      Action.async(request => Future.successful(f(user)(request)))
    }*/

    //函数重载，需要提供返回值类型？
    def apply(f: => String => Request[AnyContent] => Future[Result]):EssentialAction = apply(BodyParsers.parse.anyContent)(f)

    //currying函数，在传入bodyParser时候A的类型被固定，Request的类型也就被固定。如果不用这样的方式，实际上在request被传入的时候类型是any，
    //那么async的类型就和request类型不一致。
    def apply[A](bodyParser: BodyParser[A])(f: => String => Request[A] => Future[Result]):EssentialAction =
      Security.Authenticated(username, onUnauthorized) {
        user =>
          Action.async(bodyParser)(request => f(user)(request))
      }

    /* def IsAuthenticated[A](bodyParser: BodyParser[A],f: => String => Request[A] => Future[Result]) = Security.Authenticated(username, onUnauthorized) { user =>
    Action.async(bodyParser)(request => f(user)(request))
  }*/
  }

}
