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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.MutableLiveData
import com.example.sigmafinance.database.TemporaryLists
import com.example.sigmafinance.main.getOccurrencesForYearRecurring
import com.example.sigmafinance.main.getOccurrencesForYearStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@HiltViewModel
class ViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    private val context: Context get() = getApplication<Application>().applicationContext
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
    val _isInitialized = mutableStateOf(false)
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

     fun getEventById(eventId: Int): DBType.FundsEvent? {
            return FundsEvents.value?.find { it.id == eventId }
    }
     fun getRecurringEventById(eventId: Int): DBType.FundsEventRecurring? {
        return FundsEventsRecurring.value?.find { it.id == eventId }
    }

    fun updateEvent(event: DBType.FundsEvent){
        viewModelScope.launch (Dispatchers.IO){
            repository.updateEvent(event)
        }
    }
    fun updateEventRecurring(event: DBType.FundsEventRecurring){
        viewModelScope.launch (Dispatchers.IO){
            repository.updateEventRecurring(event)
        }
    }
    fun deleteEvent(event: DBType.FundsEvent){
        viewModelScope.launch (Dispatchers.IO){
            repository.deleteEvent(event)
        }
    }
    fun deleteEventRecurring(event: DBType.FundsEventRecurring){
        viewModelScope.launch (Dispatchers.IO){
            repository.deleteEventRecurring(event)
        }
    }
    val Context.dataStore by preferencesDataStore("MoneyValue")

    val moneyValue_key = floatPreferencesKey("MoneyValue_Key")

    suspend fun saveMoneyValue(value: Float) {
        context.dataStore.edit { preferences ->
            preferences[moneyValue_key] = value
        }
    }
    fun getMoneyValue(): Flow<Float> {
        return context.dataStore.data.map { preferences ->
            preferences[moneyValue_key] ?: 0.0f
        }
    }
    val lastLoginDateKey = stringPreferencesKey("last_login_date")

    suspend fun saveLastLogin(date: LocalDate) {
        context.dataStore.edit { preferences ->
            preferences[lastLoginDateKey] = date.toString()
        }
    }
     fun getLastLogin(): Flow<LocalDate> {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        return context.dataStore.data.map { preferences ->
            val string = preferences[lastLoginDateKey] ?: LocalDate.now().format(formatter)
            LocalDate.parse(string, formatter)
        }
    }
    private var _previousYearEvents = MutableLiveData<List<TemporaryLists.DemonstrationEvent>>()
    var previousYearEvents: LiveData<List<TemporaryLists.DemonstrationEvent>> = _previousYearEvents

    private var _currentYearEvents = MutableLiveData<List<TemporaryLists.DemonstrationEvent>>()
    var currentYearEvents: LiveData<List<TemporaryLists.DemonstrationEvent>> = _currentYearEvents

    private var _nextYearEvents = MutableLiveData<List<TemporaryLists.DemonstrationEvent>>()
    var nextYearEvents: LiveData<List<TemporaryLists.DemonstrationEvent>> = _nextYearEvents

    suspend fun updateYearlyLists(direction: Int, newDate: LocalDate) {
        Log.d("ViewModel", "updateYearlyLists has been called")
        val fundsEvents = _FundsEvents.value ?: emptyList()
        val fundsEventsRecurring = _FundsEventsRecurring.value ?: emptyList()

        suspend fun fetchEventsForYear(scope: CoroutineScope, year: Int): List<TemporaryLists.DemonstrationEvent> {
            val regularEventsDeferred = scope.async(Dispatchers.IO) { getOccurrencesForYearStatic(fundsEvents, year) }
            val recurringEventsDeferred = scope.async(Dispatchers.IO) { getOccurrencesForYearRecurring(fundsEventsRecurring, year) }
            return regularEventsDeferred.await() + recurringEventsDeferred.await()
        }

        viewModelScope.launch {
            when (direction) {
                0 -> {

                    val currentYearDeferred = async { fetchEventsForYear(this, newDate.year) }
                    val previousYearDeferred = async { fetchEventsForYear(this, newDate.year - 1) }
                    val nextYearDeferred = async { fetchEventsForYear(this, newDate.year + 1) }

                    val currentEvents = currentYearDeferred.await()
                    val nextEvents = nextYearDeferred.await()
                    val previousEvents = previousYearDeferred.await()
                    _previousYearEvents.postValue(previousEvents)
                    _currentYearEvents.postValue(currentEvents)
                    _nextYearEvents.postValue(nextEvents)
                    Log.d(
                        "Events Loading", """
                    Loading finished, direction 0:
                    Current Year (${newDate.year}): ${currentEvents}
                    Previous Year (${newDate.year - 1}): ${previousEvents}
                    Next Year (${newDate.year + 1}): ${nextEvents}
                    """.trimIndent()
                    )
                }


                1 -> { // Shift backward: current -> next, previous -> current
                    _nextYearEvents.postValue(_currentYearEvents.value)
                    _currentYearEvents.postValue(_previousYearEvents.value)

                    val previousEventsDeferred = async {
                        fetchEventsForYear(this, newDate.year - 1)
                    }
                    val previousEvents = previousEventsDeferred.await()
                    _previousYearEvents.postValue(previousEvents)
                    Log.d(
                        "Events Loading", """
                    Loading finished, direction 1:
                    Current Year (${newDate.year}): ${_currentYearEvents.value}
                    Previous Year (${newDate.year - 1}): ${previousEvents}
                    Next Year (${newDate.year + 1}): ${_nextYearEvents.value}
                    """.trimIndent()
                    )
                }

                2 -> {
                    _previousYearEvents.postValue(_currentYearEvents.value)
                    _currentYearEvents.postValue(_nextYearEvents.value)

                    val nextEventsDeferred = async {
                        fetchEventsForYear(this, newDate.year + 1)
                    }

                    val nextEvents = nextEventsDeferred.await()
                    _nextYearEvents.postValue(nextEvents)
                    Log.d(
                        "Events Loading", """
                            
                    Loading finished, direction 2:
                    Current Year (${newDate.year}): ${_currentYearEvents.value}
                    Previous Year (${newDate.year - 1}): ${_previousYearEvents.value}
                    nextEventsDeferred : ${nextEvents}
                    Next Year (${newDate.year + 1}): ${_nextYearEvents.value}
                    """.trimIndent()
                    )
                }
            }
            Log.d("ViewModel", "updateYearlyLists finished")
        }
    }


    fun getOccurencesBetweenLastAndCurrentLogin(LastLogin: LocalDate, NewLogin: LocalDate):  Float {
        val occurrences = mutableListOf<TemporaryLists.DemonstrationEvent>()

        val filteredFundsEvents = FundsEvents.value?.filter { event ->
            event.date in LastLogin..NewLogin
        } ?: emptyList()
        for (event in filteredFundsEvents) {
            val eventOccurrence = TemporaryLists.DemonstrationEvent(
                referenceId = event.id,
                name = event.name,
                date = event.date,
                amount = event.amount,
                type = "Static"
            )
            occurrences.add(eventOccurrence)
        }
        val filteredFundsEventRecurring = FundsEventsRecurring.value?.filter { event ->
            event.startDate <= NewLogin
        } ?: emptyList()
        for (event in filteredFundsEventRecurring) {
            var current = event.startDate
            if (event.startDate.isBefore(LastLogin)) {
                val daysBetween = ChronoUnit.DAYS.between(event.startDate, LastLogin)
                current = when (event.repeatUnit) {
                    "Months" -> {
                        val monthsBetween = ChronoUnit.MONTHS.between(event.startDate, LastLogin)
                        val adjustedMonths = (monthsBetween / event.repeatInterval) * event.repeatInterval
                        event.startDate.plusMonths(adjustedMonths)
                    }
                    "Weeks" -> {
                        val weeksBetween = ChronoUnit.WEEKS.between(event.startDate, LastLogin)
                        val adjustedWeeks = (weeksBetween / event.repeatInterval) * event.repeatInterval
                        event.startDate.plusWeeks(adjustedWeeks)
                    }
                    "Days" -> {
                        val adjustedDays = (daysBetween / event.repeatInterval) * event.repeatInterval
                        event.startDate.plusDays(adjustedDays)
                    }
                    "Years" -> {
                        val yearsBetween = ChronoUnit.YEARS.between(event.startDate, LastLogin)
                        val adjustedYears = (yearsBetween / event.repeatInterval) * event.repeatInterval
                        event.startDate.plusYears(adjustedYears)
                    }
                    else -> event.startDate
                }
            }

            while (current.isBefore(LastLogin)) {
                current = when (event.repeatUnit) {
                    "Months" -> current.plusMonths(event.repeatInterval.toLong())
                    "Weeks" -> current.plusWeeks(event.repeatInterval.toLong())
                    "Days" -> current.plusDays(event.repeatInterval.toLong())
                    "Years" -> current.plusYears(event.repeatInterval.toLong())
                    else -> break
                }
            }
            while ((current.isBefore(NewLogin) || current.isEqual(NewLogin)) && (current.isBefore(event.endDate) || current.isEqual(event.endDate))) {
                if (current in LastLogin..NewLogin) {
                    val eventOccurrence = TemporaryLists.DemonstrationEvent(
                        referenceId = event.id,
                        name = event.name,
                        date = current,
                        amount = event.amount,
                        type = "Recurring"
                    )
                    occurrences.add(eventOccurrence)
                }
                current = when (event.repeatUnit) {
                    "Months" -> current.plusMonths(event.repeatInterval.toLong())
                    "Weeks" -> current.plusWeeks(event.repeatInterval.toLong())
                    "Days" -> current.plusDays(event.repeatInterval.toLong())
                    "Years" -> current.plusYears(event.repeatInterval.toLong())
                    else -> break
                }
            }
        }
        val totalValue = occurrences.fold(0f) { acc, event -> acc + event.amount }
        return totalValue
    }











   /* suspend fun getOccurrencesForMonthRecurring(
        year: Int,
        month: Int
    ): MutableList<TemporaryLists.DemonstrationEvent> {
        val occurrences = mutableListOf<TemporaryLists.DemonstrationEvent>()
        val startOfMonth = LocalDate.of(year, month, 1)
        val endOfMonth = startOfMonth.plusMonths(1).minusDays(1)
        val filteredFundsEventRecurring = FundsEventsRecurring.value?.filter { event ->
            !(event.endDate != null && event.endDate.isBefore(startOfMonth)) && !(event.startDate.isAfter(endOfMonth))
        } ?: emptyList()


        for (event in filteredFundsEventRecurring) {
            var current = event.startDate
            when (event.repeatUnit) {
                "Months" -> {
                    while (current.isBefore(startOfMonth)) {
                        current = current.plusMonths(event.repeatInterval.toLong())
                    }
                    if (current.monthValue == month && current.year == year) {
                        val eventOccurrence = TemporaryLists.DemonstrationEvent(
                            referenceId = event.id,
                            name = event.name,
                            date = current,
                            amount = event.amount,
                            type = "Static"
                        )
                        occurrences.add(eventOccurrence)
                    }
                }
                "Weeks" -> {
                    while (current.isBefore(startOfMonth)) {
                        current = current.plusWeeks(event.repeatInterval.toLong())
                    }
                    while (current.isBefore(endOfMonth) || current.isEqual(endOfMonth)) {
                        val eventOccurrence = TemporaryLists.DemonstrationEvent(
                            referenceId = event.id,
                            name = event.name,
                            date = current,
                            amount = event.amount,
                            type = "Recurring"
                        )
                        occurrences.add(eventOccurrence)
                        current = current.plusWeeks(event.repeatInterval.toLong())
                    }
                }
                "Days" -> {
                    while (current.isBefore(startOfMonth)) {
                        current = current.plusDays(event.repeatInterval.toLong())
                    }
                    while (current.isBefore(endOfMonth) || current.isEqual(endOfMonth)) {
                        val eventOccurrence = TemporaryLists.DemonstrationEvent(
                            referenceId = event.id,
                            name = event.name,
                            date = current,
                            amount = event.amount,
                            type = "Recurring"
                        )
                        occurrences.add(eventOccurrence)
                        current = current.plusDays(event.repeatInterval.toLong())
                    }
                }
                "Years" -> {
                    while (current.isBefore(startOfMonth)) {
                        current = current.plusYears(event.repeatInterval.toLong())
                    }
                    while (current.isBefore(endOfMonth) || current.isEqual(endOfMonth)) {
                        val eventOccurrence = TemporaryLists.DemonstrationEvent(
                            referenceId = event.id,
                            name = event.name,
                            date = current,
                            amount = event.amount,
                            type = "Recurring"
                        )
                        occurrences.add(eventOccurrence)
                        current = current.plusYears(event.repeatInterval.toLong())
                    }
                }
            }
        }
        return occurrences
    }*/
}