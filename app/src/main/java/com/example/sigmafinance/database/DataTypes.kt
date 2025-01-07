package com.example.sigmafinance.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.Date

class DBType {
    @Entity(tableName = "FundsEvents")
    data class FundsEvent(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val name: String,
        val date: LocalDate,
        val amount: Float
    )
    @Entity(tableName = "FundsRecurringEvents")
    data class FundsEventRecurring(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val name: String,
        val startDate: LocalDate,
        val amount: Float,
        val repeatInterval: Int,       // E.g., every 1, 2, 3...
        val repeatUnit: String,        // E.g., "Days", "Weeks", "Months", "Years"
        val endCondition: String,      // "Never", "After N times", "Until date"
        val endAfterOccurrences: Int?, // Nullable for "After N times" condition
        val endDate: LocalDate?        // Nullable for "Until date" condition
    )
    /*    @Entity(tableName = "DaysWithEvents")
    data class DayWithEvents(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val date: LocalDate,
        val listOfEvents: List<FundsEvent>
    )*/
    data class SingleMonthEvents(
        val day: Int,
        val name: String,
        val amount: Float,
        val id: Int,
        val source: String
    )
}
class TemporaryLists {
    data class DemonstrationEvent(
        val referenceId: Int,
        val name: String,
        val date: LocalDate,
        val amount: Float,
        val type: String
    )
}