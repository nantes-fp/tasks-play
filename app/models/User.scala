package models

import play.api.db._
import play.api.Play.current

import java.util.UUID

import org.joda.time.DateTime

import anorm._
import anorm.SqlParser._

import AnormType._


case class User(id: UUID, name: String, email: String, createdAt: DateTime, passwordHash: String)

object User {
  val simple = {
    get[UUID]("myuser.id") ~
    get[String]("myuser.name") ~
    get[String]("myuser.email") ~
    get[DateTime]("myuser.created_at") ~
    get[String]("myuser.password_hash") map {
      case id~name~email~created_at~password_hash => User(id, name, email, created_at, password_hash)
    }
  }

  def findById(id: UUID): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where id = {id}").on('id -> id).as(User.simple.singleOpt)
    }
  }

  /**
   * Update a user.
   *
   * @param id The user id
   * @param task The user values.
   */
  def update(id: UUID, user: User) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update myuser
          set name = {name}, email = {email}, created_at = {created_at}, password_hash = {password_hash}
          where id = {id}
        """
      ).on(
        'id -> id,
        'name -> user.name,
        'email -> user.email,
        'created_at -> user.createdAt,
        'password_hash -> user.passwordHash
      ).executeUpdate()
    }
  }
  
  /**
   * Insert a new user.
   *
   * @param user The user values.
   */
  def insert(user: User) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into myuser (id, name, email, created_at, password_hash) values (
            {id}, {name}, {email}, {created_at}, {password_hash}
          )
        """
      ).on(
        'id -> user.id,
        'name -> user.name,
        'email -> user.email,
        'created_at -> user.createdAt,
        'password_hash -> user.passwordHash
      ).executeUpdate()
    }
  }
  
  /**
   * Delete a user.
   *
   * @param id Id of the user to delete.
   */
  def delete(id: UUID) = {
    DB.withConnection { implicit connection =>
      SQL("delete from user where id = {id}").on('id -> id).executeUpdate()
    }
  }
}
