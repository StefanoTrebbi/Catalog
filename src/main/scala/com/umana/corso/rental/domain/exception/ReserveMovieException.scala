package com.umana.corso.rental.domain.exception

sealed trait ReserveMovieException extends RuntimeException
  class MovieNotAvailableForReserve extends ReserveMovieException
  class InsufficientBalanceForReserve extends ReserveMovieException
  class InvalidIdMovie extends ReserveMovieException
class InvalidIUserForReserve extends ReserveMovieException


