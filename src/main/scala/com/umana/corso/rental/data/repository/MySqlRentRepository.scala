package com.umana.corso.rental.data.repository

import com.umana.corso.rental.domain.exception._

import java.sql.{DriverManager, ResultSet}

import akka.actor.ActorSystem
import com.umana.corso.rental.domain.repository.RentRepository
import scala.concurrent.{ExecutionContext, Future}

class MySqlRentRepository(
                           url: String,
                           user: String,
                           password: String,
                           system: ActorSystem
                         ) extends RentRepository {

  private implicit val executionContext: ExecutionContext = system.dispatchers.lookup("mysql-dispatcher")

  override def reserveMovie(idMovie: String,idUser: String,idShop: String): Future[Unit] = Future {
    var res = false
    try {
      val con = DriverManager.getConnection(url, user, password)
      try {
        val stmt = con.createStatement
        // esecuzione della query
        val sql = s"update moviecopy set status=1,reservationDate=NOW(),IdUser='$idUser' WHERE idMovie='$idMovie' AND idShop='$idShop' AND status=0 LIMIT 1"
        println(sql)
        if (stmt.executeUpdate(sql) == 1)
        // ciclo sul result set che contiene i risultati della query
          res = true
      }
      finally if (con != null) con.close()
    }

    if (res)
      Unit
    else
      throw new MovieNotAvailableForReserve()
  }

  override def rentMovie(idMovie: String,idUser: String,idShop: String): Future[Unit] = Future {
    var res = false
    try {
      val con = DriverManager.getConnection(url, user, password)
      try {
        val stmt = con.createStatement
        // esecuzione della query
        val sql1 = s"update moviecopy set status=2,rentalDate=NOW(),idUser='$idUser' WHERE idMovie='$idMovie' AND idShop='$idShop' AND status=0 LIMIT 1"
        val sql2 = s"update moviecopy set status=2,rentalDate=NOW() WHERE idMovie='$idMovie' AND idShop='$idShop' AND IdUser='$idUser' AND status=1 LIMIT 1"

        if ((stmt.executeUpdate(sql1) == 1) || (stmt.executeUpdate(sql2) == 1))
          res = true
      }
      finally if (con != null) con.close()
    }

    if (res)
      Unit
    else
      throw new MovieNotAvailableForRenting()
  }

}