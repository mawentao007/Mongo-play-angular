package services

import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.collections.default.BSONCollection

/**
 * Created by marvin on 15-5-23.
 */
object MongoConnect {
  ///这个函数如何设计？？
  def getDb(dbName:String) = {
    import reactivemongo.api._
    import scala.concurrent.ExecutionContext.Implicits.global

    // gets an instance of the driver
    // (creates an actor system)
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost"))

    // Gets a reference to the database "plugin"
    val db = connection(dbName)

    // Gets a reference to the collection "acoll"
    // By default, you get a BSONCollection.
    db
  }
}
