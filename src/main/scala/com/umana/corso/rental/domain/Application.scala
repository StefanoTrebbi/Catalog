package com.umana.corso.rental.domain

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.umana.corso.rental.api.RestInterface
import com.umana.corso.rental.data.repository.{MySqlRentRepository, MySqlShopRepository}
import com.umana.corso.rental.domain.repository.{RentRepository, ShopRepository}
import com.umana.corso.rental.domain.usecase.actor.{RentActor, ShopActor}

import scala.concurrent.ExecutionContext

object Application extends App with RestInterface {

  val config = ConfigFactory.load()

  // leggo i parametri a cui il server http deve fare il bind
  val httpHost = config.getString("http.host")
  val httpPort = config.getInt("http.port")

  // leggo i parametri di connessione per mysql
  val mySqlUrl = config.getString("mysql.url")
  val name = config.getString("mysql.name")
  val password = config.getString( "mysql.password")

  implicit val system: ActorSystem = ActorSystem("rentalmovie-microservices")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContext = system.dispatcher

  val shopRepository: ShopRepository = new MySqlShopRepository(mySqlUrl,name,password,system)
  val shopActor: ActorRef = system.actorOf(ShopActor.props(shopRepository))

  val rentRepository: RentRepository = new MySqlRentRepository(mySqlUrl,name,password,system)
  val rentActor: ActorRef = system.actorOf(RentActor.props(rentRepository))


  val route = rentalRoutes
  val bindingFuture = Http().bindAndHandle(route, httpHost, httpPort)
  bindingFuture.map { binding =>
    println(s"REST interface bound to ${binding.localAddress}")

  } recover {
    case ex =>
      println(s"REST interface could not bind to $httpHost:$httpPort", ex.getMessage)
  }
}
