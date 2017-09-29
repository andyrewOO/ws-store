package com.egfbank.tfemp.manager.services

import com.egfbank.tfemp.manager.dao.AllLogDao
import com.egfbank.tfemp.manager.dao.DoubtAccountDao

/**
 * @author Administrator
 */
object DoubtAccountService {

  /**
   * @param appName
   * @param searchTimeFrom
   * @param searchTimeTo
   */
  def getListCheckLogByCond(condMap: Map[String, String]) = {
    DoubtAccountDao.queryByCond(condMap)
  }
}

class DoubtAccountService {}