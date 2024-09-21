package com.example.sigmafinance.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.Date

class DBType {
    data class FundsEvent(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val name: String,
        val amount: Int
    )
    @Entity(tableName = "FundsRecurringEvents")
    data class FundsEventRecurring(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val name: String,
        val startDate: LocalDate,
        val amount: Int,
        val recurringType: String
    )
    @Entity(tableName = "DaysWithEvents")
    data class DayWithEvents(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val date: LocalDate,
        val listOfEvents: List<FundsEvent>
    )
    data class SingleMonthEvents(
        val day: Int,
        val name: String,
        val amount: Int,
        val id: Int,
        val source: String
    )
}