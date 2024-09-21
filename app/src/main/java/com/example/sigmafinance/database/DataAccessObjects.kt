package com.example.sigmafinance.database

import androidx.lifecycle.LiveData
import androidx.room.Query
import androidx.room.Upsert

interface DataAccessObjects {

    @Upsert
    suspend fun insertFundEvent(event: DBType.FundsEvent)

    @Upsert
    suspend fun insertRecurringEvent(event: DBType.FundsEventRecurring)

    @Upsert
    suspend fun insertDay(day: DBType.DayWithEvents)

    @Query(" Select * From FundsEvents")
    fun readFundsEvents (): LiveData<List<DBType.FundsEvent>>

    @Query(" Select * From FundsRecurringEvents")
    fun readFundsRecurringEvents (): LiveData<List<DBType.FundsEventRecurring>>

    @Query(" Select * From DaysWithEvents")
    fun readDaysWithEvents (): LiveData<List<DBType.DayWithEvents>>

    @Query("DELETE FROM FundsEvents")
    fun cleanFundsEvents()

    @Query("DELETE FROM FundsRecurringEvents")
    fun cleanFundsRecurringEvents()

    @Query("DELETE FROM DaysWithEvents")
    fun clearDaysWithEvents()

}