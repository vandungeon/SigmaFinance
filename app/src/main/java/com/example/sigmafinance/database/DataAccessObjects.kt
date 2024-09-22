package com.example.sigmafinance.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
@Dao
interface DataAccessObjects {


    @Upsert
    suspend fun insertRecurringEvent(event: DBType.FundsEventRecurring)

    @Upsert
    suspend fun insertEvent(event: DBType.FundsEvent)

  /*  @Upsert
    suspend fun insertDay(day: DBType.DayWithEvents)*/

    @Query(" Select * From FundsRecurringEvents")
    fun readFundsRecurringEvents (): LiveData<List<DBType.FundsEventRecurring>>

    @Query(" Select * From FundsEvents")
    fun readFundsEvents (): LiveData<List<DBType.FundsEvent>>

/*    @Query(" Select * From DaysWithEvents")
    fun readDaysWithEvents (): LiveData<List<DBType.DayWithEvents>>*/

    @Query("DELETE FROM FundsRecurringEvents")
    fun cleanFundsRecurringEvents()

    @Query("DELETE FROM FundsEvents")
    fun cleanFundsEvents()

/*    @Query("DELETE FROM DaysWithEvents")
    fun clearDaysWithEvents()*/

}