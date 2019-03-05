package com.umana.corso.rental.domain.usecase.actor

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import akka.util.Timeout
import com.umana.corso.rental.domain.exception.{RentMovieException, ReserveMovieException}

import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext
import com.umana.corso.rental.domain.repository.RentRepository
import com.umana.corso.rental.domain.usecase.message.RentalMessages._

class RentActor (rentRepository: RentRepository) extends Actor {

  private implicit val executionContext: ExecutionContext = context.system.dispatcher
  private implicit val timeout: Timeout = Timeout(5.seconds)

  override def receive: Receive = {

    case ReserveMovie(idUser,idMovie,idShop) =>
      rentRepository
        .reserveMovie(idUser,idMovie,idShop)
        .map(_ => ReserveMovieResponse(Right(Unit)))
        .recover {
          case e: ReserveMovieException => ReserveMovieResponse(Left(e))
        }
        .pipeTo(sender())

    case RentMovie(idUser,idMovie,idShop) =>
      rentRepository
        .rentMovie(idUser,idMovie,idShop)
        .map(_ => RentMovieResponse(Right(Unit)))
        .recover {
          case e: RentMovieException => RentMovieResponse(Left(e))
        }
        .pipeTo(sender())

  }

}
  object RentActor {
    def props(reserveRepository: RentRepository): Props = Props(classOf[RentActor], reserveRepository)
  }

