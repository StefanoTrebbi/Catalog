package com.umana.corso.rental.domain.repository

import scala.concurrent.Future

trait RentRepository {
  def reserveMovie(IdMovie:String,idUser:String,IdShop:String): Future[Unit]
  def rentMovie(IdMovie:String,idUser:String,IdShop:String): Future[Unit]
}
