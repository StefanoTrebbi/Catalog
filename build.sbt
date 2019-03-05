
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

name := "rental-microservice"

version := "0.1"
scalaVersion := "2.12.8"

mainClass in Compile := Some("com.umana.corso.user.Application")
dockerBaseImage := "openjdk:8u181" // a smaller JVM base image

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.21"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.21"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.7"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.47"