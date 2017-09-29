package com.egfbank.tfemp.util

import java.text.SimpleDateFormat
import java.util.ArrayList

import scala.collection.immutable
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.util.Random
import scala.xml.Elem
import scala.xml.XML

import org.apache.commons.beanutils.BeanUtils

import com.egfbank.tfemp.config.TFEMPConfig

/**
 * @author XJW
 */
object ToolUtils {

  /**
   * 生成传输报文流水号
   */
  def getTransSerialNumber(): String = {
    TFEMPConfig.getString("efgOrgNum") + "_" + TFEMPConfig.getString("efgDefaultTrustOrg") + getTime() + getRandom()
  }

  /**
   * 调用方系统流水号
   */
  def createCnsmSysSeqNo(): String = {
    getTime() + HfbkUtil.genRandomNumber(3)
  }

  /**
   * 获取系统时间
   */
  def getTime(): String = {
    val dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    dateFormat.format(System.currentTimeMillis())
  }

  /**
   * 生成随机数
   */
  private[this] def getRandom(): String = {
    val random = Random.nextLong()
    var result = Math.abs(random).toString()
    while (result.length() < 11) {
      var m = new StringBuilder("0")
      result = m.append(result).toString()
    }
    if (result.length() >= 11) result = result.substring(0, 11).toString()
    result
  }

  /**
   *
   * Bean 对象转换成 Map 结构
   * @param obj
   * @return
   */
  def BeanToMap(obj: Any): Map[String, String] = {
    val map = BeanUtils.describe(obj)
    map.remove("class")
    javaMap2ScalaMap(map)
  }

  /**
   *
   * 把 Map 中的数据复制到 Bean对象上
   * @param bean
   * @param map
   */
  def MapToBean(bean: Any, map: java.util.Map[String, String]) = {
    BeanUtils.populate(bean, map)
  }

  /**
   * 从 beanMap 中获取 field 节点的值，为一个List[Map[]]
   * 最后把每个map转换成一个bean，返回List[beanType]
   */
  def getBeanList[T](beanType: T, field: String, beanMap: Map[String, Any]) = {
    val fieldListData = beanMap(field).asInstanceOf[List[Map[String, String]]]

    val beanList = new ArrayList[T]
    for (fieldData <- fieldListData) {
      val bean = beanType.getClass().newInstance()
      BeanUtils.populate(bean, scalaMap2JavaHashMap(fieldData))
      beanList.add(bean)
    }
    beanList
  }

  /**
   * 把Scala中的 Map 转换成Java的HashMap
   * @param java.util.HashMap
   * @return
   */
  def scalaMap2JavaHashMap[T](map: Map[String, T]) = {
    val javaHashMap = new java.util.HashMap[String, T]()
    map.foreach { x =>
      javaHashMap.put(x._1, x._2)
    }
    javaHashMap
  }

  /**
   * Java Map 转换成  Scala 中的 Map
   */
  def javaMap2ScalaMap(map: java.util.Map[String, String]): Map[String, String] = {
    val jhashMap: HashMap[String, String] = new HashMap[String, String]
    val it = map.entrySet().iterator()
    while (it.hasNext()) {
      val entry = it.next()
      jhashMap += entry.getKey -> entry.getValue
    }
    jhashMap.toMap
  }

  /**
   * Java 中的List 转换成 Scala 中的 List
   */
  def JavaList2scalaList[T](jlist: java.util.List[T]): List[T] = {
    val sList = new ListBuffer[T]
    val it = jlist.iterator()
    while (it.hasNext()) {
      sList += it.next()
    }
    sList.toList
  }
  
  /**
   * Java 中的List 转换成 Scala 中的 List
   */
  def scalaList2JavaList[T](list: List[T]):java.util.List[T] = {
	  val jList = new java.util.ArrayList[T]
    list.map { x => jList.add(x) }
    jList
  }

  /**
   * 从XML中取值
   */
  def getFVFromXml(field: String, xml: String): String = {
    (XML.loadString(xml) \\ field).text.toString
  }

  /**
   * 从XML中取值
   */
  def getFVFromXml(field: String, xml: Elem): String = {
    (xml \\ field).text.toString
  }

  /**
   * 字符串首字母变小写
   */
  def firstChar2Lower(field: String): String = {
    field(0).toLower + field.substring(1, field.length)
  }

  /**
   * 字符串首字母变大写
   */
  def firstChar2Upper(field: String): String = {
    field(0).toUpper + field.substring(1, field.length)
  }

  /**
   * 把 List中的Map的key都变成小写
   */
  def lowwerKey[T](sourceList: List[Map[String, T]]) = {
    val targetList = for (itemList <- sourceList) yield {
      for (itemMap <- itemList) yield {
        itemMap._1.toLowerCase -> itemMap._2
      }
    }
    targetList
  }

  /**
   * 把Map中的key都变成小写
   */
  def lowwerKey[T](sourceMap: immutable.Map[String, T]) = {
    val targetMap = for (item <- sourceMap) yield {
      item._1.toLowerCase -> item._2
    }
    targetMap
  }
  
  /**
   * 比较两个日期的大小:
   * day1 大于 day2： 1
   * day1 等于 day2：0
   * day1 小于 day2： -1
   */
  def compareDay(day1: String, day2: String) = {
    var df:SimpleDateFormat=new SimpleDateFormat("yyyyMMdd")
    df.parse(day1).compareTo(df.parse(day2))
  }

}