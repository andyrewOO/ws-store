package com.andy.mode.bean

import java.sql.Date

import com.andy.constant.SysDefautV
import com.andy.constant.SysStatus

/**
 * @author andy
 */
case class Mpeople(
  id: String = SysDefautV.DEFAUT_STRING,
  name: String = SysDefautV.DEFAUT_STRING,
  age: Int = SysDefautV.DEFAUT_INT,
  birthday: Date = SysDefautV.DEFAUT_DATE,
  phone: String = SysDefautV.DEFAUT_STRING,
  gender: Int = SysDefautV.DEFAUT_INT,
  weixin: String = SysDefautV.DEFAUT_STRING,
  remark: String = SysDefautV.DEFAUT_STRING,
  status: Int = SysStatus.SYS_DEFAUT)

case class MReAddres(
  id: String = SysDefautV.DEFAUT_STRING,
  pid: String = SysDefautV.DEFAUT_STRING,
  province: String = SysDefautV.DEFAUT_STRING,
  city: String = SysDefautV.DEFAUT_STRING,
  region: String = SysDefautV.DEFAUT_STRING,
  address: String = SysDefautV.DEFAUT_STRING,
  phone: String = SysDefautV.DEFAUT_STRING,
  receiver: String = SysDefautV.DEFAUT_STRING,
  status: Int = SysStatus.SYS_DEFAUT)

case class MOrder(
  id: String = SysDefautV.DEFAUT_STRING,
  commitId: String = SysDefautV.DEFAUT_STRING,
  pid: String = SysDefautV.DEFAUT_STRING,
  aid: String = SysDefautV.DEFAUT_STRING,
  money: Double = SysDefautV.DEFAUT_DOUBLE,
  ctime: Date = SysDefautV.DEFAUT_DATE,
  ptime: Date = SysDefautV.DEFAUT_DATE,
  dtime: Date = SysDefautV.DEFAUT_DATE,
  rtime: Date = SysDefautV.DEFAUT_DATE,
  utime: Date = SysDefautV.DEFAUT_DATE,
  status: Int = SysStatus.SYS_DEFAUT,
  remark: String = SysDefautV.DEFAUT_STRING)

case class MGoods(
  id: String = SysDefautV.DEFAUT_STRING,
  kind: Int = SysDefautV.DEFAUT_INT,
  lprice: Double = SysDefautV.DEFAUT_DOUBLE,
  sprice: Double = SysDefautV.DEFAUT_DOUBLE,
  place: String = SysDefautV.DEFAUT_STRING,
  description: String = SysDefautV.DEFAUT_STRING,
  ctime: Date = SysDefautV.DEFAUT_DATE,
  dtime: Date = SysDefautV.DEFAUT_DATE,
  total: Int = SysDefautV.DEFAUT_INT,
  remark: String = SysDefautV.DEFAUT_STRING,
  status: Int = SysStatus.SYS_DEFAUT)

case class MOrGoR(
  id: String = SysDefautV.DEFAUT_STRING,
  oid: String = SysDefautV.DEFAUT_STRING,
  gid: String = SysDefautV.DEFAUT_STRING,
  price: Double = SysDefautV.DEFAUT_DOUBLE,
  discount: Double = SysDefautV.DEFAUT_DOUBLE,
  count: Int = SysDefautV.DEFAUT_INT,
  remark: String = SysDefautV.DEFAUT_STRING,
  status: Int = SysStatus.SYS_DEFAUT)

case class SUser(
  id: String = SysDefautV.DEFAUT_STRING,
  userId: String = SysDefautV.DEFAUT_STRING,
  userName: String = SysDefautV.DEFAUT_STRING,
  password: String = SysDefautV.DEFAUT_STRING,
  status: Int = SysStatus.SYS_DEFAUT)