package models

import anorm._
import play.api.db.DB
import play.api.Play.current
import java.util.UUID
import anorm.SqlParser._
import org.joda.time.Instant
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.sql.Timestamp

object AnormType {
  
  // Column read
  implicit def rowToUUID: Column[UUID] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case d: UUID => Right(d)
      case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to UUID for column " + qualified))
    }
  }
  
  implicit def rowToInstant: Column[Instant] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case d: Instant => Right(d)
      case d: Timestamp => Right(new Instant(d))
      case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to Instant for column " + qualified))
    }
  }
  
  // statement write
  implicit val uuidToStatement = new ToStatement[UUID] {
    def set(s: java.sql.PreparedStatement, index: Int, aValue: UUID): Unit = s.setObject(index, aValue)
  }
  
  // statement write
  implicit val instantToStatement = new ToStatement[Instant] {
    def set(s: java.sql.PreparedStatement, index: Int, aValue: Instant): Unit = s.setTimestamp(index, new java.sql.Timestamp(aValue.toDate.getTime))
//    def set(s: java.sql.PreparedStatement, index: Int, aValue: Instant): Unit = s.setObject(index, "TIMESTAMP '" + dateToSQL(aValue) + "'")
  }
  
  
  // utils
  private val fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss'");
  
  def dateToSQL(i:Instant): String =  { 
    fmt print i
  }
  
  implicit def rowToDateTime: Column[DateTime] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
      value match {
        case ts: java.sql.Timestamp => Right(new DateTime(ts.getTime))
        case d: java.sql.Date => Right(new DateTime(d.getTime))
        case str: java.lang.String => Right(fmt.parseDateTime(str))  
        case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass) )
      }
  }

  implicit val dateTimeToStatement = new ToStatement[DateTime] {
    def set(s: java.sql.PreparedStatement, index: Int, aValue: DateTime): Unit = {
      s.setTimestamp(index, new java.sql.Timestamp(aValue.withMillisOfSecond(0).getMillis()) )
    }
  }
}

