/*
  Copyright 2009 Jeremy Cloud

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.egfbank.tfemp.actor.worker.scheduler

import scala.collection.mutable.ListBuffer

/**
 * cron expression:
 *   <seconds> <minutes> <hours> <daysOfMonth> <months> <daysOfWeek> [<year>]
 */
object CronParser {
  private val all = List(-1)

  def parseCronExpression(cronExpression: String):CronJobSchedule = {
    val cronParts = cronExpression.split(" ");

    if (cronParts.length < 6 || cronParts.length > 7) {
      throw new IllegalArgumentException("invalid cron expression: " + cronExpression)
    }

    val seconds = parseCronUnit(cronParts(0), 0, 59)
    val minutes = parseCronUnit(cronParts(1), 0, 59)
    val hours = parseCronUnit(cronParts(2), 0, 23)
    val daysOfMonth = parseCronUnit(cronParts(3), 1, 31)
    val months = parseCronUnit(cronParts(4), 1, 12)
    val daysOfWeek = parseCronUnit(cronParts(5), 1, 7)
    val year = if (cronParts.length == 7) Some(parseInt(cronParts(6), 1970, 2099)) else None
    new CronJobSchedule(seconds, minutes, hours, daysOfMonth, months, daysOfWeek, year)
  }

  private def parseCronUnit(unit: String, min: Int, max: Int): List[Int] = {
    if (unit.equals("*") || unit.equals("?")) {
      all
    }
    else {
      val slash = unit.indexOf('/')
      val values = new ListBuffer[Int]

      if (slash != -1) {
        var offset = parseInt(unit.substring(0, slash), min, max)
        val interval = parseInt(unit.substring(slash + 1), 1, max)

        while (offset <= max) {
           values += offset
           offset += interval
        }
      }
      else {
        val ranges = unit.split(",")

        for (range <- unit.split(",")) {
          val dash = range.indexOf('-')

          if (dash == -1) {
            values += parseInt(range, min, max)
          }
          else {
            val start = parseInt(range.substring(0, dash), min, max)
            val end = parseInt(range.substring(dash + 1), min, max)
            start.to(end).foreach(values += _)
          }
        }
      }

      values.toList
    }
  }


  private def parseInt(str: String, min: Int, max: Int): Int = {
    try {
      val num = str.toInt

      if (num < min || num > max) {
        throw new NumberFormatException
      }
      else {
        num
      }
    }
    catch {
      case e: NumberFormatException =>
        throw new IllegalArgumentException("Invalid value in cron expresion: '" + str + "', expecting number in the range " + min + "-" + max)
    }
  }

  def main(args: Array[String]) {
    val cron = parseCronExpression("00 00  * * *")
    Console.println(cron)
  }
}
