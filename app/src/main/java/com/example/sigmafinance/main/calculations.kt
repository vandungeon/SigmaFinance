package com.example.sigmafinance.main

import android.util.Log
import com.example.sigmafinance.database.DBType
import com.example.sigmafinance.database.TemporaryLists
import java.time.YearMonth
import java.time.DayOfWeek
import java.time.LocalDate

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
                for (i in 1..endAfterOccurrences) {
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
        else -> date // No addition if unit is not recognized
    }
}

fun getFirstDayOfMonth(year: Int, month: Int): DayOfWeek {
    val firstDay = LocalDate.of(year, month, 1)
    return firstDay.dayOfWeek // Get day of the week for the first day of the month
}
enum class DayOfWeekDisplay(val shortName: String, val fullName: String) {
    MONDAY("Mon", "Monday"),
    TUESDAY("Tue", "Tuesday"),
    WEDNESDAY("Wed", "Wednesday"),
    THURSDAY("Thu", "Thursday"),
    FRIDAY("Fri", "Friday"),
    SATURDAY("Sat", "Saturday"),
    SUNDAY("Sun", "Sunday");

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

suspend fun getOccurrencesForMonthStatic(
    events: List<DBType.FundsEvent>,
    year: Int,
    month: Int
): MutableList<TemporaryLists.DemonstrationEvent> {
    val occurrences = mutableListOf<TemporaryLists.DemonstrationEvent>()
    val startOfMonth = LocalDate.of(year, month, 1)
    val endOfMonth = startOfMonth.plusMonths(1).minusDays(1)

    for (event in events) {

                if ((event.date.isAfter(startOfMonth) || event.date == startOfMonth) && (event.date.isBefore(endOfMonth) || event.date == endOfMonth)) {
                    val eventOccurrence = TemporaryLists.DemonstrationEvent(
                        referenceId = event.id,
                        name = event.name,
                        date = event.date,
                        amount = event.amount,
                        type = "Static"
                    )
                    occurrences.add(eventOccurrence)

                }
    }

    return occurrences
}

suspend fun getOccurrencesForMonthRecurring(
    events: List<DBType.FundsEventRecurring>,
    year: Int,
    month: Int
): MutableList<TemporaryLists.DemonstrationEvent> {
    val occurrences = mutableListOf<TemporaryLists.DemonstrationEvent>()
    val startOfMonth = LocalDate.of(year, month, 1)
    val endOfMonth = startOfMonth.plusMonths(1).minusDays(1)

    for (event in events) {
        if (event.endDate != null && event.endDate.isBefore(startOfMonth)) continue

        if (event.startDate.isAfter(endOfMonth)) continue

        when (event.repeatUnit) {
            "Months" -> {
                var current = event.startDate
                while (current.isBefore(startOfMonth)) {
                    current = current.plusMonths(event.repeatInterval.toLong())
                }
                if (current.monthValue == month && current.year == year) {
                    val eventOccurrence = TemporaryLists.DemonstrationEvent(
                        referenceId = event.id,
                        name = event.name,
                        date = current,
                        amount = event.amount,
                        type = "Static"
                    )
                    occurrences.add(eventOccurrence)
                }
            }
            "Weeks" -> {
                var current = event.startDate
                while (current.isBefore(startOfMonth)) {
                    current = current.plusWeeks(event.repeatInterval.toLong())
                }
                while (current.isBefore(endOfMonth) || current.isEqual(endOfMonth)) {
                        val eventOccurrence = TemporaryLists.DemonstrationEvent(
                            referenceId = event.id,
                            name = event.name,
                            date = current,
                            amount = event.amount,
                            type = "Recurring"
                        )
                        occurrences.add(eventOccurrence)
                    current = current.plusWeeks(event.repeatInterval.toLong())
                }
            }
            "Days" -> {
                var current = event.startDate
                while (current.isBefore(startOfMonth)) {
                    current = current.plusDays(event.repeatInterval.toLong())
                }
                while (current.isBefore(endOfMonth) || current.isEqual(endOfMonth)) {
                        val eventOccurrence = TemporaryLists.DemonstrationEvent(
                            referenceId = event.id,
                            name = event.name,
                            date = current,
                            amount = event.amount,
                            type = "Recurring"
                        )
                        occurrences.add(eventOccurrence)
                    current = current.plusDays(event.repeatInterval.toLong())
                }
            }
            "Years" -> {
                var current = event.startDate
                while (current.isBefore(startOfMonth)) {
                    current = current.plusYears(event.repeatInterval.toLong())
                }
                while (current.isBefore(endOfMonth) || current.isEqual(endOfMonth)) {
                    val eventOccurrence = TemporaryLists.DemonstrationEvent(
                        referenceId = event.id,
                        name = event.name,
                        date = current,
                        amount = event.amount,
                        type = "Recurring"
                    )
                    occurrences.add(eventOccurrence)
                    current = current.plusYears(event.repeatInterval.toLong())
                }
            }
        }
    }
    return occurrences
}

