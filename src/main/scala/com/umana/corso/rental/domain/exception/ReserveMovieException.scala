package com.umana.corso.rental.domain.exception

sealed trait ReserveMovieException extends RuntimeException
  class MovieNotAvailableForReserve extends ReserveMovieException


