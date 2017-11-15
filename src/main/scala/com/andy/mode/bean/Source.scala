package com.andy.mode.bean

import scala.beans.BeanProperty

/**
 * @author andy
 */
case class Mpeople(
  id: String = "",
  name: String = "",
  age: String = "",
  birthday: String = "",
  phone: String = "",
  gender: String = "",
  weixin: String = "",
  remark: String = "",
  status: String = "")

case class MReAddres(
  id: String = "",
  pid: String = "",
  province: String = "",
  city: String = "",
  region: String = "",
  address: String = "",
  phone: String = "",
  receiver: String = "",
  status: String = "")

case class MOrder(
  id: String = "",
  commitId: String = "",
  pid: String = "",
  aid: String = "",
  money: String = "",
  ctime: String = "",
  ptime: String = "",
  dtime: String = "",
  rtime: String = "",
  utime: String = "",
  status: String = "",
  remark: String = "")

case class MGoods(
  id: String = "",
  kind: String = "",
  lprice: String = "",
  sprice: String = "",
  place: String = "",
  description: String = "",
  ctime: String = "",
  dtime: String = "",
  total: String = "",
  remark: String = "",
  status: String = "")

case class MOrGoR(
  id: String = "",
  oid: String = "",
  gid: String = "",
  price: String = "",
  discount: String = "",
  count: String = "",
  remark: String = "",
  status: String = "")

case class SUser(
  id: String = "",
  userId: String = "",
  userName: String = "",
  password: String = "",
  status: String = "")