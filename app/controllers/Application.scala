package controllers

import javax.inject.{Singleton, Inject}

import models.{JsonFormats, User}
import play.api.data.Form
import play.api.data.Forms._
import services.UUIDGenerator
import play.api.Logger
import play.api.mvc._
import views.html

import scala.text

/**
 * Instead of declaring an object of Application as per the template project, we must declare a class given that
 * the application context is going to be responsible for creating it and wiring it up with the UUID generator service.
 * @param uuidGenerator the UUID generator service we wish to receive.
 */
@Singleton
class Application @Inject() (uuidGenerator: UUIDGenerator) extends Controller {


  val loginForm = Form(
    tuple(                           //tuple 创建一个Mapping，verifying在这个Mapping的基础上加上点对点的限制条件和错误消息
      "userName" -> nonEmptyText,           //// tuples come with built-in apply/unapply
      "password" -> nonEmptyText             //single 在单变量的时候代替tuples
    )
      verifying ("Invalid email or password", result => result match {         //两路数据绑定的构造函数
      case (userName, password) => User.authenticate(userName.toString, password.toString).isDefined
    })
  )


  def index = Action {
    request =>
      Logger.info("Serving index page...")
      Ok(html.index())
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

trait Secured{
  private def username(request: RequestHeader) = request.session.get("email")
}
