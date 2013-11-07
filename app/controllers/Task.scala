package controllers

import java.util.UUID
import scala.util.Try

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._

import org.joda.time.DateTime

import play.api.mvc._

object Task extends Controller {
  private def stringToUUID(s: String): Option[UUID] = Try(UUID.fromString(s)).toOption

  val user = models.User(
    UUID.fromString("4d54e9df-eb2a-4d7e-b72f-bec5dde89cab"),
    "John",
    "john.doe@example.org",
    new DateTime("2013-10-31"),
    ""
  )

  case class TaskData(name: String, done: Boolean, dueDate: Option[DateTime])

  val taskForm = Form(
    mapping(
      "name" -> text,
      "done" -> boolean,
      "due_date" -> optional(of[DateTime])
    )(TaskData.apply)(TaskData.unapply)
  )

  def getAll = Action { implicit request =>
    val tasks = models.Task.getAll

    Ok(views.html.taskindex(tasks))
  }

  def create = Action { implicit request =>
    Ok(views.html.taskpagecreate(taskForm))
  }

  def save = Action { implicit request =>
    val form = taskForm.bindFromRequest

    form.fold(
      es => BadRequest(views.html.taskpagecreate(es)),
      td => {
        val id = UUID.randomUUID()
        val task = models.Task(id, td.name, td.dueDate, user.id, td.done)

        models.Task.insert(task)

        Redirect(routes.Task.show(id.toString))
      }
    )
  }

  def show(id: String) = Action { implicit request =>
    (for {
      u <- stringToUUID(id)
      t <- models.Task.findById(u)
    } yield Ok(views.html.taskpage(t))) getOrElse NotFound
  }

  def edit(id: String) = Action { implicit request =>
    (for {
      u <- stringToUUID(id)
      t <- models.Task.findById(u)
    } yield {
      val f = taskForm.fill(TaskData(t.name, t.done, t.dueDate))
      Ok(views.html.taskpageedit(t.id, f))
    }) getOrElse NotFound
  }

  def update(id: String) = Action { implicit request =>
    (for {
      u <- stringToUUID(id)
      t <- models.Task.findById(u)
    } yield {
      val form = taskForm.bindFromRequest

      form.fold(
        es => BadRequest(views.html.taskpageedit(t.id, es)),
        td => {
          val newTask = t.copy(
            name = td.name,
            dueDate = td.dueDate,
            done = td.done
          )

          models.Task.update(newTask.id, newTask)

          Redirect(routes.Task.show(newTask.id.toString))
        }
      )
    }) getOrElse NotFound
  }

  def delete(id: String) = Action { implicit request =>
    (for {
      u <- stringToUUID(id)
      t <- models.Task.findById(u)
    } yield {
      models.Task.delete(t.id)
      Redirect("/")
    }) getOrElse NotFound
  }
}
