import io.gatling.sbt.GatlingPlugin
import sbt.Keys._
import sbt._

object GatlingWrapperBuild extends Build {
  import scala.Console.{ RED, RESET }
  val dismissTest = taskKey[Unit]("dismiss test command")

  lazy val gatlingWrapper = (project in file(".")).
    settings(
      name := "gatling-wrapper",
      version := "0.0.1",
      scalaVersion := "2.11.7",
      libraryDependencies ++= Seq(
        "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.1.7" % "test",
        "io.gatling" % "gatling-test-framework" % "2.1.7" % "test",
        // http
        "org.apache.httpcomponents" % "fluent-hc" % "4.5.1" % "test",
        // thrift
        "org.apache.thrift" % "libthrift" % "0.9.2"
      ),
      scalacOptions ++= Seq(
        "-unchecked",
        "-deprecation",
        "-feature",
        "-language:postfixOps"
      ),
      dismissTest := println(s"$RED`test` is not supported, please use `testOnly`$RESET"),
      test <<= dismissTest
    )
    .enablePlugins(GatlingPlugin)
}
