package controllers

import play.api.mvc._

import java.util.UUID
import org.joda.time.DateTime

object Application extends Controller {
  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def lolo = Action {
    val user = models.User(
      UUID.fromString("4d54e9df-eb2a-4d7e-b72f-bec5dde89cab"),
      "John",
      "john.doe@example.org",
      new DateTime("2013-10-31"),
      ""
    )

    models.User.insert(user)

    Ok("")
  }
}
