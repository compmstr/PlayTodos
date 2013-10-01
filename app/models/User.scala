package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class User(uid: Long, username: String)

object User{
	def login(username: String, password: String): Option[User] = {
		DB.withConnection {
			implicit c =>
				SQL("SELECT uid, username FROM users WHERE username = {username} AND pass = {password}")
					.on('username -> username, 'password -> password)
			//.singleOpt gets a single row, or None
			//  .single returns the object directly, but breaks if no result
					.singleOpt(user)
		}
	}
	
	val user = {
		get[Long]("uid") ~
		get[String]("username") map {
			case uid~username => User(uid, username)
		}
	}
}
