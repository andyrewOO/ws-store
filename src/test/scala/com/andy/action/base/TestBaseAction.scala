package com.andy.action.base

import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.DefaultFormats

/**
 * @author andy
 */
object TestBaseAction {
  def main(args: Array[String]): Unit = {
    val jsonTest = """ { "numbers" : [1, 2, 3, 4] } """

  }

  /**
   * 根据JValue 获取泛型所对应的对象
   */
  protected def getBean[A: Manifest](json: JValue): A = {
    implicit val formats = DefaultFormats
    json.extract[A]
  }

}