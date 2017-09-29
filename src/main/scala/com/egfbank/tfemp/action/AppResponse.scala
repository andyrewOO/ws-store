package com.egfbank.tfemp.action

/**
 * 返回结果封装
 * @author huxp
 */
sealed trait AppResponse

/**
 * 登录校验结果封装
 */
final case class LoginCheckResponse(flag: String, msg: String) extends AppResponse

/**
 * 异常结果封装
 */
final case class ExceptionResponse(status: String, messages: String, errorCode: String) extends AppResponse

/**
 * 查询返回值封装类
 * 返回：flag:(0操作成功,-1操作失败)；msg:返回消息；total:数据条数；data:数据
 */
final case class AppQueryResult(flag: String, msg: String, total: Int, data: AnyRef)

/**
 * 更新操作返回值封装类
 * 返回：flag:(0操作成功,-1操作失败)；msg:返回消息
 */
final case class UpdateResult(flag: String, msg: String)

/**
 * 登录退出返回值封装类
 */
final case class QueryUserResult(flag: String, isSuperAdmin: Boolean, isAdministrator: Boolean, isUser: Boolean)
