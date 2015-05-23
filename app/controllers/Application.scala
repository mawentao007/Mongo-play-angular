package controllers

import javax.inject.{Singleton, Inject}

import models.{JsonFormats, User}
import play.api.data.Form
import play.api.data.Forms._
import services.UUIDGenerator
import org.slf4j.{LoggerFactory, Logger}
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

  private final val logger: Logger = LoggerFactory.getLogger(classOf[Application])

  val loginForm = Form(
    tuple(                           //tuple 创建一个Mapping，verifying在这个Mapping的基础上加上点对点的限制条件和错误消息
      "firstname" -> nonEmptyText,           //// tuples come with built-in apply/unapply
      "lastname" -> nonEmptyText             //single 在单变量的时候代替tuples
    )
      verifying ("Invalid email or password", result => result match {         //两路数据绑定的构造函数
      case (firstname, lastname) => User.authenticate(firstname, lastname).isDefined
    })
  )


  def index = Action {
    request =>
      logger.info("Serving index page...")
      Ok(html.index())
  }

  def randomUUID = Action {
    logger.info("calling UUIDGenerator...")
    Ok(uuidGenerator.generate.toString)
  }

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(    //将数据与表格进行绑定，通过验证则不会返回错误消息
      formWithErrors => BadRequest(html.login(formWithErrors)),   //表格验证失败
      user => Redirect(routes.Application.index).withSession("firstname" -> user._1)
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
