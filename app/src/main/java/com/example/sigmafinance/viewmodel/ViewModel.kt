package com.example.sigmafinance.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.example.sigmafinance.database.DBType
import com.example.sigmafinance.database.Days_and_events_db
import com.example.sigmafinance.main.getDaysInMonth
import com.example.sigmafinance.main.getFirstDayOfMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val repository: Repository
    private val dbDao = Days_and_events_db.getDatabase(application).dao()

    private val _FundsEvents = MediatorLiveData<List<DBType.FundsEvent>>()
        val FundsEvents: LiveData<List<DBType.FundsEvent>> = _FundsEvents
    private val _FundsEventsRecurring = MediatorLiveData<List<DBType.FundsEventRecurring>>()
        val FundsEventsRecurring: LiveData<List<DBType.FundsEventRecurring>> = _FundsEventsRecurring
/*    private val _DaysWithEvents = MediatorLiveData<List<DBType.DayWithEvents>>()
        val DaysWithEvents: LiveData<List<DBType.DayWithEvents>> = _DaysWithEvents*/

    private var _currentDate = mutableStateOf(LocalDate.now())
        val currentDate = _currentDate
    private var _listOfDays = mutableStateOf(getDaysInMonth(currentDate.value.year, currentDate.value.monthValue))
        val listOfDays = _listOfDays

    init {
        repository = Repository(dbDao)
        _FundsEvents.addSource(repository.readFundsEvents) { events->
            _FundsEvents.value = events
            Log.d("viewModel", "Events data loaded: $events")
        }
        _FundsEventsRecurring.addSource(repository.readFundsRecurringEvents) { eventsRecurring ->
            _FundsEventsRecurring.value = eventsRecurring
            Log.d("viewModel", "Events data loaded: $eventsRecurring")
        }
/*        _DaysWithEvents.addSource(repository.readDaysWithEvents) { days ->
            _DaysWithEvents.value = days
            Log.d("viewModel", "Events data loaded: $days")
        }*/
        checkInitialization()
    }
    private val _isInitialized = mutableStateOf(false)
    val isInitialized: Boolean get() = _isInitialized.value

    private fun checkInitialization() {
        if (_FundsEvents.value != null && _FundsEventsRecurring.value != null) {
            _isInitialized.value = true
        }
    }
    fun updateDate(newDate: LocalDate) {
        currentDate.value = newDate
        listOfDays.value = getDaysInMonth(newDate.year, newDate.monthValue)
    }
    fun InsertRecurringEvent(event: DBType.FundsEventRecurring){
        viewModelScope.launch(Dispatchers.IO) {
            repository.InsertRecurringEvent(event)
        }
    }
    fun InsertEvent(event: DBType.FundsEvent){
        viewModelScope.launch(Dispatchers.IO) {
            repository.InsertEvent(event)
        }
    }

/*    suspend fun InsertDay(day: DBType.DayWithEvents){
        repository.InsertDay(day)
    }*/

    fun cleanFundsRecurringEvents(){
        repository.cleanFundsRecurringEvents()
    }
    fun cleanFundsEvents(){
        repository.cleanFundsEvents()
    }

/*    fun clearDaysWithEvents(){
        repository.clearDaysWithEvents()
    }*/

}