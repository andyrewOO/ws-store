package com.egfbank.tfemp.actor.services

import xitrum.Log
import java.text.SimpleDateFormat
import com.egfbank.tfemp.util.HfbkUtil

/**
 * @author huxp
 */
trait TDHDbService extends TDHService with Log {

  /**
   * 查询历史交易明细表
   */
  def queryTransDetail(acct_no: String, sub_acct_no: String, begin: String, end: String) = {
    log.info("Start queryTransDetail")
    val sql = """select trad_type TranType,debit_credit_site DbtCrdtFlg,ccy CcyType,trad_amt TranAmt,cunt_no_bal TranBal,trad_time TranTime,trad_seri_no TranSeqNo,tran_advs_acct_name OpponentName,trad_advs_main_acct trgtacctno,cert_no OpponentCredentialNumber,trad_advs_org_no trgtopnacctbrchid,summy_code Remark,trad_org_name TranAdr,trad_org AcptBrchId,log_no LogNumber, detal_trad_seri_no SummonsNumber, vouch_no VoucherCode, vouch_type  VoucherType,cash_sign CshRmtFlg,tmn_no TerminalNumber,trad_stat TransactionStatus from acc_trans_info where id > ? and id < ?"""

    var df: SimpleDateFormat = new SimpleDateFormat("yyyyMMdd")
    val format: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")

    val beginDate = format.format(df.parse(begin))
    val endDate = format.format(df.parse(end))

    val beginId = acct_no + "_" + sub_acct_no + "_" + beginDate
    val endId = acct_no + "_" + sub_acct_no + "_" + endDate
    log.info(s"Query parameter,beginId:${beginId},endId:${endId}")
    queryTDH(sql, Seq(
      beginId,
      endId))
  }

  /**
   * 向TDH中的交易失败表插入数据（tfemp_verify_failed）
   */
  def insertVerifyFailed(dataMap: Map[String, String]) = {
    log.info("Start insertVerifyFailed")
    val sql: String = "insert into TFEMP_VERIFY_FAILED(id, trans_serial_number, channel, outidtype, outidcode, inidtype, inidcode, trans_time, verify_failed_status, status) values (?,?,?,?,?,?,?,?,?,?)"
    executeUpdateTDH(sql, Seq(
      dataMap.getOrElse("id", ""),
      dataMap.getOrElse("serial", ""),
      dataMap.getOrElse("channel", ""),
      dataMap.getOrElse("datatype1", ""),
      dataMap.getOrElse("data1", ""),
      dataMap.getOrElse("datatype2", ""),
      dataMap.getOrElse("data2", ""),
      dataMap.getOrElse("time", ""),
      dataMap.getOrElse("verifyStatus", ""),
      dataMap.getOrElse("status", "")))
  }

  /**
   * 查询t100403
   */
  def queryT100403() = {
    log.info("Start queryT100403")
    val sql = """select id, feature_code, id_type, id_number, id_name, card_number, account_open_time, account_open_place from tfemp_t100403 where status=?"""

    queryTDH(sql, Seq(TDHDbService.Task_CRE))
  }

  def updateT100403(id: String) = {
    log.info("Start updateT100403")
    val sql = """update tfemp_t100403 set status=? where id=?"""

    executeUpdateTDH(sql, Seq("1", id))
  }

  /**
   * @return
   */
  def queryT100404() = {
    log.info("Start queryT100404")
    val sql = """select id, feature_code, card_number, account_name, id_type, id_number, account_number, account_serial, account_type, account_status, currency, transaction_type, borrowing_signs, currency_trans, transaction_amount, account_balance, transaction_time, transaction_serial, opponent_account_number, opponent_deposit_bank_id, transaction_branch_name, transaction_branch_code, cash_mark, transaction_status from tfemp_t100404 where status=?"""

    queryTDH(sql, Seq(TDHDbService.Task_CRE))
  }

  def updateT100404(id: String) = {
    log.info("Start updateT100404")
    val sql = """update tfemp_t100404 set status=? where id=?"""

    executeUpdateTDH(sql, Seq("1", id))
  }

  /**
   * @return
   */
  def queryT100405() = {
    log.info("Start queryT100405")
    val sql = """select id, account_name, card_number, account_number, account_serial, account_type, account_status, currency, feature_code, transaction_type, borrowing_signs, currency_trans, transaction_amount, account_balance, transaction_time, transaction_serial, opponent_account_number, opponent_deposit_bank_id, transaction_branch_name, transaction_branch_code, cash_mark, transaction_status from tfemp_t100405 where status=?"""

    queryTDH(sql, Seq(TDHDbService.Task_CRE))
  }

  def updateT100405(id: String) = {
    log.info("Start updateT100405")
    val sql = """update tfemp_t100405 set status=? where id=?"""

    executeUpdateTDH(sql, Seq("1", id))
  }

}
object TDHDbService {
  /**
   * 指令创建状态 task_create
   */
  val Task_CRE = "0"
  /**
   * 报文校验成功task_execute
   */
  val Task_EXE = "1"
  /**
   * 报文校验失败task_finish
   */
  val Task_FIN = "2"
}