package com.umana.corso.rental.api

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.Uri.Path.Segment
import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, onSuccess, path, pathEnd, pathPrefix, post}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.umana.corso.rental.domain.model.Shop
import com.umana.corso.rental.domain.usecase.message.RentalMessages.{GetShopByIdMovie, GetShopByIdMovieResponse}

import scala.concurrent.duration.DurationDouble

trait RestInterface extends JsonSupport {

  val rentalActor: ActorRef

  private implicit val timeout: Timeout = Timeout(5.seconds)

  lazy val rentalRoutes: Route = pathPrefix("rental") {
    concat (
      path("movies") {
        path(Segment) {
          idMovie =>
            concat(
              // GET /users/$ID
              get {
                val response = rentalActor ? GetShopByIdMovie(idMovie)
                onSuccess(response) {
                  case GetShopByIdMovieResponse(shop: Seq[Shop]) => complete(StatusCodes.OK, shop)
                  case GetShopByIdMovieResponse(None) => complete(StatusCodes.NotFound)
                  case _ => complete(StatusCodes.InternalServerError)
                }

              }
            )
        }
      }
    )
  }
}


