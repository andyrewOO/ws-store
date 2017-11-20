package com.andy.actor.dao

import com.andy.mode.bean.MGoods
import com.andy.mode.Page
import java.sql.Connection
import com.andy.constant.TableName
import org.apache.commons.lang3.StringUtils
import com.andy.constant.SearchOperater
import com.andy.util.MySqlDBUtil

/**
 * @author andy
 */
object MGoodsDaoTest extends MGoodsDao {
  def main(args: Array[String]): Unit = {
    implicit val connect = MySqlDBUtil.getConn()
    val goods = MGoods("11110", lprice = 100)
    insert(goods)
    connect.commit()
  }
}