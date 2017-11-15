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
    val (fieldsList, tableName) = getClassFieldsAndTable(ob)
    // 组装Insert中的列元素
    val columnStr = fieldsList.toString().replaceFirst("List", "")
    // 组装Insert中的参数?字符
    val paramStr = (for (f <- fieldsList) yield { "?" }).toString().replaceFirst("List", "")
    val sqlStr = s"insert into ${tableName} ${columnStr} values ${paramStr}"
    sqlStr
  }

  /**
   * 获取 case class 中的属性名称和表名
   */
  def getClassFieldsAndTable[T](ob: T) = {
    val (objectCls, tableName) = ob match {
      case Mpeople   => (classOf[Mpeople], TableName.MPEOPLE)
      case MReAddres => (classOf[MReAddres], TableName.MREADDR)
      case MOrder    => (classOf[MOrder], TableName.MORDER)
      case MGoods    => (classOf[MGoods], TableName.MGOODS)
      case MOrGoR    => (classOf[MOrGoR], TableName.MORGOR)
      case SUser     => (classOf[SUser], TableName.SUSER)
    }

    val fields = objectCls.getDeclaredFields
    val fieldNameList = for (field <- fields) yield { field.getName }
    (fieldNameList.toList, tableName)
  }
}