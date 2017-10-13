package com.andy.util

import javax.sql.DataSource
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import org.apache.commons.dbcp2.BasicDataSource
import org.apache.commons.dbcp2.BasicDataSourceFactory
import xitrum.util.Loader
import org.apache.commons.dbutils.QueryRunner

/**
 * @author huxp
 */
object MySqlDBUtilTest {
  def main(args: Array[String]): Unit = {
    MySqlDBUtil.getConn()
  }
}