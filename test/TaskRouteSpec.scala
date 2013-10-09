
import models.Task
import play.api.libs.json.JsUndefined
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
      runWithTestDb  {
        val header = FakeRequest(GET, "/ajax/tasks").withSession(guestLogin)
        val result = route(header).get

        status(result) must equalTo(200)
      }
    }
  }

  "GET /ajax/tasks/:id" should {
    "return task not found if task doesn't exist" in {
      runWithTestDb {
        val header = FakeRequest(GET, "/ajax/tasks/999999999").withSession(guestLogin)
        val result = route(header).get

        (contentAsJson(result) \ "status").asOpt[String].getOrElse("") mustEqual "failure"
        status(result) mustEqual(404)
      }
    }
    "return a task if task exists" in {
      runWithTestDb {
        val header = FakeRequest(GET, "/ajax/tasks/1").withSession(guestLogin)
        val result = route(header).get

        val retObj = contentAsJson(result)

        (retObj \ "task") mustNotEqual JsUndefined
        (retObj \ "status").asOpt[String].getOrElse("") mustEqual "success"
        status(result) must equalTo(200)
      }
    }
  }

}
