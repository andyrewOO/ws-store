package com.andy.constant

import java.sql.Date

/**
 * @author andy
 */
object SysStatus {
  // 初始系统默认无效状态
  val SYS_DEFAUT = -1
  // 数据库记录有效状态
  val SYS_VALID = 1
  // 数据库记录无效状态
  val SYS_INVALID = 0
}

/**
 * 系统默认值参数
 */
object SysDefautV {
  val DEFAUT_INT = -1
  val DEFAUT_DOUBLE = -1
  val DEFAUT_STRING = "DEFAUT"
  val DEFAUT_DATE = new Date(1970)
}