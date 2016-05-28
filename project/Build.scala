import sbt.Keys._
import sbt._

object Build extends sbt.Build {


  val commonSettings = Seq(
    organization := "build.unstable",
    version := "0.2.0",
    scalaVersion := "2.11.8",
    //run <<= run in Compile in core,
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

  lazy val macros = Project(id = "tylog-macros", base = file("macros"))
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= {
        Seq(
          "org.slf4j" % "slf4j-api" % "1.7.21",
          "org.scala-lang" % "scala-reflect" % scalaVersion.value,
          "org.scala-lang" % "scala-compiler" % scalaVersion.value,
          "org.scalatest" %% "scalatest" % "2.2.5" % "test"
        )
      }
    )

  lazy val core = Project(id = "tylog-core", base = file("core"))
    .settings(commonSettings: _*)
    .dependsOn(macros % "compile->compile;test->test")

  lazy val examples = Project(id = "tylog-examples", base = file("examples"))
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"
    ).dependsOn(core % "compile->compile;test->test", macros % "compile->compile;test->test")

  lazy val root = Project(id = "tylog", base = file("."))
    .settings(commonSettings: _*)
    .aggregate(macros, core)
}
