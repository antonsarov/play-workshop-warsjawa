name := """jobsmapper"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "org.webjars" % "angularjs" % "1.2.23",
  "org.webjars" % "angular-leaflet-directive" % "0.7.7",
  "org.webjars" % "leaflet" % "0.7.3",
  "org.webjars" % "bootstrap" % "3.2.0"
)
