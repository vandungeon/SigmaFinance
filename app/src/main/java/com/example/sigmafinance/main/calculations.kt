package com.example.sigmafinance.main

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
