package com.andy.util

import com.andy.mode.bean.Mpeople

/**
 * @author andy
 */
object TestBeanDBUtil {
  def main(args: Array[String]): Unit = {
    val a = Mpeople("1")
    val b = a.getClass.getMethod("id")
    val c = b.invoke(a)
    println(c)
  }
}