package com.example.sigmafinance.main

import android.util.Log
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
