package com.andy.actor

import com.andy.mode.TEvent
import akka.actor.actorRef2Scala
import com.andy.util.OracleDBUtil
import com.andy.actor.dao.RepetitionException

trait BizActorWorker extends DefaultWorker {
  // 业务模块通用的数据库连接
  implicit val connect = OracleDBUtil.getConn()

  override def receive = {
    case request: TEvent[_] => {
      try {
        val result = execute(request)
        // 提交整个事务
        connect.commit()
        result.map { rt =>
          request.tsender.map { x =>
            x ! rt
          }
        }
      } catch {
        case ex: RepetitionException => {
          // 报错就回滚事务
          connect.rollback()
          ex.printStackTrace()
          request.tsender.map { x => x ! ex }
        }
        case t: Exception => {
          // 报错就回滚事务
          connect.rollback()
          t.printStackTrace()
          request.tsender.map { x => x ! "服务器处理异常" }
        }
      } finally {
        // 关闭连接
        OracleDBUtil.close(conn = connect)
      }
    }
  }
  def execute(event: TEvent[_]): Option[TEvent[_]]
  def killSelf = context stop self
}

 