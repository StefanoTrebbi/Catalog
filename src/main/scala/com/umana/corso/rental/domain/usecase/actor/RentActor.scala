package com.umana.corso.rental.domain.usecase.actor

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import akka.util.Timeout
import com.umana.corso.rental.domain.exception._

import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext
import com.umana.corso.rental.domain.repository.{CatalogueRepository, RentRepository, UserRepository}
import com.umana.corso.rental.domain.usecase.message.RentalMessages._

class RentActor (rentRepository: RentRepository,userRepository: UserRepository,catalogueRepository: CatalogueRepository) extends Actor {

  private implicit val executionContext: ExecutionContext = context.system.dispatcher
  private implicit val timeout: Timeout = Timeout(5.seconds)

  override def receive: Receive = {

    case ReserveMovie(idUser,idMovie,idShop) =>
      catalogueRepository
        .getPriceByIdMovie(idMovie)
        .map(result => WrapGetPriceByIdMovieResponse(idUser,idMovie,idShop,result))
        .pipeTo(self)(sender)

    case WrapGetPriceByIdMovieResponse(_,_,_,None) =>
      sender() ! ReserveMovieResponse(Left(new InvalidIdMovie()))

    case WrapGetPriceByIdMovieResponse(idUser,idMovie,idShop,Some(price:Double)) =>
      userRepository
          .debit(idUser,price/2)
        .map(_ => WrapDebitResponse(idUser,idMovie,idShop,price,Right(Unit)))
        .recover {
          case e: InsufficientBalance =>WrapDebitResponse(idUser,idMovie,idShop,price,Left(e))
        }
        .pipeTo(self)(sender)

    case WrapDebitResponse(_,_,_,_,Left(_:InsufficientBalance)) =>
      sender() ! ReserveMovieResponse(Left(new InsufficientBalanceForReserve()))

    case WrapDebitResponse(idUser,idMovie,idShop,price,Right(())) =>
      rentRepository
        .reserveMovie(idUser,idMovie,idShop)
        .map(_ => WrapReserveMovieResponse(idUser,idMovie,idShop,price,Right(Unit)))
        .recover {
          case e: ReserveMovieException => WrapReserveMovieResponse(idUser,idMovie,idShop, price, Left(e))
        }
        .pipeTo(self)(sender())

    case WrapReserveMovieResponse(_,_,_,_, Right(())) =>
      sender() ! ReserveMovieResponse(Right(Unit))

    case WrapReserveMovieResponse(idUser,_,_,price,Left(_:ReserveMovieException)) =>
      userRepository.credit(idUser,price/2)
        .map(result => ReserveMovieResponse(Right(Unit)))
        .recover {
          case e: CreditException => ReserveMovieResponse(Left(new InvalidIUserForReserve()))
        }
        .pipeTo(sender())



//  /*
//    case RentMovie(idUser,idMovie,idShop) =>
//      catalogueRepository
//        .getPriceByIdMovie(idMovie)
//        .map(result => WrapGetPriceByIdMovieResponse(idUser,idMovie,idShop,result))
//        .pipeTo(self)(sender)
//
//    case WrapGetPriceByIdMovieResponse(_,_,_,None) =>
//      sender() ! ReserveMovieResponse(Left(new InvalidIdMovie()))
//
//    case WrapGetPriceByIdMovieResponse(idUser,idMovie,idShop,Some(price:Double)) =>
//      userRepository
//        .debit(idUser,price)
//        .map(_ => WrapDebitResponse(idUser,idMovie,idShop,price,Right(Unit)))
//        .recover {
//          case e: InsufficientBalance =>WrapDebitResponse(idUser,idMovie,idShop,price,Left(e))
//        }
//        .pipeTo(self)(sender)
//
//    case WrapDebitResponse(_,_,_,_,Left(_:InsufficientBalance)) =>
//      sender() ! ReserveMovieResponse(Left(new InsufficientBalanceForReserve()))
//
//    case WrapDebitResponse(idUser,idMovie,idShop,price,Right(())) =>
//
//      rentRepository
//        .rentMovie(idUser,idMovie,idShop)
//        .map(_ =>  WrapReserveMovieResponse(idUser,idMovie,idShop,price,Right(Unit)))
//        .recover {
//          case e: RentMovieException => WrapReserveMovieResponse(idUser,idMovie,idShop, price, Left(e))
//        }
//        .pipeTo(self)(sender())
//
//    case WrapReserveMovieResponse(_,_,_,_, Right(())) =>
//      sender() ! ReserveMovieResponse(Right(Unit))
//
//    case WrapReserveMovieResponse(idUser,_,_,price,Left(_:ReserveMovieException)) =>
//      userRepository.credit(idUser,price)
//        .map(result => ReserveMovieResponse(Right(Unit)))
//        .recover {
//          case e: CreditException => ReserveMovieResponse(Left(new InvalidIUserForReserve()))
//        }
//        .pipeTo(sender())
//
//
//
//*/

  }
  private case class WrapGetPriceByIdMovieResponse(idUser:String,idMovie:String,idShop:String,result: Option[Double])
  private case class WrapDebitResponse(idUser:String,idMovie:String,idShop:String,price:Double, result: Either[DebitException, Unit])
  private case class WrapReserveMovieResponse(idUser:String,idMovie:String,idShop:String, price: Double,result: Either[ReserveMovieException, Unit])
  private case class WrapCreditResponse(idUser:String,idMovie:String,idShop:String,price:Double, result: Either[CreditException, Unit])
}
  object RentActor {

    def props(rentRepository: RentRepository,userRepository: UserRepository,catalogueRepository: CatalogueRepository): Props = Props(classOf[RentActor], rentRepository,userRepository,catalogueRepository)
  }

