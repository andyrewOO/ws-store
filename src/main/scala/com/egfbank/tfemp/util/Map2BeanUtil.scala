package com.egfbank.tfemp.util

import java.util.ArrayList

import scala.collection.mutable.ListBuffer

import org.apache.commons.beanutils.BeanUtils

import com.egfbank.tfemp.constant.CFCATradeCode
import com.egfbank.tfemp.constant.CFCATradeRegex

import cfca.safeguard.api.bank.bean.tx.upstream.Tx100102
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100104
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100106
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100202
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100204
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100206
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100304
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100306
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100308
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100401
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100403
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100404
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100405
import cfca.safeguard.tx.Attachment
import cfca.safeguard.tx.Transaction
import cfca.safeguard.tx.business.bank.TxCaseReport_Transaction
import cfca.safeguard.tx.business.bank.TxInvolvedAccount_Account
import cfca.safeguard.tx.business.bank.TxUnusualOpencard_Accounts
import xitrum.Log

/**
 * 通过交易类型和数据，转换成交易所对应的Bean对象
 * @author andy
 */
object Map2BeanUtil extends Log {
  def getBeanByTXCode(tradeType: String, beanMap: Map[String, Any]) = {
    var bean = new AnyRef
    tradeType match {
      case CFCATradeRegex.freeOrpaymentRegex() =>
        // 冻结/止付
        bean = getBean4SFQ(tradeType, beanMap)
      case CFCATradeCode.queryAccountMainO =>
        // 查询持卡主体
        bean = getBean4SFQ(tradeType, beanMap)
      case CFCATradeCode.queryAccountDynamicLift =>
        // 动态查询解除
        bean = getBean4SFQ(tradeType, beanMap)
      case CFCATradeCode.queryAccountDynamic => {
        // 动态查询
        val transaction = new Transaction
        val transactionList = ToolUtils.getBeanList(transaction, "transactions", beanMap)
        val tx100306 = new Tx100306
        BeanUtils.populate(tx100306, ToolUtils.scalaMap2JavaHashMap(beanMap.filter(!_._1.equals("transactions"))))
        tx100306.setTransactions(transactionList)
        bean = tx100306
      }
      
      case CFCATradeCode.caseReport => {
        // 案件举报
        val txCaseReport_Transaction = new TxCaseReport_Transaction
        val txCaseReport_TransactionList = ToolUtils.getBeanList(txCaseReport_Transaction, "transactionList", beanMap)
        val attachment = new Attachment
        val attachmentList = ToolUtils.getBeanList(attachment, "attachments", beanMap)
        val tx100401 = new Tx100401
        BeanUtils.populate(tx100401, ToolUtils.scalaMap2JavaHashMap(beanMap.filter(kv => kv._2.isInstanceOf[String])))
        tx100401.setTransactionList(txCaseReport_TransactionList)
        tx100401.setAttachmentList(attachmentList)
        bean = tx100401
      }
      //TODO 暂不完善
      case CFCATradeCode.abnormalOpenCardR => {
        // 可疑名单上报-异常开卡
        val accounts = new TxUnusualOpencard_Accounts
        val accountsList = ToolUtils.getBeanList(accounts, "accountsList", beanMap)
        val tx100403 = new Tx100403
        BeanUtils.populate(tx100403, ToolUtils.scalaMap2JavaHashMap(beanMap.filter(!_._1.equals("accountsList"))))
        tx100403.setAccountsList(accountsList)
        bean = tx100403
      }
      //TODO 暂不完善
      case CFCATradeCode.involvedAccount => {
        // 可疑名单上报-涉案账户
        val accountListData = beanMap("accountList").asInstanceOf[ListBuffer[Map[String, Any]]]
        val accountList = new ListBuffer[Map[String, Any]]
        if (accountListData.isInstanceOf[ListBuffer[_]])
          accountList.appendAll(accountListData.toList)

        // 封装 accountList
        val accountBeanList = new ArrayList[TxInvolvedAccount_Account]
        for (accountMap <- accountList.toList) {
          val transaction = new Transaction()
          val transactionList = ToolUtils.getBeanList(transaction, "transactionList", accountMap)
          val accountBean = new TxInvolvedAccount_Account
          BeanUtils.populate(accountBean, ToolUtils.scalaMap2JavaHashMap(accountMap.filter(!_._1.equals("transactionList"))))
          accountBean.setTransactionList(transactionList)
          accountBeanList.add(accountBean)
        }

        val tx100404 = new Tx100404()
        BeanUtils.populate(tx100404, ToolUtils.scalaMap2JavaHashMap(beanMap.filter(!_._1.equals("accountList"))))
        tx100404.setAccountList(accountBeanList)
        bean = tx100404
      }
    }
    bean
  }

  /**
   * 构造支付/冻结反馈,查询持卡主体反馈,账户动态查询解除反馈, 交易的Bean对象
   */
  private[this] def getBean4SFQ(tradeType: String, beanMap: Map[String, Any]) = {
    log.info(this.getClass + ", getBean4SFQ:" + tradeType + ", beanMap:" + beanMap)
    var tx = new AnyRef
    tradeType match {
      case CFCATradeCode.stopPayment             => tx = new Tx100102
      case CFCATradeCode.stopPaymentLift         => tx = new Tx100104
      case CFCATradeCode.stopPaymentDelay        => tx = new Tx100106
      case CFCATradeCode.freezePay               => tx = new Tx100202
      case CFCATradeCode.freezePayFree           => tx = new Tx100204
      case CFCATradeCode.freezePayDelay          => tx = new Tx100206
      case CFCATradeCode.queryAccountMainO       => tx = new Tx100304
      case CFCATradeCode.queryAccountDynamicLift => tx = new Tx100308
      case _                                     =>
    }
    val dataMap = beanMap.asInstanceOf[Map[String, String]]
    BeanUtils.populate(tx, ToolUtils.scalaMap2JavaHashMap(dataMap))
    tx
  }
}