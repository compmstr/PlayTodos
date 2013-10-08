import models.User
import org.specs2.mutable.Specification

import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import org.specs2.mock.Mockito
import test._

/**
 * Created with IntelliJ IDEA.
 * User: corey
 * Date: 10/8/13
 * Time: 1:17 PM
 * To change this template use File | Settings | File Templates.
 */
class UsersSpec extends Specification {
  "User.login" should {
    "return some user if login succeeded" in {
      running(inMemory) {
        val res = User.login("guest", "guestPass");
        res must beSome
      }
    }

    "return None if login failed" in {
      running(inMemory) {
        val res = User.login("guest", "thisPasswordCouldNeverWork!");
        res must beNone
      }
    }
  }

  "User.getByName" should {
    "return an existing user" in {
      runWithTestDb {
        val user = User.getByName("guest")
        user must beSome
      }
    }

    "return None for nonexistant user" in {
      runWithTestDb {
        val user = User.getByName("blargh")
        user must beNone
      }
    }
  }

  "User.getById" should {
    "return an existing user" in {
      runWithTestDb {
        val user = User.getById(1)
        user must beSome
      }
    }

    "return None for nonexistant user" in {
      runWithTestDb {
        val user = User.getById(666666666)
        user must beNone
      }
    }
  }
}
