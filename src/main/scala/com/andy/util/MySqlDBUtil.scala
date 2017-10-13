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
object MySqlDBUtil {

  /**
   * 初始化连接池
   */
  lazy val dataSourece: DataSource = BasicDataSourceFactory.createDataSource(Loader.propertiesFromClasspath("dbcp_mysql.properties"))

  /**
   * 获取queryRunner
   * @return
   */
  def getOracleQueryRunner(): QueryRunner = {
    new QueryRunner(dataSourece, true)
  }

  /**
   * 获取连接
   *
   * @return
   */
  def getConn(): Connection = {
    try {
      return dataSourece.getConnection();
    } catch {
      case ex: SQLException => {
        ex.printStackTrace()
      }
    }
    return null;
  }

  /**
   * 释放资源
   *
   * @param rs
   * @param ps
   * @param conn
   */
  def close(rs: ResultSet = null, ps: PreparedStatement = null, conn: Connection = null): Unit = {
    try {
      if (rs != null)
        try {
          rs.close();
        } catch {
          case ex: SQLException => {
            ex.printStackTrace()
          }
        }
    } finally {
      try {
        if (ps != null)
          try {
            ps.close();
          } catch {
            case ex: SQLException => {
              ex.printStackTrace()
            }
          }
      } finally {
        if (conn != null)
          try {
            conn.close();
          } catch {
            case ex: SQLException => {
              ex.printStackTrace()
            }
          }
      }
    }
  }

}