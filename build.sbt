name := "ZipEntries"

version := "1.0"

scalaVersion := "2.11.8"


libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.5.2",
  "commons-io" % "commons-io" % "2.5",
  "org.scalatest" %% "scalatest" % "2.2.6" % Test
)