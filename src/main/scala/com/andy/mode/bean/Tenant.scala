package com.hfbank.mode.bean

import java.util.Date

/**
 * @author andy
 */

case class TenantUser(
  userId: String,
  userName: String,
  password: String,
  phone: String,
  mail: String,
  creationTime: String,
  creator: String,
  updatedTime: String,
  updater: String,
  status: String,
  remark: String)

case class TenantAPP(
  appId: String,
  userId: String,
  appName: String,
  description: String,
  creationTime: String,
  creator: String,
  updatedTime: String,
  updater: String,
  status: String)

case class TenantPartion(
  partId: String,
  appId: String,
  partName: String,
  description: String,
  creationTime: String,
  creator: String,
  updatedTime: String,
  updater: String,
  status: String)

case class TenantMessage(
  id: String,
  appId: String,
  code: String,
  name: String,
  description: String,
  creationTime: String,
  creator: String,
  updatedTime: String,
  updater: String,
  messagePath: String,
  interfacePath: String,
  status: String)

case class TenantInterface(
  id: String,
  appId: String,
  code: String,
  name: String,
  description: String,
  url: String,
  creationTime: String,
  creator: String,
  updatedTime: String,
  updater: String,
  method: String,
  request: String,
  response: String,
  responseExample: String,
  timeOut: String,
  retryTimes: String,
  interval_ : String,
  status: String)

case class TenantSource(
  id: String,
  appId: String,
  sourceId: String,
  type_ : String,
  description: String,
  creationTime: String,
  creator: String,
  updatedTime: String,
  updater: String,
  status: String)
  
// 对应v_tenant_source 视图对象
case class TenantSourceV(
  id: String,
  sourceId: String,
  name: String,
  appId: String,
  appName: String,
  userId: String,
  userName: String,
  type_ : String,
  description: String,
  creationTime: String,
  creator: String,
  updatedTime: String,
  updater: String,
  status: String)