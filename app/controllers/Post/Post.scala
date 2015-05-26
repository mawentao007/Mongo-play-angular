package controllers.Post

import controllers.Application
import controllers.Secured
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import reactivemongo.api.Cursor
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Logger
import javax.inject.Singleton
import play.api.mvc._
import play.api.libs.json._


/**
 * Created by marvin on 15-5-26.
 */
class Post extends Controller with MongoController with Secured {


  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("picture").map { picture =>
      import java.io.File
      val filename = picture.filename
      val contentType = picture.contentType
      picture.ref.moveTo(new File(s"/tmp/picture/$filename"))
      Ok("File uploaded")
    }.getOrElse {
      Redirect(controllers.routes.Application.index).flashing(
        "error" -> "Missing file")
    }
  }


  //别忘了加render()
  def uploadPage = Action.async{
    Future(Ok(views.html.Post.post.render()))

  }

}
