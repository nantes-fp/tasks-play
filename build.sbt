import play.Project._

name := "tasks"

version := "1.0"

libraryDependencies ++= Seq(jdbc, anorm)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies ++= Seq(
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "joda-time" % "joda-time" % "2.0",
  "org.joda" % "joda-convert" % "1.1"
)

playScalaSettings
