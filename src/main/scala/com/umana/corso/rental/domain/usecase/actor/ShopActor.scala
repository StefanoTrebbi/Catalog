package com.umana.corso.rental.domain.usecase.actor

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import akka.util.Timeout
import com.umana.corso.rental.domain.repository.ShopRepository
import com.umana.corso.rental.domain.usecase.message.ShopMessages._

import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext

class ShopActor (shopRepository: ShopRepository) extends Actor {

  private implicit val executionContext: ExecutionContext = context.system.dispatcher
  private implicit val timeout: Timeout = Timeout(5.seconds)

  override def receive: Receive = {
    case GetShopByIdMovie(idMovie) =>
      shopRepository
        .getShopByIdMovie(idMovie)
        .map(result => GetShopByIdMovieResponse(result))
        .pipeTo(sender())


  }

}
object ShopActor {
  def props(shopRepository: ShopRepository): Props = Props(classOf[ShopActor], shopRepository)
}