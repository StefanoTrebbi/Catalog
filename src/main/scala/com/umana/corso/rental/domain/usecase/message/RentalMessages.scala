package com.umana.corso.rental.domain.usecase.message

import com.umana.corso.rental.domain.model.Shop

object RentalMessages {

  //region GetShopByIdMovie

  case class GetShopByIdMovie(idMovie : String)
  case class GetShopByIdMovieResponse(result:Seq[Shop])

  // endregion

}
