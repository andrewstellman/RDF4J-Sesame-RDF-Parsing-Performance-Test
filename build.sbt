lazy val commonSettings = Seq(
  name := "rdf-parse-performace-test",
  organization := "com.stellmangreene",
  version := "1.0",
  scalaVersion := "2.12.2"
)

scalaVersion := "2.12.2"
scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
   "io.netty" % "netty-all" % "4.1.15.Final",
   "org.eclipse.rdf4j" % "rdf4j-runtime" % "2.2.2",
   "org.openrdf.sesame" % "sesame-runtime" % "2.7.9",
   "com.github.pathikrit" %% "better-files" % "3.1.0",

   // Log dependencies
   "org.slf4j" % "slf4j-api" % "1.7.25",
   "org.slf4j" % "slf4j-simple" % "1.7.25",
   "org.log4s" %% "log4s" % "1.3.5"
)

// sbt-eclipse settings
EclipseKeys.withSource := true

