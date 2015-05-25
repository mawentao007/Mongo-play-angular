package models



import play.api.Logger
import play.api.libs.json._
import services.MongoConnect._


import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}



case class User( email:String,
                 userName: String,
                 password: String
                 )


object JsonFormats {
  import play.api.libs.json.Json

  // Generates Writes and Reads for Feed and User thanks to Json Macros
  //数据库中保存的是Json格式，序列化和反序列化原生支持，Bson也一样
  implicit val userFormat = Json.format[User]
}




object User{
  import JsonFormats._
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
 /* private final val logger: Logger = Logger(this.getClass())*/

  private val duration = Duration(100,"ms")
  private def collection = getCollection("users")

  def authenticate(userName:String,password: String):Option[User] = {
   val userList:Future[List[User]] = collection.find(Json.obj("userName" -> userName,"password"->password)).cursor[User]
    .collect[List]()              //妈蛋大小写错了花了我好长时间，注意Json.obj中相应字段大小写！！！
 //   Duration.Inf  无限等待

    val result:List[User]  = Await.result(userList,Duration.Inf)

   /* val nameSelector = Json.obj("firstName" -> firstname, "lastName" -> lastname)
    val futureList = collection.find(nameSelector).cursor[User].collect[List](1)
    val num = Await.result(futureList, duration).size*/
    Logger.info(result.size.toString)
    if(result.isEmpty) return None
    Some(result.head)

    //Some(User(12,"12","12",true))

  }



}
