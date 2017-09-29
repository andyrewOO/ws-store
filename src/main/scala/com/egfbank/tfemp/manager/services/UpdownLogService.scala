package com.egfbank.tfemp.manager.services

import com.egfbank.tfemp.manager.dao.UpdownLogDao

/**
 * @author Administrator
 */
object UpdownLogService {

  /**
   * @param appName
   * @param searchTimeFrom
   * @param searchTimeTo
   */
  def getListCheckLogByCond(condMap: Map[String, String]) = {
    UpdownLogDao.queryByCond(condMap)
  }
}

class UpdownLogService {}