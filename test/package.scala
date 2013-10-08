import models.{User, Task}
import org.specs2.mutable.Specification
import play.api.mvc.Cookies
import play.api.test.FakeApplication
import play.api.test.Helpers._
import play.GlobalSettings

package object test extends Specification {
  def inMemory = FakeApplication(additionalConfiguration =
    Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url" -> "jdbc:h2:mem:play;MODE=MYSQL;DB_CLOSE_DELAY=-1",
      "evolutionplugin" -> "enabled",
      "applyEvolutions.default" -> "true"
    ))

  private var dataLoaded = false;
  private def loadFakeDataIfNotThere(){
    if(!dataLoaded){
      Task.create("foo", guestUid)
      Task.create("bar", guestUid)
      dataLoaded = true;
    }
  }

  def runWithTestDb[T](block: => T): T = {
    running(inMemory) {
      loadFakeDataIfNotThere()
      block
    }
  }

  def guestUid = {
    User.getByName("guest")match{
      case Some(user: User) => user.uid.toInt
      case None => 1
    }
  }

  def guestLogin = {
    ("userId" -> guestUid.toString)
  }
}