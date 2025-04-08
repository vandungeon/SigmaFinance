package com.example.sigmafinance.main

import android.util.Log
import com.example.sigmafinance.database.DBType
import com.example.sigmafinance.database.TemporaryLists
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.time.YearMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun getDaysInMonth(year: Int, month: Int): List<LocalDate> {
    val yearMonth = YearMonth.of(year, month)
    val daysInMonth = yearMonth.lengthOfMonth()
    return (1..daysInMonth).map { day ->
        LocalDate.of(year, month, day)
    }
}
fun calculateEndDate(
    startDate: LocalDate,
    repeatInterval: Int?,
    repeatUnit: String?,
    endCondition: String,
    endAfterOccurrences: Int?,
    endDate: LocalDate?
): LocalDate? {

    var newEndDate: LocalDate? = startDate
    when (endCondition) {
        "After N times" -> {
            if (endAfterOccurrences != null) {
                for (i in 2..endAfterOccurrences) {
                    newEndDate = addInterval(newEndDate, repeatInterval ?: 1, repeatUnit)
                }
            }
        }
        "Until date" -> {
            return endDate
        }
        "Never" -> {
            newEndDate = null
        }
    }

    return newEndDate
}

private fun addInterval(date: LocalDate?, interval: Int, unit: String?): LocalDate? {
    Log.d("Add interval", "calculating new interval")
    return when (unit) {
        "Days" -> date?.plusDays(interval.toLong())
        "Weeks" -> date?.plusWeeks(interval.toLong())
        "Months" -> date?.plusMonths(interval.toLong())
        "Years" -> date?.plusYears(interval.toLong())
        else -> date
    }
}
enum class DayOfWeekDisplay(val shortName: String) {
    MONDAY("Mon"),
    TUESDAY("Tue"),
    WEDNESDAY("Wed"),
    THURSDAY("Thu"),
    FRIDAY("Fri"),
    SATURDAY("Sat"),
    SUNDAY("Sun");

    companion object {
        fun from(dayOfWeek: DayOfWeek): DayOfWeekDisplay {
            return when (dayOfWeek) {
                DayOfWeek.MONDAY -> MONDAY
                DayOfWeek.TUESDAY -> TUESDAY
                DayOfWeek.WEDNESDAY -> WEDNESDAY
                DayOfWeek.THURSDAY -> THURSDAY
                DayOfWeek.FRIDAY -> FRIDAY
                DayOfWeek.SATURDAY -> SATURDAY
                DayOfWeek.SUNDAY -> SUNDAY
            }
        }
    }
}


fun getOccurrencesForYearStatic(
    events: List<DBType.FundsEvent>,
    year: Int
): List<TemporaryLists.DemonstrationEvent> {
    val startOfYear = LocalDate.of(year, 1, 1)
    val endOfYear = startOfYear.plusYears(1).minusDays(1)
    Log.d("Calculations", "getOccurrencesForYearStatic called")
    return events.asSequence()
        .filter { event -> event.date in startOfYear..endOfYear }
        .map { event ->
            TemporaryLists.DemonstrationEvent(
                referenceId = event.id,
                name = event.name,
                date = event.date,
                amount = event.amount,
                type = "Static"
            )
        }
        .toList()
}
fun getOccurrencesForYearRecurring(
    events: List<DBType.FundsEventRecurring>,
    year: Int
): List<TemporaryLists.DemonstrationEvent> {
    val startOfYear = LocalDate.of(year, 1, 1)
    val endOfYear = startOfYear.plusYears(1).minusDays(1)
    val filteredRecurringEvents = events.filter { event ->
        (event.startDate.isBefore(endOfYear) || event.startDate == endOfYear) &&
                (event.endDate == null || event.endDate.isAfter(startOfYear) || event.endDate == startOfYear)
    }

    fun alignToStart(date: LocalDate, interval: Long, unit: ChronoUnit, start: LocalDate): LocalDate {
        val periodsBetween = unit.between(date, start) / interval
        return date.plus(periodsBetween * interval, unit).let {
            if (it.isBefore(start)) it.plus(interval, unit) else it
        }
    }
    fun generateDesiredSequence(event: DBType.FundsEventRecurring, unit: ChronoUnit): List<TemporaryLists.DemonstrationEvent>{
        val firstOccurrence =
            if (event.startDate.year != year){
                alignToStart(event.startDate, event.repeatInterval.toLong(), unit, startOfYear)
            }
        else { event.startDate }
        return generateSequence(firstOccurrence) { current ->
            current.plus(event.repeatInterval.toLong(), unit)
        }.takeWhile { current ->
            current <= endOfYear && (event.endDate == null || current <= event.endDate)
        }.map { current ->
            val maxDayOfMonth = current.lengthOfMonth()
            TemporaryLists.DemonstrationEvent(
                referenceId = event.id,
                name = event.name,
                date = LocalDate.of(current.year, current.month,
                        if (event.repeatUnit == "Months") {
                            event.startDate.dayOfMonth.coerceAtMost(maxDayOfMonth)
                        }
                    else { current.dayOfMonth }
                ),
                amount = event.amount,
                type = "Recurring"
            )
        }.toList()
    }
    return filteredRecurringEvents.flatMap { event ->
        val eventDay = event.startDate.dayOfMonth
        when (event.repeatUnit) {
            "Months" -> { generateDesiredSequence(event, ChronoUnit.MONTHS) }
            "Weeks" -> { generateDesiredSequence(event, ChronoUnit.WEEKS) }
            "Days" -> { generateDesiredSequence(event, ChronoUnit.DAYS) }
            "Years" -> {
                if ((year - event.startDate.year) % event.repeatInterval == 0) {
                    val maxDayOfMonth = startOfYear.lengthOfMonth()
                    listOf(
                        TemporaryLists.DemonstrationEvent(
                            referenceId = event.id,
                            name = event.name,
                            date = LocalDate.of(year, event.startDate.month, eventDay.coerceAtMost(maxDayOfMonth)),
                            amount = event.amount,
                            type = "Recurring"
                        )
                    )
                } else { emptyList() }
            }
            else -> emptyList()
        }
    }
}

