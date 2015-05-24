package services

import play.modules.reactivemongo.json.collection.JSONCollection

/**
 * Created by marvin on 15-5-23.
 */
object MongoConnect {
  def getCollection(collectionName:String):JSONCollection = {
    import reactivemongo.api._
    import scala.concurrent.ExecutionContext.Implicits.global

    // gets an instance of the driver
    // (creates an actor system)
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost"))

    // Gets a reference to the database "plugin"
    val db = connection("one")

    // Gets a reference to the collection "acoll"
    // By default, you get a BSONCollection.
    val collection = db.collection[JSONCollection](collectionName)
    collection
  }
}
