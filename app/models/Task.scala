package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

case class Task(id: Long, label: String)
object Task {
  //This is so that Task can be converted into a Json.Write class implicitly, and
  //  so it can be sent out easily via Json
  //It's a macro that sets up a JS object with fields matching this class
  implicit val taskWrites = Json.writes[Task]

	def all(uid: Long): List[Task] = 
		//Play's DB withConnection helper
		DB.withConnection { 
			implicit c =>
			//Anorm's SQL function, using task parser to return results
			SQL("SELECT * FROM task WHERE uid = {uid}").on('uid -> uid).as(task *)
			//Or: you can apply() on SQL(...) to get a lazy stream of Row instances:
			//SQL("SELECT * FROM task")().map(row =>
				//new Task(row[Long]("id"), row[String]("label"))).toList
		}
	def create(label: String, uid: Int) {
		DB.withConnection {
			implicit c =>
			SQL("INSERT INTO task (label, uid) values ({label}, {uid})").on(
				'label -> label,
				'uid -> uid
			).executeUpdate()
		}
	}
	def delete(uid: Int, id: Long) {
		DB.withConnection {
			implicit c =>
			//Run sql, with replacements
			SQL("DELETE FROM task WHERE id = {id} AND uid = {uid}").on(
				//Replace {id} with id
				//Can also use strings instead of symbols for keys
				'id -> id,
				'uid -> uid
			).executeUpdate()
		}
	}
	def update(id: Long, uid: Int, newLabel: String) = {
		DB.withConnection {
			implicit c =>
				SQL("""
						UPDATE task
						SET label = {newLabel}
						WHERE uid = {uid}
						AND id = {id}
						""")
			.on('uid -> uid, 'id -> id, 'newLabel -> newLabel)
			.executeUpdate()
		}
	}
	
	//Anorm ex:
		//val Option[Long] = SQL("INSERT INTO City(name, country) values ({name}, {country})")
		//	.on('name -> "Cambridge", 'country -> "New Zealand").executeInsert()
	
	//Task is a parser, that given a ResultSet row with at least id and label, is able
  //  to create a Task value
	val task = {
		get[Long]("id") ~
		get[String]("label") map {
			case id~label => Task(id, label)
		}
	}
}
