package com.umana.corso.rental.domain.repository

import com.umana.corso.rental.domain.model.Shop
import scala.concurrent.Future

trait ShopRepository {
  def getShopByIdMovie(id: String): Future[Seq[Shop]]
}
