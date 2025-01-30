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

    suspend fun updateEvent(event: DBType.FundsEvent){
        dataAccessObjects.updateEvent(event)
    }
    suspend fun updateEventRecurring(event: DBType.FundsEventRecurring){
        dataAccessObjects.updateEventRecurring(event)
    }
    suspend fun deleteEvent(event: DBType.FundsEvent){
        dataAccessObjects.deleteEvent(event)
    }
    suspend fun deleteEventRecurring(event: DBType.FundsEventRecurring){
        dataAccessObjects.deleteEventRecurring(event)
    }
    val readFundsRecurringEvents: LiveData<List<DBType.FundsEventRecurring>> = dataAccessObjects.readFundsRecurringEvents()
    val readFundsEvents: LiveData<List<DBType.FundsEvent>> = dataAccessObjects.readFundsEvents()


    fun cleanFundsRecurringEvents(){
        dataAccessObjects.cleanFundsRecurringEvents()
    }
    fun cleanFundsEvents(){
        dataAccessObjects.cleanFundsEvents()
    }

}