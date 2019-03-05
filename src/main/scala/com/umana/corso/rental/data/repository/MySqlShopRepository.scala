package com.umana.corso.rental.data.repository

import java.sql.DriverManager

import akka.actor.ActorSystem
import com.umana.corso.rental.domain.model.Shop
import com.umana.corso.rental.domain.repository.ShopRepository

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

class MySqlShopRepository (
                             url: String,
                             user: String,
                             password: String,
                             system: ActorSystem
                            ) extends ShopRepository {

  private implicit val executionContext: ExecutionContext = system.dispatchers.lookup("mysql-dispatcher")

    override def getShopByIdMovie(idToFind:String) : Future[Seq[Shop]] = Future {
      val shopList = ListBuffer.empty[Shop]

      try {
        val con = DriverManager.getConnection(url, user, password)
        try {
          val stmt = con.createStatement
          // esecuzione della query
          val rs = stmt.executeQuery(s"select id,name,city,address from shop where id in (select distinct idShop from moviecopy inner join shop on moviecopy.idShop=shop.id where status = 0 and moviecopy.idMovie='$idToFind')")
          // ciclo sul result set che contiene i risultati della query
          while (rs.next()) {
            val id = rs.getString(1)
            val name = rs.getString(2)
            val city = rs.getString(3)
            val address = rs.getString(4)
            val shop = Shop(id, name, city, address)
            shopList += shop
          }
        }
        finally if (con != null) con.close()
      }

      shopList
    }

  }
