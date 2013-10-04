package models

import util.Utils
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

case class Task(val uid: Long, val id: Long, val label: String, val complete: Boolean)
object Task {
  //This is so that Task can be converted into a Json.Write class implicitly, and
  //  so it can be sent out easily via Json
  //It's a macro that sets up a JS object with fields matching this class
  implicit val taskWrites = Json.writes[Task]

  def byId(uid: Long, id: Long): Option[Task] = {
    DB.withConnection{
      implicit c =>
        //singleOpt is used as opposed to as for a single result
         SQL("SELECT * FROM task WHERE uid = {uid} AND id = {id} LIMIT 1")
        .on('uid -> uid, 'id -> id).singleOpt(task)
    }
  }

	def all(uid: Long): List[Task] = {
		//Play's DB withConnection helper
		DB.withConnection { 
			implicit c =>
			//Anorm's SQL function, using task parser to return results
			SQL("SELECT * FROM task WHERE uid = {uid} ORDER BY id DESC").on('uid -> uid).as(task *)
			//Or: you can apply() on SQL(...) to get a lazy stream of Row instances:
			//SQL("SELECT * FROM task")().map(row =>
				//new Task(row[Long]("id"), row[String]("label"))).toList
		}
  }
	def create(label: String, uid: Int): Option[Long] = {
    //executeInsert returns auto-generated key(s) upon returning
    //executeUpdate does not
		DB.withConnection {
			implicit c =>
        val newId: Option[Long] = SQL("INSERT INTO task (label, uid) values ({label}, {uid})").on(
          'label -> label,
          'uid -> uid
        ).executeInsert()
        newId
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

	def update(id: Long, uid: Int, newLabel: Option[String] = None, newComplete: Option[Boolean] = None) = {
		DB.withConnection {
			implicit c =>
        var setClauses = "";
        setClauses = newLabel match {
          case Some(label: String) =>
            Utils.addSetClause(setClauses, "label", label)
          case None =>
            setClauses
        }
        setClauses = newComplete match {
          case Some(complete: Boolean) =>
            Utils.addSetClause(setClauses, "complete", if(complete) "1" else "0")
          case None => setClauses
        }
				SQL("""
						UPDATE task
            SET {setClauses}
						WHERE uid = {uid}
						AND id = {id}
            						""")
			.on('uid -> uid, 'id -> id, 'setClauses -> setClauses)
			.executeUpdate()
		}
	}

  def toggleComplete(id: Long, uid: Long){
    DB.withConnection {
      implicit c =>
         SQL(
           """
             UPDATE task
             SET complete =
               (SELECT IF(complete = 0, 1, 0) FROM task WHERE id = {id} AND uid = {uid})
             WHERE id = {id} AND uid = {uid}
           """)
        .on('uid -> uid, 'id -> id)
    }
  }
  def toggleComplete(task: Task){
    toggleComplete(task.id, task.uid)
  }

  def setComplete(id: Long, uid: Int, newComplete: Boolean){
    update(id, uid, None, Some(newComplete))
  }
	
	//Anorm ex:
		//val Option[Long] = SQL("INSERT INTO City(name, country) values ({name}, {country})")
		//	.on('name -> "Cambridge", 'country -> "New Zealand").executeInsert()
	
	//Task is a parser, that given a ResultSet row with at least id and label, is able
  //  to create a Task value
	val task = {
    get[Long]("uid") ~
		get[Long]("id") ~
		get[String]("label") ~
    get[Int]("complete") map {
			case uid~id~label~complete => Task(uid, id, label, (complete != 0))
		}
	}
}
