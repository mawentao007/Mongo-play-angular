package controllers


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
 * The Users controllers encapsulates the Rest endpoints and the interaction with the MongoDB, via ReactiveMongo
 * play plugin. This provides a non-blocking driver for mongoDB as well as some useful additions for handling JSon.
 * @see https://github.com/ReactiveMongo/Play-ReactiveMongo
 */
@Singleton
class Users extends Controller with MongoController with Secured {


  private val duration = Duration(100,"ms")

  /*
   * Get a JSONCollection (a Collection implementation that is designed to work
   * with JsObject, Reads and Writes.)
   * Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   * the collection reference to avoid potential problems in development with
   * Play hot-reloading.
   */

  //创建集合，类似于创建表
  def collection: JSONCollection = db.collection[JSONCollection]("users")

  // ------------------------------------------ //
  // Using case classes + Json Writes and Reads //
  // ------------------------------------------ //

  import models._
  import models.JsonFormats._

  /*
  * request.body is a JsValue.
  * There is an implicit Writes that turns this JsValue as a JsObject,
  * so you can call insert() with this JsValue.
  * (insert() takes a JsObject as parameter, or anything that can be
  * turned into a JsObject using a Writes.)
  *
  *
  * Performing a simple query

Queries are performed quite the same way as in the Mongo Shell.

val query = BSONDocument(
"age" -> BSONDocument(
 "$gt" -> 27))

// result type is Future[List[BSONDocument]]
val peopleOlderThanTwentySeven =
collection.
 find(query).
 cursor[BSONDocument].
 collect[List]()
Of course you can collect only a limited number of documents.

val peopleOlderThanTwentySeven =
collection.
 find(query).
 cursor[BSONDocument].
 collect[List](25) // get up to 25 documents
  */


  def createUser = IsAuthenticated(parse.json){
    user => request =>
      request.body.validate[User].map {
        user =>
          val nameSelector = Json.obj("firstName" -> user.userName, "password" -> user.password)
          val futureList = collection.find(nameSelector).cursor[User].collect[List](1)
          val num = Await.result(futureList, duration).size
          if(num > 0){
            Future(BadRequest("exist"))
          }else {
            collection.insert(user).map {
              lastError =>
                Logger.debug(s"Successfully inserted with LastError: $lastError")
                Created(s"User Created")
            }
          }

      }.getOrElse(Future.successful(BadRequest("invalid json")))
  }


  def deleteUser(userName:String,email:String) = IsAuthenticated(parse.json){
    user => request =>
    val nameSelector = Json.obj("userName" -> userName, "email" -> email)
    collection.remove(nameSelector).map{
      lastError =>
        Ok("go")
    }

  }



  def updateUser(userName: String, email: String) = IsAuthenticated(parse.json) {  //添加parse解析器
    user =>
      request =>
      val modifer =Json.obj(
        "$set" -> Json.obj(
          "userName" -> request.body\"userName",
          "email" -> request.body\"email"
        )
      )

      val nameSelector = Json.obj("userName" -> userName, "email" -> email)
      collection.update(nameSelector, modifer).map {
        lastError =>
          Logger.info(s"Successfully updated with LastError: $lastError")
          Created(s"User Updated")
      }

  /* request.body.validate[User].map {
        user =>
          //find our user by first name and last name
         val nameSelector = Json.obj("userName" -> userName, "email" -> email)
          collection.update(nameSelector, user).map {
            lastError =>
              logger.info(s"Successfully updated with LastError: $lastError")
              Created(s"User Updated")
          }
      }.getOrElse(Future.successful(BadRequest("invalid json")))*/
  }

  def findUsers = IsAuthenticated{
    user =>
      request =>
    // let's do our query
      val cursor: Cursor[User] = collection.
        // find all
        find(Json.obj()).
        // sort them by creation date,根据id倒排
        sort(Json.obj("_id" -> -1)).
      // perform the query and get a cursor of JsObject
        cursor[User]

    // gather all the JsObjects in a list
      val futureUsersList: Future[List[User]] = cursor.collect[List]()

    // transform the list into a JsArray
      val futurePersonsJsonArray: Future[JsArray] = futureUsersList.map { users =>
        Json.arr(users)
      }
    // everything's ok! Let's reply with the array
      futurePersonsJsonArray.map {
        users =>
          Ok(users(0))
      }
    }

  def test = Action.async{
    Future.successful(Ok("Hello world"))
  }

}
