package com.andy.mode

/**
 * 返回结果封装
 */
/**
 * @author win7
 *
 */
/**
 * @author win7
 *
 */
/**
 * @author win7
 *
 */
/**
 * @author win7
 *
 */
/**
 * @author win7
 *
 */
/**
 * @author win7
 *
 */
/**
 * @author win7
 *
 */
sealed trait AppResponse

/**
 * 登录校验结果封装
 * @return flag 1：登录成功，0：登录失败
 * @return msg 返回消息
 */
case class LoginCheckResponse(flag: String, msg: String) extends AppResponse
/**
 * 登录结果封装
 */
case class LoginResultResponse(flag: String, msg: String, userId: String, userName: String, userRole: String) extends AppResponse
/**
 * 异常结果封装
 */
case class ExceptionReesponse(status: String, message: String, errorCode: String) extends AppResponse

/**
 * 查询结果返回值封装类
 * 查询返回值封装类
 * 返回：flag:(0操作成功,-1操作失败)；msg:返回消息；total:数据条数；data:数据
 */
case class AppQueryResult(flag: String, msg: String, total: Int, data: AnyRef)

/**
 * 更新操作返回值封装类
 * @return isSucceeded(TRUE操作成功,FALSE操作失败);
 * @return msg 返回消息;
 */
case class UpdateResult(isSucceeded: Boolean = true, msg: String = "success")

case class SQLResponse(count: Int, msg: String)

case class Page(start: Int = -1, size: Int = -1, sum: Int = 0, result: List[Any] = null)

/**
 * @param <T>: 查询对应的实体
 * @param page:查询对应的翻页对象
 */
case class AppQueryParam[T](queryBean: T, page: Page)