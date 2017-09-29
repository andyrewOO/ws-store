package com.egfbank.tfemp.manager.services

import com.egfbank.tfemp.manager.dao.AllLogDao

/**
 * @author Administrator
 */
object AllLogService {

  /**
   * @param appName
   * @param searchTimeFrom
   * @param searchTimeTo
   */
  def getListCheckLogByCond(condMap: Map[String, String]) = {
    AllLogDao.queryByCond(condMap)
  }
}

class AllLogService {}