package com.egfbank.tfemp.manager.services

import com.egfbank.tfemp.manager.model.ListCheckLog
import com.egfbank.tfemp.manager.dao.ListCheckLogDao

/**
 * @author Administrator
 */
object ListCheckLogService {

  /**
   * @param appName
   * @param searchTimeFrom
   * @param searchTimeTo
   */
  def getListCheckLogByCond(condMap: Map[String, String]) = {
    ListCheckLogDao.queryByCond(condMap)
  }
}

class ListCheckLogService {}