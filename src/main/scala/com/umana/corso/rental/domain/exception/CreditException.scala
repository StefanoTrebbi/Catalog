package com.umana.corso.rental.domain.exception

sealed trait CreditException  extends RuntimeException
class ErrorIdUser extends CreditException
