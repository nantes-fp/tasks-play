package models

import play.api.db._
import play.api.Play.current

import java.util.UUID
import org.joda.time.DateTime

import anorm._
import anorm.SqlParser._

import AnormType._


case class Task(id: UUID, name: String, dueDate: Option[DateTime], creatorId: UUID, done: Boolean = false)

object Task {
  val simple = {
    get[UUID]("task.id") ~
    get[String]("task.name") ~
    get[Option[DateTime]]("task.due_date") ~
    get[UUID]("task.creator_id") ~
    get[Boolean]("task.done") map {
      case id~name~due_date~creator_id~done => Task(id, name, due_date, creator_id, done)
    }
  }

  val withCreator = Task.simple ~ (User.simple.?) map {
    case task~user => (task, user)
  }

  def getAll(): List[Task] = {
    DB.withConnection { implicit connection =>
      SQL("select * from task").as(Task.simple.*)
    }
  }

  def findById(id: UUID): Option[Task] = {
    DB.withConnection { implicit connection =>
      SQL("select * from task where id = {id}").on('id -> id).as(Task.simple.singleOpt)
    }
  }

  /**
   * Update a task.
   *
   * @param id The task id
   * @param task The task values.
   */
  def update(id: UUID, task: Task) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update task
          set name = {name}, due_date = {due_date}, creator_id = {creator_id}, done = {done}
          where id = {id}
        """
      ).on(
        'id -> id,
        'name -> task.name,
        'due_date -> task.dueDate,
        'creator_id -> task.creatorId,
        'done -> task.done
      ).executeUpdate()
    }
  }
  
  /**
   * Insert a new task.
   *
   * @param task The task values.
   */
  def insert(task: Task) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into task values (
            {id}, {name}, {due_date}, {creator_id}, {done}
          )
        """
      ).on(
        'id -> task.id,
        'name -> task.name,
        'due_date -> task.dueDate,
        'creator_id -> task.creatorId,
        'done -> task.done
      ).executeUpdate()
    }
  }
  
  /**
   * Delete a task.
   *
   * @param id Id of the task to delete.
   */
  def delete(id: UUID) = {
    DB.withConnection { implicit connection =>
      SQL("delete from task where id = {id}").on('id -> id).executeUpdate()
    }
  }
}
