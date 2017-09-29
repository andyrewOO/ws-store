package com.egfbank.tfemp.constant

/**
 * @author andy
 */
object CFCAResult {

  /**
   * 反馈成功
   */
  val SUCCESSFUL = "0000"

  /**
   * 身份认证失败-证件信息（种类、号码等）不符、证件、帐号不符
   */
  val AUTHEN_FAIL_CERTINFO = "0100"

  /**
   * 身份认证失败-其他
   */
  val AUTHEN_FAIL_OTHER = "0199"

  /**
   * 无效金额-金额因填写错误造成为负或格式错误
   */
  val INVALID_AMOUNT_FORMAT = "0200"

  /**
   * 无效金额-其他
   */
  val INVALID_AMOUNT_OTHER = "0299"

  /**
   * 无效帐卡号-帐号填写有误，卡号不存在
   */
  val INVALID_CARD_INEXIST = "0300"
  /**
   * 无效帐卡号-帐号和名称不符
   */
  val INVALID_CARD_UNCONFORM = "0301"
  /**
   * 无效帐卡号-帐号白名单
   */
  val INVALID_CARD_WHITE = "0302"
  /**
   * 无效帐卡号-帐号挂失
   */
  val INVALID_CARD_LOSS = "0303"
  /**
   * 无效帐卡号-帐号
   */
  val INVALID_CARD_ACCOUNT = "0304"
  /**
   * 无效帐卡号-其他
   */
  val INVALID_CARD_OTHER = "0399"
  /**
   * 找不到原始交易-查找不到原始交易，匹配原始请求交易出错
   */
  val NOFOUND_ORITRAN_NORECORD = "0400"
  /**
   * 找不到原始交易-其他
   */
  val NOFOUND_ORITRAN_OTHER = "0499"

  /**
   * 报文格式错误-账户名、身份证号、金额等必输内容缺失
   */
  val FORMART_ERROR_REQUISITE = "0500"
  /**
   *  报文格式错误-报文乱码不可解析
   */
  val FORMART_ERROR_CONFUSED = "0501"
  /**
   * 报文格式错误-其他
   */
  val FORMART_ERROR_OTHER = "0599"
  /**
   * 相应法律文书缺失-查询、止付、冻结报文缺失相应法律文书文件
   */
  val FILE_LOSS = "0600"
  /**
   * 相应法律文书缺失-其他
   */
  val FILE_OTHER = "0699"
  /**
   * 机构代码错误 -机构编号异常，无法解析
   */
  val ORGCODE_ERROR = "0700"
  /**
   * 机构代码错误-子机构编码异常，无法解析
   */
  val ORGCODE_EXCEPTION = "0701"
  /**
   * 机构代码错误 -其他
   */
  val ORGCODE_OTHER = "0799"
  /**
   * 处理日期不合法 -日期格式错误
   */
  val DATE_ILLEGAL_FORMART = "0800"
  /**
   * 处理日期不合法 -起始与截止日期超出指令法律效力范围
   */
  val DATE_ILLEGAL_SCOPE = "0801"
  /**
   * 处理日期不合法 -指令申请日期异常
   */
  val DATE_ILLEGAL_EXCEPTION = "0802"
  /**
   * 处理日期不合法 -其他日期错误
   */
  val DATE_ILLEGAL_OTHER = "0899"
  /**
   * 已处理过的业务-已经处理该项业务
   */
  val YET_HANDLE_TRANS = "0900"
  /**
   * 已处理过的业务-其他
   */
  val YET_HANDLE_OTHER = "0999"
  /**
   * 请求的功能尚不支持-发卡机构尚未开通此功能
   *
   */
  val UNSUPPORT_NOFUNCTION = "1000"
  /**
   * 请求的功能尚不支持 -其他
   */
  val UNSUPPORT_OTHER = "1099"
  /**
   * 接收机构或中间网络设施找不到或无法达到-行内处理异常
   */
  val NET_ERROR_INSIDE = "1100"
  /**
   *  接收机构或中间网络设施找不到或无法达到-平台通信异常
   */
  val NET_ERROR_COMMUNICATION = "1101"
  /**
   *  接收机构或中间网络设施找不到或无法达到-其他
   */
  val NET_ERROR_OTHER = "1199"
  /**
   * 止付有缺陷的成功 -账户已限额止付，但账户内金额不足
   */
  val STOP_DEFECT_SUCCESS_AMOUNT = "1200"
  /**
   * 止付有缺陷的成功 -其他
   */
  val STOP_DEFECT_SUCCESS_OTHER = "1200"
  /**
   * 止付失败 -行内止付失败
   */
  val STOP_FAIL_BANK = "1300"
  /**
   * 止付失败  -账户已止付
   */
  val STOP_FAIL_KEEPSTOP = "1301"
  /**
   * 止付失败-账户已限额冻结
   */
  val STOP_FAIL_LIMIT_FREEZE = "1302"
  /**
   * 止付失败-账户已全部冻结
   */
  val STOP_FAIL_ALL_FREEZE = "1303"
  /**
   * 止付失败-超出规定止付次数
   */
  val STOP_FALI_TIMESOUT = "1304"
  /**
   * 止付失败-其他
   */
  val STOP_FAIL_OTHER = "1399"
  
  
  
  /**
   * 止付延期失败-止付已到期，延期操作失效
   */
  val STOPDELAY_FAIL_EPIRE = "1800"
  /**
   * 止付延期失败-其他
   */
  val STOPDELAY_FAIL_OTHER = "1899"
  /**
   * 止付解除失败-止付已到期，解除操作失效
   */
  val STOPFREE_FAIL_EXPIRE = "1900"
  /**
   * 止付解除失败-其他
   */
  val STOPFREE_OTHER = "1999"
  
}