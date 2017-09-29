package com.egfbank.tfemp.actor

/**
 * @author huxp
 */
sealed trait BizScene

// ESB 的业务场景如下:

/**
 * 黑名单查验-单要素查验
 */
case object VerifyDoubtSingle extends BizScene

/**
 * 黑名单查验-双要素查验
 */
case object VerifyDoubtMulti extends BizScene

/**
 * 案件举报
 */
case object ReportCase extends BizScene

/**
 * 案件举报
 */
case object ReportCaseResult extends BizScene

/**
 * 可疑名单上报(异常开卡)
 */
case object ReportDoubtCard extends BizScene

/**
 * 可疑名单上报(涉案帐户)
 */
case object ReportDoubtAcc extends BizScene

/**
 * 可疑名单上报(异常事件)
 */
case object ReportException extends BizScene
/**
 * 可疑名单上报(异常事件)
 */
case object ReportExceptionAction extends BizScene

//CFCA下发指令 如下
/**
 * 止付
 */
case object StopPay extends BizScene

/**
 * 止付延期
 */
case object StopPayDelay extends BizScene

/**
 * 止付解除
 */
case object StopPayFree extends BizScene

/**
 * 冻结
 */
case object FreezePay extends BizScene

/**
 * 冻结延期
 */
case object FreezePayDelay extends BizScene

/**
 * 冻结解除
 */
case object FreezePayFree extends BizScene

/**
 * 帐户交易明细查询
 */
case object QueryTransDetail extends BizScene

/**
 * 帐户持卡主体查询
 */
case object QueryCardOwner extends BizScene

/**
 * 帐户动态查询
 */
case object QueryAccMonitor extends BizScene

/**
 * 帐户动态查询解除
 */
case object QueryAccMonitorFree extends BizScene

/**
 * 全帐户查询
 */
case object QueryAccAllInfo extends BizScene

/**
 * 前置机接受反馈失败
 */
case object CFCAResponseError extends BizScene

