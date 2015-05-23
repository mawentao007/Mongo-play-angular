package models


import org.slf4j.{LoggerFactory, Logger}
import play.api.libs.json._
import services.MongoConnect._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}



case class User( age: Int,
                 firstName: String,
                 lastName: String,
                 active: Boolean)

object JsonFormats {
  import play.api.libs.json.Json

  // Generates Writes and Reads for Feed and User thanks to Json Macros
  //数据库中保存的是Json格式，序列化和反序列化原生支持，Bson也一样
  implicit val userFormat = Json.format[User]
}




object User{
  import JsonFormats._
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  private final val logger: Logger = LoggerFactory.getLogger(classOf[User])

  private val duration = Duration(100,"ms")
  private def collection = getCollection("users")

  def authenticate(firstname:String,lastname:String):Option[User] = {
   val userList:Future[List[User]] = collection.find(Json.obj("firstname" -> firstname,"lastname"->lastname)).cursor[User]
    .collect[List]()
 //   Duration.Inf  无限等待
    val result:List[User]  = Await.result(userList,Duration.Inf)
    if(result.isEmpty) return None
    Some(result.head)
    //Some(User(12,"12","12",true))

  }



}
