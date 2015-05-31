package controllers.Post


import controllers. Secured
import models.Post.ModelComment
import play.modules.reactivemongo.MongoController

import reactivemongo.api.gridfs.{ReadFile, GridFS}
import reactivemongo.bson.{BSONObjectID, BSONDocument, BSONValue}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
//没有就报错
import reactivemongo.api.gridfs.Implicits.{DefaultReadFileReader}




/**
 * Created by marvin on 15-5-26.
 */
class Post extends Controller with MongoController with Secured {

  val gridFS = GridFS(db)

  def post = Action.async(gridFSBodyParser(gridFS)) { request =>
    // here is the future file!
    //   val futureFile:Future[[ReadFile[BSONValue]]] = request.body.files.head.ref
    //将数据存入gridfs，同时返回存储后的元数据
    val futureFile: Future[ReadFile[BSONValue]] = request.body.files.head.ref
    // when the upload is complete, we add the article id to the file entry (in order to find the attachments of the article)
/*    val futureUpdate = for {
      file <- futureFile
      // here, the file is completely uploaded, so it is time to update the article
      updateResult <- {
        //从元数据集合中找到相应的文件的元数据并更新
        //Genericcollection有update方法，BSONCollection继承了这个trait。
        gridFS.files.asInstanceOf[BSONCollection].update(
          BSONDocument("_id" -> file.id),
          BSONDocument("$set" -> BSONDocument("article" -> BSONObjectID(id))))
      }
    } yield updateResult*/

    futureFile.map {
      case file => Redirect(controllers.Post.routes.Post.getAllPost())
    }.recover {
      case e => InternalServerError(e.getMessage())
    }
  }



  def getPostById(id:String) = Action.async{
    request =>
      val file = gridFS.find(BSONDocument("_id" -> BSONObjectID(id)))
      serve(gridFS,file)
  }

  def getAllPost =Action.async{
    val postIdwithContent = ModelComment.getCommentWithPostId
    gridFS.find(BSONDocument()).collect[List]().map{
      files =>
        val filesWithId = files.map { file =>
          file.id.asInstanceOf[BSONObjectID].stringify -> file.asInstanceOf[ReadFile[BSONValue]]
        }
        Ok(views.html.Post.post(postIdwithContent,Some(filesWithId),ModelComment.form))
    }


  }

 /* def postPage = Action.async{
    request =>
      Future.successful(Ok(views.html.Post.post.render()))
  }*/


}
