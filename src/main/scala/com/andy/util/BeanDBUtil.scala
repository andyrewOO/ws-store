package com.andy.util

import com.andy.mode.bean.Mpeople
import com.andy.mode.bean.SUser
import com.andy.mode.bean.MGoods
import com.andy.mode.bean.MOrder
import com.andy.mode.bean.MOrGoR
import com.andy.mode.bean.MReAddres
import com.andy.constant.TableName
import scala.language.existentials

/**
 * @author andy
 */
object BeanDBUtil {

  /**
   * 生成Case Class 的 Insert语句
   */
  def createInsert[T](ob: T) = {
    val (tableName, fieldsList, valueList) = getClassFieldsAndTable(ob)
    // 组装Insert中的列元素
    val columnStr = fieldsList.toString().replaceFirst("List", "")
    // 组装Insert中的参数?字符
    val paramStr = (for (f <- fieldsList) yield { "?" }).toString().replaceFirst("List", "")
    val sqlStr = s"insert into ${tableName} ${columnStr} values ${paramStr}"
    (sqlStr, valueList)
  }

  /**
   * 获取 case class 中的属性名称和值，并返回表名
   */
  def getClassFieldsAndTable[T](ob: T) = {
    val (objectCls, tableName) = ob match {
      case m: Mpeople   => (classOf[Mpeople], TableName.MPEOPLE)
      case m: MReAddres => (classOf[MReAddres], TableName.MREADDR)
      case m: MOrder    => (classOf[MOrder], TableName.MORDER)
      case m: MGoods    => (classOf[MGoods], TableName.MGOODS)
      case m: MOrGoR    => (classOf[MOrGoR], TableName.MORGOR)
      case m: SUser     => (classOf[SUser], TableName.SUSER)
    }

    val fields = objectCls.getDeclaredFields
    val fieldNameList = for (field <- fields) yield { field.getName }
    val valueList = for (field <- fields) yield { objectCls.getMethod(s"${field.getName}").invoke(ob) }
    (tableName, fieldNameList.toList, valueList.toList)
  }

  /**
   * 将首字母大写
   */
  def getMethodName(fildeName: String): String = {
    var items = fildeName.getBytes()
    items(0) = (items(0).asInstanceOf[Char] - 'a' + 'A').asInstanceOf[Byte]
    new String(items)
  }
}