import com.google.inject.{Guice, AbstractModule}
import play.api.GlobalSettings
import play.api.mvc.RequestHeader
import services.{SimpleUUIDGenerator, UUIDGenerator}
import play.api.mvc.Results._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Set up the Guice injector and provide the mechanism for return objects from the dependency graph.
 */
object Global extends GlobalSettings {

  /**
   * Bind types such that whenever UUIDGenerator is required, an instance of SimpleUUIDGenerator will be used.
   */
  val injector = Guice.createInjector(new AbstractModule {
    protected def configure() {
      bind(classOf[UUIDGenerator]).to(classOf[SimpleUUIDGenerator])
    }
  })

  /*override def onBadRequest(request: RequestHeader, error: String) = {
    Future(BadRequest("Bad Request: " + error))
  }*/

  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)
}
