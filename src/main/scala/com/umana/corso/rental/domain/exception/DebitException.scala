package com.umana.corso.rental.domain.exception

sealed trait DebitException extends RuntimeException
class InsufficientBalance extends DebitException