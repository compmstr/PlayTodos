package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models.Task

import util.Utils
import play.api.Routes
import play.api.libs.json.Json

object Tasks extends Controller{

	def tasks = Action {
		implicit request =>
		Ok(views.html.tasks(Task.all(Utils.sessionUserId(request.session)), newTaskForm, updateTaskForm))
	}
  def tasksJson = Action {
    implicit request =>
      Ok(Json.toJson(Task.all(Utils.sessionUserId(request.session)))).as("text/json")
  }
	def newTask = Action {
		implicit request =>
		val uid = Utils.sessionUserId(request.session)
		newTaskForm.bindFromRequest.fold(
			//errors gets bound to the form with the errors flagged
			errors => BadRequest(views.html.tasks(Task.all(uid), errors, updateTaskForm)),
			//value will be either a single field, or a tuple with the form values in it
			label => {
				Task.create(label, uid)
				Redirect(routes.Tasks.tasks)
			}
		)
	}

  def deleteTaskJson(id: Long) = Action {
    implicit request =>
    val uid = Utils.sessionUserId(request.session)
    Task.delete(uid, id)
    Ok(Json.obj("status" -> "success")).as("text/json")
  }
	def deleteTask(id: Long) = Action {
		implicit request =>
		val uid = Utils.sessionUserId(request.session)
		Task.delete(uid, id)
		Redirect(routes.Tasks.tasks)
	}
	
	def updateTask(id: Long) = Action {
		implicit request =>
		val uid = Utils.sessionUserId(request.session)
		baseUpdateTaskForm.bindFromRequest.fold(
			errors => {
				println("Bad request: " + errors.errors.map((error) => error.key + ": " + error.message).mkString(", "))
				Redirect(routes.Tasks.tasks)
			},
			newLabel => {
				println("Updating id: %d -> %s -- %d rows updated".format(id, newLabel, Task.update(id, uid, newLabel)))
				Redirect(routes.Tasks.tasks)
			}
		)
	}

	val newTaskForm = Form(
		"label" -> nonEmptyText
	)
	
	val baseUpdateTaskForm = Form(
		"label" -> nonEmptyText
	)
	def updateTaskForm(label: String): Form[String] = {
		baseUpdateTaskForm.fill(label)
	}

  /**
   * A js file that imports the Play routes into javascript
   * See also main.scala.html for an inline version of the js routes
   * @return
   */
  def jsRoutes = Action {
    implicit request =>
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.Tasks.tasksJson,
        routes.javascript.Tasks.deleteTaskJson
      )
    ).as("text/javascript")
      /*Ok(Routes.javascriptRouter("taskRoutes",
        routes.javascript.Tasks.tasks,
        routes.javascript.Tasks.deleteTask))
      .withHeaders(("Content-Type", "text/javascript"))*/
  }

}
