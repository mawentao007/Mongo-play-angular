package controllers.Post

import controllers.{Secured}
import models.Post.ModelComment
import org.joda.time.DateTime
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger

import scala.concurrent.Future

/**
 * Created by marvin on 15-5-29.
 * 
 */
class Comment extends Controller with MongoController with Secured {
  def collection = db.collection[BSONCollection]("comments")


  def makeComment(id:String) = Action.async { implicit request =>
      ModelComment.form.bindFromRequest.fold(
        errors => Future.successful(Ok("Error")),
        comment =>
          collection.insert(comment.copy(postId = Some(BSONObjectID(id)),creationDate = Some(new DateTime()), updateDate = Some(new DateTime()))).map(_ =>
            Redirect(controllers.Post.routes.Post.getAllPost()))
      )
  }


  def deleteCommentById(id:String) = Action.async{implicit request =>
    Logger.info("deleteCommentById")
    collection.remove(BSONDocument("_id" -> BSONObjectID(id))).map{
      case lastError => Redirect(controllers.Post.routes.Post.getAllPost())
    }
  }

  /*def getCommentByPostId(postId:String) = Action.async{
    implicit request =>
      val comments = collection.find(BSONDocument("postId" -> BSONObjectID(postId))).cursor[models.Post.Comment]
      comments.collect[List]().map { commentList =>
        val contentList: List[String] = commentList.map {
          comment => comment.content
        }
        Logger.info(contentList.toString())
        Ok(views.html.Post.comments.render(contentList))
      }
  }*/



}