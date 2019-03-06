package com.umana.corso.rental.domain.exception

sealed trait PriceException extends RuntimeException
class PriceNotFind extends PriceException