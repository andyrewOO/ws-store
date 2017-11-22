package com.andy.util

import scala.language.implicitConversions
/**
 * @author andy
 * 隐世转换方法集合
 */
object MyImplicitTypeConversion {
  implicit def DefautIntToStr(defautInt: Int) = if (defautInt == -1) "" else defautInt.toString
  implicit def IntToBoolean(result: Int) = if (result > 0) true else false
}