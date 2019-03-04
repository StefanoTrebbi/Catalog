package com.umana.corso.rental.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.umana.corso.rental.domain.model.Shop
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val shopJsonFormat = jsonFormat4(Shop)

}
