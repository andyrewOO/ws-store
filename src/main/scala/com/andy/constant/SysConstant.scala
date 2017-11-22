package com.andy.constant

import java.util.Date

/**
 * @author andy
 */
object SysStatus {
  // 数据库记录有效状态
  val SYS_VALID = 1
  // 数据库记录无效状态
  val SYS_INVALID = 0
}

object OperType {
  val BASE = "base"
  val SEARCH = "search"
  val ADD = "add"
  val UPDATE = "update"
}

/**
 * 系统默认值参数
 */
object SysDefautV {
  val DEFAUT_INT = -1
  val DEFAUT_DOUBLE = -1
  val DEFAUT_STRING = ""
  val DEFAUT_DATE = new Date
  val DEFAUT_NULL = null
}