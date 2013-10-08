
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import org.specs2.mock.Mockito
import test._

/**
 * Created with IntelliJ IDEA.
 * User: corey
 * Date: 10/8/13
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
class TaskRouteSpec extends Specification{

  "GET /ajax/tasks" should {
    "return a list of tasks" in {
      running(inMemory) {
        val header = FakeRequest(GET, "/ajax/tasks")
        val result = route(header).get

        status(result) must equalTo(200)
      }
    }
  }

  "GET /ajax/tasks/:id" should {
    "return task not found" in {
      running(inMemory){
        val header = FakeRequest(GET, "/ajax/tasks/999999999")
        val result = route(header).get

        status(result) must equalTo(404)
      }
    }
  }

}
