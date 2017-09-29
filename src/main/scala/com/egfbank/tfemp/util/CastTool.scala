package com.egfbank.tfemp.util

import scala.collection.mutable.HashMap
import xitrum.Log
import xitrum.util.Loader

/**
 * 映射关系
 * @author andy
 */
object CastTool extends Log {

  val currency = Loader.propertiesFromClasspath("common/currency.properties")

  val certificate = Loader.propertiesFromClasspath("common/certificate.properties")

  /**
   * 币种映射
   */
  def currencyCast(cType: String): String = {
    currency.getProperty(cType, "")
  }

  /**
   * 证件类型映射
   */
  def certificateCast(cType: String): String = {
    certificate.getProperty(cType, "")
  }
}