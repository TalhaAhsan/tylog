import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys._

object Build extends sbt.Build {

  val slf4j = "1.7.21"

  val commonSettings = Seq(
    organization := "build.unstable",
    version := "0.3.0",
    scalaVersion := "2.11.8",
    licenses +=("MIT", url("https://opensource.org/licenses/MIT")),
    scalacOptions := Seq(
      "-unchecked",
      "-Xlog-free-terms",
      "-deprecation",
      "-language:experimental.macros",
      "-encoding", "UTF-8",
      "-Xlint",
      "-Ywarn-dead-code",
      "-target:jvm-1.8"
    )
  )

  lazy val core = Project(id = "tylog-core", base = file("core"))
    .settings(commonSettings: _*)

  lazy val macros = Project(id = "tylog-macros", base = file("macros"))
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= {
        Seq(
          "org.slf4j" % "slf4j-api" % slf4j,
          "org.scala-lang" % "scala-reflect" % scalaVersion.value,
          "org.scala-lang" % "scala-compiler" % scalaVersion.value
        )
      }
    ).dependsOn(core % "compile->compile")

  lazy val tylog = Project(
    id = "tylog",
    base = file("."),
    dependencies = Seq(core, macros),
    aggregate = Seq(core, macros)
  ).settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= {
        Seq(
          "org.slf4j" % "slf4j-api" % slf4j,
          "org.scalatest" %% "scalatest" % "2.2.5" % "test"
        )
      })

  lazy val examples = Project(id = "tylog-examples", base = file("examples"))
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"
    ).dependsOn(tylog)

  override def rootProject: Option[Project] = Some(tylog)
}
