package com.umana.corso.rental.api

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, onSuccess, path, pathEnd, pathPrefix, post}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.umana.corso.rental.api.model.{ErrorCode, NewMovieCopy}
import com.umana.corso.rental.domain.exception.MovieNotAvailableForRenting
import com.umana.corso.rental.domain.model.Shop
import com.umana.corso.rental.domain.usecase.message.ShopMessages._
import com.umana.corso.rental.domain.usecase.message.RentalMessages._

import scala.concurrent.duration.DurationDouble

trait RestInterface extends JsonSupport {

  val shopActor: ActorRef
  val rentActor: ActorRef
  private implicit val timeout: Timeout = Timeout(5.seconds)

  lazy val rentalRoutes: Route =
    pathPrefix("rental") {
    concat(
        pathEnd {
          concat(
            // POST /rent
            post {
              entity(as[NewMovieCopy]){ movie=>
                val response = rentActor ? RentMovie(movie.idMovie, movie.idUser, movie.idShop)
                onSuccess(response) {
                  case RentMovieResponse(Right(())) => complete(StatusCodes.OK)
                  case RentMovieResponse(Left(_: MovieNotAvailableForRenting)) => complete(StatusCodes.NotFound, ErrorCode("MovieNotAvailableForRenting"))
                  case _ => complete(StatusCodes.InternalServerError)
                }
              }
            }
          )
        },
      pathPrefix("movie") {
        path(Segment) {
          idMovie =>
            concat(
              // GET /movie/$idShop
              get {
                val response = shopActor ? GetShopByIdMovie(idMovie)
                onSuccess(response) {
                  case GetShopByIdMovieResponse(shop: Seq[Shop]) => complete(StatusCodes.OK, shop)
                  case _ => complete(StatusCodes.InternalServerError)
                }
              }
            )
        }
      },
      path("reserve"){
        pathEnd {
          concat(
            // POST /reserve
            post {
              entity(as[NewMovieCopy]){ movie=>
                val response = rentActor ? ReserveMovie(movie.idMovie, movie.idUser, movie.idShop)
                onSuccess(response) {
                  case ReserveMovieResponse(Right(())) => complete(StatusCodes.OK)
                  case ReserveMovieResponse(Left(_: MovieNotAvailableForRenting)) => complete(StatusCodes.NotFound, ErrorCode("MovieNotAvailableForReserve"))
                  case _ => complete(StatusCodes.InternalServerError)
                }
              }
            }
          )
        }
      }
    )
  }
}


