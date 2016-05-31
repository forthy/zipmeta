name := "zipmeta"
organization := "com.snacktrace"
version := "1.0.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.5.2",
  "commons-io" % "commons-io" % "2.5",
  "org.scalatest" %% "scalatest" % "2.2.6" % Test
)

pomExtra in Global := {
  <url>https://github.com/s-nel/zipmeta</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
    </license>
  </licenses>
  <scm>
    <connection>scm:git:github.com/s-nel/zipmeta.git</connection>
    <developerConnection>scm:git:git@github.com:s-nel/zipmeta.git</developerConnection>
    <url>https://github.com/s-nel/zipmeta</url>
  </scm>
  <developers>
    <developer>
      <id>s-nel</id>
      <name>Samuel Nelson</name>
      <url>https://snacktrace.com/</url>
    </developer>
  </developers>
}