package models.Post


import org.joda.time.DateTime
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson._
import play.api.data.format.Formats._

import scala.collection.mutable
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration

//必须加

import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by marvin on 15-5-29.
 */
case class ModelComment(
                         id: Option[BSONObjectID],
                         postId: Option[BSONObjectID],
                         author: String,
                         content: String,
                         creationDate: Option[DateTime],
                         updateDate: Option[DateTime])

object ModelComment {

  def db = services.MongoConnect.getDb("one")

  def collection = db.collection[BSONCollection]("comments")

  implicit object CommentBSONReader extends BSONDocumentReader[ModelComment] {
    def read(doc: BSONDocument): ModelComment =
      ModelComment(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONObjectID]("postId"),
        doc.getAs[String]("author").get,
        doc.getAs[String]("content").get,
        doc.getAs[BSONDateTime]("creationDate").map(dt => new DateTime(dt.value)),
        doc.getAs[BSONDateTime]("updateDate").map(dt => new DateTime(dt.value)))
  }

  implicit object CommentBSONWriter extends BSONDocumentWriter[ModelComment] {
    def write(comment: ModelComment): BSONDocument =
      BSONDocument(
        "_id" -> comment.id.getOrElse(BSONObjectID.generate),
        "postId" -> comment.postId,
        "author" -> comment.author,
        "content" -> comment.content,
        "creationDate" -> comment.creationDate.map(date => BSONDateTime(date.getMillis)),
        "updateDate" -> comment.updateDate.map(date => BSONDateTime(date.getMillis)))
  }

  val form = Form(
    mapping(
      "id" -> optional(of[String] verifying pattern(
        """[a-fA-F0-9]{24}""".r,
        "constraint.objectId",
        "error.objectId")),
      "postId" -> optional(of[String]),
      "author" -> optional(of[String]),
      "content" -> text,
      "creationDate" -> optional(of[Long]),
      "updateDate" -> optional(of[Long])) { (id, postId, author, content, creationDate, updateDate) =>
      ModelComment(
        id.map(BSONObjectID(_)),
        postId.map(BSONObjectID(_)),
        author.getOrElse("marvin"),
        content,
        creationDate.map(new DateTime(_)),
        updateDate.map(new DateTime(_)))
    } { comment =>
      Some(
        (comment.id.map(_.stringify),
          comment.postId.map(_.stringify),
          Some(comment.author),
          comment.content,
          comment.creationDate.map(_.getMillis),
          comment.updateDate.map(_.getMillis)))
    })


  def getCommentWithPostId: Future[Map[String, List[(String,String)]]] = {
    val comments = collection.find(BSONDocument()).cursor[models.Post.ModelComment]
    val contentListWithPostIdPrimise = comments.collect[List]().map { commentList =>
      commentList.map {
        comment => comment.postId.map(_.stringify) -> (comment.id.map(_.stringify).get,comment.content)
      }
    }


    val contentGroup = contentListWithPostIdPrimise map{
      x => x.groupBy{case (x,y) => x}
    }
//map嵌套层数不能太多
    contentGroup.map{
      y => y.map{x => (x._1.get -> x._2.map(m => m._2))}
    }
/*
    val contentListWithPostId = Await.result(contentListWithPostIdPrimise,Duration.Inf)



    contentListWithPostId.groupBy { case (x, y) => x }.foreach {
      x => postIdToContentList.put(x._1.get, x._2.map(m => m._2))
    }


    Logger.info("postIdToContentList " + postIdToContentList.toString)
    postIdToContentList.toMap*/
  }

}
