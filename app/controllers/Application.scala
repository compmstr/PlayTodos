package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models.Task

object Application extends Controller {

  def index = Action {
		Redirect(routes.Application.tasks)
  }
	
	def tasks = Action {
		Ok(views.html.index(Task.all(), taskForm))
	}
	def newTask = Action {
		implicit request =>
		taskForm.bindFromRequest.fold(
			//errors gets bound to the form with the errors flagged
			errors => BadRequest(views.html.index(Task.all(), errors)),
			//value will be either a single field, or a tuple with the form values in it
			label => {
				Task.create(label)
				Redirect(routes.Application.tasks)
			}
		)
	}
	def deleteTask(id: Long) = Action {
		Task.delete(id)
		Redirect(routes.Application.tasks)
	}

	val taskForm = Form(
		"label" -> nonEmptyText
	)
}
