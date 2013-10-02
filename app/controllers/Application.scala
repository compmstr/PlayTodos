package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models.User
import util.Utils

object Application extends Controller {
	
  def index = Action {
		request =>
			request.session.get("userId").map {
				userId =>
					Redirect(routes.Tasks.tasks)
			}.getOrElse{
				Ok(views.html.index(loginForm))
			}
  }
	
	def login = Action {
		implicit request =>
			loginForm.bindFromRequest.fold(
				errors => BadRequest(views.html.index(errors)),
				vals => {
					val (username, pass) = vals
					println("Username: %s - Pass: %s -- res: %s".format(username, pass, User.login(username, pass)))
					User.login(username, pass) match {
						//case None => Redirect(routes.Application.index())
						case None => BadRequest(views.html.index(loginForm.withGlobalError("Login Failed")))
						case Some(user) => Redirect(routes.Tasks.tasks).withSession("userId" -> user.uid.toString)
					}
				}
			)
	}
	def logout = Action {
		implicit request =>
			Redirect(routes.Application.index).withSession(session - "userId")
	}

	val loginForm = Form(
		tuple(
			"username" -> nonEmptyText,
			"password" -> nonEmptyText
		)
	)
}
