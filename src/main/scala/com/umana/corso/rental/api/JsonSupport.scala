package com.umana.corso.rental.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.umana.corso.rental.api.model.{ErrorCode,NewMovieCopy}
import com.umana.corso.rental.domain.model.Shop
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val shopJsonFormat = jsonFormat4(Shop)
  implicit val errorCodeJsonFormat = jsonFormat1(ErrorCode)
  implicit val newMovieCopyJsonFormat = jsonFormat3(NewMovieCopy)
}
