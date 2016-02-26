scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.1.7" % "test",
  "io.gatling"            % "gatling-test-framework"    % "2.1.7" % "test",
  // http
  "org.apache.httpcomponents" % "fluent-hc" % "4.5.1" % "test"
)

scalacOptions ++= Seq(
  "-feature",
  "-language:postfixOps"
)

enablePlugins(GatlingPlugin)
