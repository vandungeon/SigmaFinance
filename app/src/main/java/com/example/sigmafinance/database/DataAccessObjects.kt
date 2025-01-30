package com.example.sigmafinance.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
@Dao
interface DataAccessObjects {


    @Upsert
    suspend fun insertRecurringEvent(event: DBType.FundsEventRecurring)

    @Upsert
    suspend fun insertEvent(event: DBType.FundsEvent)

    @Update
    suspend fun updateEvent(event: DBType.FundsEvent)
    @Update
    suspend fun updateEventRecurring(event: DBType.FundsEventRecurring)
    @Delete
    suspend fun deleteEvent(event: DBType.FundsEvent)
    @Delete
    suspend fun deleteEventRecurring(event: DBType.FundsEventRecurring)

    @Query(" Select * From FundsRecurringEvents")
    fun readFundsRecurringEvents (): LiveData<List<DBType.FundsEventRecurring>>

    @Query(" Select * From FundsEvents")
    fun readFundsEvents (): LiveData<List<DBType.FundsEvent>>


    @Query("DELETE FROM FundsRecurringEvents")
    fun cleanFundsRecurringEvents()

    @Query("DELETE FROM FundsEvents")
    fun cleanFundsEvents()


}