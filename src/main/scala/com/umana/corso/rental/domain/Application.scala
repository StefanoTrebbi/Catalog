package com.umana.corso.rental.domain

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.umana.corso.rental.api.RestInterface

import scala.concurrent.ExecutionContext

object Application extends App with RestInterface {

  val config = ConfigFactory.load()

  // leggo i parametri a cui il server http deve fare il bind
  val httpHost = config.getString("http.host")
  val httpPort = config.getInt("http.port")

  // leggo i parametri di connessione per mongo
  val mongodbUrl = config.getString("mongodb.url")

  implicit val system: ActorSystem = ActorSystem("users-microservices")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContext = system.dispatcher

  val userRepository: UserRepository = new MongoUserRepository(system, mongodbUrl)

  val userActor: ActorRef = system.actorOf(UserActor.props(userRepository))

  val route = userRoutes

  val bindingFuture = Http().bindAndHandle(route, httpHost, httpPort)
  bindingFuture.map { binding =>
    println(s"REST interface bound to ${binding.localAddress}")

  } recover {
    case ex =>
      println(s"REST interface could not bind to $httpHost:$httpPort", ex.getMessage)
  }

}


}
