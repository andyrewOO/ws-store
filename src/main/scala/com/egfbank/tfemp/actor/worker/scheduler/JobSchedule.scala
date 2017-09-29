package com.egfbank.tfemp.actor.worker.scheduler

import java.util.Calendar
import java.util.Date
import xitrum.Log

/**
 * @author XuHaibin
 */
abstract class JobSchedule {
  def firstRun(currentTime: Long): Long
  def nextRun(currentTime: Long): Option[Long]
}
object JobSchedule {
  /**
   * Creates a job schedule that runs only once after the specified delay.
   */
  def once(delay: Long): JobSchedule =
    OnceSchedule(System.currentTimeMillis() + delay)

  /**
   * Creates a job schedule that runs only once at the specified
   * date/time.
   */
  def once(when: Date): JobSchedule = OnceSchedule(when.getTime)

  /**
   * Creates a job schedule that runs repeatedly at the specified interval.
   * The first time the job runs will be now + interval.
   */
  def repeat(interval: Long): JobSchedule =
    IntervalJobSchedule(interval, interval, true)

  /**
   * Creates a schedule that first runs after the specified delay, and then
   * repeats at the specified interval.  Use of delay of zero to have the job
   * run immediately after being added to a Scheduler.
   */
  def repeat(delay: Long, interval: Long): JobSchedule =
    IntervalJobSchedule(delay, interval, true)

  /**
   * cron expression:
   *   <seconds> <minutes> <hours> <daysOfMonth> <months> <daysOfWeek> [<year>]
   */
  def cron(cronExpression: String) = CronParser.parseCronExpression(cronExpression)
}

case class OnceSchedule(when: Long) extends JobSchedule {
  def firstRun(currentTime: Long) = when - currentTime
  def nextRun(currentTime: Long) = None
}

case class IntervalJobSchedule(delay: Long, interval: Long, repeating: Boolean) extends JobSchedule {
  val createTime = System.currentTimeMillis()
  val whenDelay = createTime + delay
  def firstRun(currentTime: Long) = whenDelay - createTime
  def nextRun(currentTime: Long) = if (repeating) Some(currentTime + interval) else None
}

case class CronJobSchedule(
  seconds: List[Int],
  minutes: List[Int],
  hours: List[Int],
  daysOfMonth: List[Int],
  months: List[Int],
  daysOfWeek: List[Int],
  year: Option[Int]) extends JobSchedule
{
  import CronJobSchedule._

  def firstRun(currentTime: Long) = nextRun(currentTime).get

  /**
   * Computes the next time for this schedule after 'current'.
   */
  def nextRun(currentTime: Long): Option[Long] = {
    val now = Calendar.getInstance()
    now.setTimeInMillis(currentTime)
    now.set(Calendar.MILLISECOND, 0)

    var alarm = now.clone.asInstanceOf[Calendar]


    //
    // the updates work in a cascade -- if next minute value is in the
    // following hour, hour is incremented.  If next valid hour value is
    // in the following day, day is incremented, and so on.
    //

    // increase alarm seconds
    var current = alarm.get(Calendar.SECOND)
    var offset = 0
    // force increment at least to next second
    offset = getOffsetToNext(current, minSecond, maxSecond, seconds)
    alarm.add(Calendar.SECOND, offset)

    // increase alarm minutes
    current = alarm.get(Calendar.MINUTE)
    offset = getOffsetToNextOrEqual(current, minMinute, maxMinute, minutes)
    alarm.add(Calendar.MINUTE, offset)

    // update alarm hours if necessary
    current = alarm.get(Calendar.HOUR_OF_DAY)  // (as updated by minute shift)
    offset = getOffsetToNextOrEqual(current, minHour, maxHour, hours)
    alarm.add(Calendar.HOUR_OF_DAY, offset)

    //
    // If days of month AND days of week are restricted, we take whichever match
    // comes sooner.
    // If only one is restricted, take the first match for that one.
    // If neither is restricted, don't do anything.
    //
    if (daysOfMonth(0) != -1 && daysOfWeek(0) != -1) {
      // BOTH are restricted - take earlier match
      val dayOfWeekAlarm = alarm.clone.asInstanceOf[Calendar]
      updateDayOfWeekAndMonth(dayOfWeekAlarm)

      val dayOfMonthAlarm = alarm.clone.asInstanceOf[Calendar]
      updateDayOfMonthAndMonth(dayOfMonthAlarm)

      // take the earlier one
      if (dayOfMonthAlarm.getTime().getTime() < dayOfWeekAlarm.getTime().getTime()) {
        alarm = dayOfMonthAlarm
      }
      else {
        alarm = dayOfWeekAlarm
      }
    }
    else if (daysOfWeek(0) != -1) { // only dayOfWeek is restricted
      // update dayInWeek and month if necessary
      updateDayOfWeekAndMonth(alarm)
    }
    else if (daysOfMonth(0) != -1) { // only dayOfMonth is restricted
      // update dayInMonth and month if necessary
      updateDayOfMonthAndMonth(alarm)
    }
    // else if neither is restricted (both(0) == -1), we don't need to do anything.

    Some(alarm.getTimeInMillis - currentTime)
  }

  /**
   * daysInMonth can't use simple offsets like the other fields, because the
   * number of days varies per month (think of an alarm that executes on every
   * 31st).  Instead we advance month and dayInMonth together until we're on a
   * matching value pair.
   */
  private def updateDayOfMonthAndMonth(alarm: Calendar) {
    var currentMonth = alarm.get(Calendar.MONTH)
    var currentDayOfMonth = alarm.get(Calendar.DAY_OF_MONTH)
    var offset = 0

    // loop until we have a valid day AND month (if current is invalid)
    while (!months.contains(currentMonth) || !daysOfMonth.contains(currentDayOfMonth)) {
      // if current month is invalid, advance to 1st day of next valid month
      if (!months.contains(currentMonth)) {
        offset = getOffsetToNextOrEqual(currentMonth, minMonth, maxMonth, months)
        alarm.add(Calendar.MONTH, offset)
        alarm.set(Calendar.DAY_OF_MONTH, 1)
        currentDayOfMonth = 1
      }

      // advance to the next valid day of month, if necessary
      if (!daysOfMonth.contains(currentDayOfMonth)) {
        val maxDayOfMonth = alarm.getActualMaximum(Calendar.DAY_OF_MONTH)
        offset = getOffsetToNextOrEqual(currentDayOfMonth, minDayOfMonth, maxDayOfMonth, daysOfMonth)
        alarm.add(Calendar.DAY_OF_MONTH, offset)
      }

      currentMonth = alarm.get(Calendar.MONTH)
      currentDayOfMonth = alarm.get(Calendar.DAY_OF_MONTH)
    }
  }


  private def updateDayOfWeekAndMonth(alarm: Calendar) {
    var currentMonth = alarm.get(Calendar.MONTH)
    var currentDayOfWeek = alarm.get(Calendar.DAY_OF_WEEK)
    var offset = 0

    // loop until we have a valid day AND month (if current is invalid)
    while (!months.contains(currentMonth) || !daysOfWeek.contains(currentDayOfWeek)) {
      // if current month is invalid, advance to 1st day of next valid month
      if (!months.contains(currentMonth)) {
        offset = getOffsetToNextOrEqual(currentMonth, minMonth, maxMonth, months)
        alarm.add(Calendar.MONTH, offset)
        alarm.set(Calendar.DAY_OF_MONTH, 1)
        currentDayOfWeek = alarm.get(Calendar.DAY_OF_WEEK)
      }

      // advance to the next valid day of week, if necessary
      if (!daysOfWeek.contains(currentDayOfWeek)) {
        offset = getOffsetToNextOrEqual(currentDayOfWeek, minDayOfWeek, maxDayOfWeek, daysOfWeek)
        alarm.add(Calendar.DAY_OF_YEAR, offset)
      }

      currentDayOfWeek = alarm.get(Calendar.DAY_OF_WEEK)
      currentMonth = alarm.get(Calendar.MONTH)
    }
  }



  // ----------------------------------------------------------------------
  //            General utility methods
  // ----------------------------------------------------------------------

  /**
   * if values = {-1}
   *   offset is 1 (because next value definitely matches)
   * if current < last(values)
   *   offset is diff to next valid value
   * if current >= last(values)
   *   offset is diff to values(0), wrapping from max to min
   */
  private def getOffsetToNext(current: Int, min: Int, max: Int, values: List[Int]): Int = {
    // find the distance to the closest valid value > current (wrapping if neccessary)

    // {-1} means *  -- offset is 1 because current++ is valid value
    if (values(0) == -1) {
      1
    }
    else {
      // need to wrap
      if (current >= values.last) {
        val next = values(0)
        (max - current + 1) + (next - min)
      }
      else { // current < max(values) -- find next valid value after current
        values.find(_ > current).get - current
      } // end current < max(values)
    }
  }

  /**
   * if values = {-1} or current is valid
   *   offset is 0.
   * if current < last(values)
   *   offset is diff to next valid value
   * if current >= last(values)
   *   offset is diff to values(0), wrapping from max to min
   */
  private def getOffsetToNextOrEqual(current: Int, min: Int, max: Int, values: List[Int]): Int = {
    // find the distance to the closest valid value >= current (wrapping if necessary)

    // {-1} means *  -- offset is 0 if current is valid value
    if (values(0) == -1 || values.contains(current)) {
      0
    }
    else {
      val safeValues = values.filter(_ <= max)

      // need to wrap
      if (current > safeValues.last) {
        val next = safeValues(0)
        (max-current+1) + (next-min)
      }
      else { // current <= max(values) -- find next valid value
        safeValues.find(_ > current).get - current
      } // end current <= max(values)
    }
  }
}


private object CronJobSchedule {
  private val minSecond = 0
  private val maxSecond = 59
  private val minMinute = 0
  private val maxMinute = 59
  private val minHour = 0
  private val maxHour = 23
  private val minDayOfMonth = 1
  // maxDayOfMonth varies by month
  private val minMonth = 0
  private val maxMonth = 11
  private val minDayOfWeek = 1
  private val maxDayOfWeek = 7
}
