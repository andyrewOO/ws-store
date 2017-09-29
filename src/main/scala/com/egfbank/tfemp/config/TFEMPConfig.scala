package com.egfbank.tfemp.config

import com.typesafe.config.ConfigFactory
import xitrum.util.Loader

/**
 * TFEMP的配置类
 */

object TFEMPConfig {

  lazy val conf = Loader.propertiesFromClasspath("tfemp.properties")

  /**
   * ESB连接地址
   */
  lazy val ESBUrl = conf.getProperty("esburl")

  /**
   * 获取指定名称的配置
   */
  def getString(key: String) = conf.getProperty(key)

}