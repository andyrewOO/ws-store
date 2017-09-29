package com.egfbank.tfemp.util

/**
 * mutable.Map 转成 immutable.Map
 * @author andy
 */
class MmaptoImmap[T, U](val map: Map[T, U])(implicit ev: T <:< Any, ev1: U <:< Any) {
  def MmaptoImmap = {
    val m = scala.collection.mutable.Map[T, U]()
    map.foreach {
      mm => m.put(mm._1, mm._2)
    }
    m
  }
}