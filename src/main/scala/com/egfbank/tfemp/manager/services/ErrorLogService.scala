package com.egfbank.tfemp.manager.services

import com.egfbank.tfemp.manager.dao.ErrorLogDao

/**
 * @author Administrator
 */
object ErrorLogService {

  /**
   * @param appName
   * @param searchTimeFrom
   * @param searchTimeTo
   */
  def getListCheckLogByCond(condMap: Map[String, String]) = {
    ErrorLogDao.queryByCond(condMap)
  }
}

class ErrorLogService {}