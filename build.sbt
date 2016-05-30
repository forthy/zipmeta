name := "zipmeta"
organization := "com.snacktrace"
version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.8"


libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.5.2",
  "commons-io" % "commons-io" % "2.5",
  "org.scalatest" %% "scalatest" % "2.2.6" % Test
)
