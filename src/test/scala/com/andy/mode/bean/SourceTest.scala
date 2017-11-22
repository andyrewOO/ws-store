package com.andy.mode.bean

import org.apache.commons.beanutils.BeanUtils
import scala.collection.JavaConverters._
import scala.reflect.runtime.universe._
import com.andy.util.BeanDBUtil
import java.util.Date
import com.andy.mode.UpdateResult

/**
 * @author andy
 */
object SourceTest {
  def main(args: Array[String]): Unit = {
    val a = Mpeople(id="123")
    println(System.currentTimeMillis())
    val (b1, b2) = BeanDBUtil.createInsert(a)
    println(System.currentTimeMillis())
    println(b1)
    
    val datetime = new Date
    println(datetime)

  }
}
