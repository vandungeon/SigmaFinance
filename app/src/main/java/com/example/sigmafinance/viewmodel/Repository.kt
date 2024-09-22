package com.example.sigmafinance.viewmodel

import androidx.lifecycle.LiveData
import com.example.sigmafinance.database.DBType
import com.example.sigmafinance.database.DataAccessObjects

class Repository (private val dataAccessObjects: DataAccessObjects){

    suspend fun InsertRecurringEvent(event: DBType.FundsEventRecurring){
        dataAccessObjects.insertRecurringEvent(event)
    }
    suspend fun InsertEvent(event: DBType.FundsEvent){
        dataAccessObjects.insertEvent(event)
    }

/*    suspend fun InsertDay(day: DBType.DayWithEvents){
        dataAccessObjects.insertDay(day)
    }*/

    val readFundsRecurringEvents: LiveData<List<DBType.FundsEventRecurring>> = dataAccessObjects.readFundsRecurringEvents()
    val readFundsEvents: LiveData<List<DBType.FundsEvent>> = dataAccessObjects.readFundsEvents()

/*    val readDaysWithEvents: LiveData<List<DBType.DayWithEvents>> = dataAccessObjects.readDaysWithEvents()*/

    fun cleanFundsRecurringEvents(){
        dataAccessObjects.cleanFundsRecurringEvents()
    }
    fun cleanFundsEvents(){
        dataAccessObjects.cleanFundsEvents()
    }

/*    fun clearDaysWithEvents(){
        dataAccessObjects.clearDaysWithEvents()
    }*/
}