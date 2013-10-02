package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models.Task

import util.Utils

object Tasks extends Controller{

	def tasks = Action {
		implicit request =>
		Ok(views.html.tasks(Task.all(Utils.sessionUserId(request.session)), newTaskForm, updateTaskForm))
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
	
}
