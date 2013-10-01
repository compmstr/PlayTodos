package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models.Task
import models.User

object Application extends Controller {
	
	def strToInt(s: String): Option[Int] = {
		try{
			Some(s.toInt)
		}catch{
			case e: Throwable => None
		}
	}

  def index = Action {
		request =>
			request.session.get("userId").map {
				userId =>
					Redirect(routes.Application.tasks)
			}.getOrElse{
				Ok(views.html.index(loginForm))
			}
  }
	
	def sessionUserId(session: Session): Int = {
		strToInt(session.get("userId").getOrElse("-1")).getOrElse(-1)
	}
	
	def tasks = Action {
		implicit request =>
			Ok(views.html.tasks(Task.all(sessionUserId(request.session)), newTaskForm, updateTaskForm))
	}
	def newTask = Action {
		implicit request =>
			val uid = sessionUserId(request.session)
		newTaskForm.bindFromRequest.fold(
			//errors gets bound to the form with the errors flagged
			errors => BadRequest(views.html.tasks(Task.all(uid), errors, updateTaskForm)),
			//value will be either a single field, or a tuple with the form values in it
			label => {
				Task.create(label, uid)
				Redirect(routes.Application.tasks)
			}
		)
	}
	def deleteTask(id: Long) = Action {
		implicit request =>
			val uid = sessionUserId(request.session)
		Task.delete(uid, id)
		Redirect(routes.Application.tasks)
	}
	
	def updateTask(id: Long) = Action {
		implicit request =>
			val uid = sessionUserId(request.session)
			baseUpdateTaskForm.bindFromRequest.fold(
				errors => {
					println("Bad request: " + errors)
					BadRequest(views.html.tasks(Task.all(uid), newTaskForm, updateTaskForm))
				},
				vals => {
					val (id, newLabel) = vals
					println("Updating -- %d rows update".format(Task.update(id, uid, newLabel)))
					Redirect(routes.Application.tasks)
				}
			)
	}
	
	def login = Action {
		implicit request =>
			loginForm.bindFromRequest.fold(
				errors => BadRequest(views.html.index(errors)),
				vals => {
					val (username, pass) = vals
					println("Username: %s - Pass: %s -- res: %s".format(username, pass, User.login(username, pass)))
					User.login(username, pass) match {
						case None => Redirect(routes.Application.index)
						case Some(user) => Redirect(routes.Application.tasks).withSession("userId" -> user.uid.toString)
					}
				}
			)
	}
	def logout = Action {
		implicit request =>
			Redirect(routes.Application.index).withSession(session - "userId")
	}

	val newTaskForm = Form(
		"label" -> nonEmptyText
	)
	
	val baseUpdateTaskForm = Form(
		tuple(
			"id" -> longNumber,
			"label" -> nonEmptyText
		)
	)
	def updateTaskForm(id: Long, label: String): Form[(Long, String)] = {
		baseUpdateTaskForm.fill((id, label))
	}
	
	val loginForm = Form(
		tuple(
			"username" -> nonEmptyText,
			"password" -> nonEmptyText
		)
	)
}
