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
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@HiltViewModel
class ViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    private val context: Context get() = getApplication<Application>().applicationContext
    private val repository: Repository
    private val dbDao = Days_and_events_db.getDatabase(application).dao()

    private val _fundsEvents = MediatorLiveData<List<DBType.FundsEvent>>()
    val fundsEvents: LiveData<List<DBType.FundsEvent>> = _fundsEvents
    private val _fundsEventsRecurring = MediatorLiveData<List<DBType.FundsEventRecurring>>()
    val fundsEventsRecurring: LiveData<List<DBType.FundsEventRecurring>> = _fundsEventsRecurring

    private var _currentDate = mutableStateOf(LocalDate.now())
    val currentDate = _currentDate
    private var _listOfDays = mutableStateOf(getDaysInMonth(currentDate.value.year, currentDate.value.monthValue))
    val listOfDays = _listOfDays

    init {
        repository = Repository(dbDao)
        _fundsEvents.addSource(repository.readFundsEvents) { events ->
            _fundsEvents.value = events
            Log.d("viewModel", "Events data loaded: $events")
        }
        _fundsEventsRecurring.addSource(repository.readFundsRecurringEvents) { eventsRecurring ->
            _fundsEventsRecurring.value = eventsRecurring
            Log.d("viewModel", "Events data loaded: $eventsRecurring")
        }
        checkInitialization()
    }
    private val _isInitialized = mutableStateOf(false)

    private fun checkInitialization() {
        if (_fundsEvents.value != null && _fundsEventsRecurring.value != null) {
            _isInitialized.value = true
        }
    }

    fun insertRecurringEvent(event: DBType.FundsEventRecurring) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertRecurringEvent(event)
        }
    }

    fun insertEvent(event: DBType.FundsEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertEvent(event)
        }
    }

    fun getEventById(eventId: Int): DBType.FundsEvent? {
        return fundsEvents.value?.find { it.id == eventId }
    }

    fun getRecurringEventById(eventId: Int): DBType.FundsEventRecurring? {
        return fundsEventsRecurring.value?.find { it.id == eventId }
    }

    fun updateEvent(event: DBType.FundsEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEvent(event)
        }
    }

    fun updateEventRecurring(event: DBType.FundsEventRecurring) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEventRecurring(event)
        }
    }

    fun deleteEvent(event: DBType.FundsEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEvent(event)
        }
    }

    fun deleteEventRecurring(event: DBType.FundsEventRecurring) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEventRecurring(event)
        }
    }

    private val Context.dataStore by preferencesDataStore("MoneyValue")

    private val moneyValueKey = floatPreferencesKey("MoneyValue_Key")

    suspend fun saveMoneyValue(value: Float) {
        context.dataStore.edit { preferences ->
            preferences[moneyValueKey] = value
        }
    }

    fun getMoneyValue(): Flow<Float> {
        return context.dataStore.data.map { preferences ->
            preferences[moneyValueKey] ?: 0.0f
        }
    }
    //
    private val budgetValueKey = floatPreferencesKey("budgetValue_Key")

    suspend fun saveBudgetValue(value: Float) {
        context.dataStore.edit { preferences ->
            preferences[budgetValueKey] = value
        }
    }

    fun getBudgetValue(): Flow<Float> {
        return context.dataStore.data.map { preferences ->
            preferences[budgetValueKey] ?: 0.0f
        }
    }

    private val lastLoginDateKey = stringPreferencesKey("last_login_date")

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
    private var _currentYearEvents = MutableLiveData<List<TemporaryLists.DemonstrationEvent>>()
    var currentYearEvents: LiveData<List<TemporaryLists.DemonstrationEvent>> = _currentYearEvents
    private var _nextYearEvents = MutableLiveData<List<TemporaryLists.DemonstrationEvent>>()
    private var _currentBudgetMonthEvents = MutableLiveData<List<TemporaryLists.DemonstrationEvent>>()
    var currentBudgetMonthEvents: LiveData<List<TemporaryLists.DemonstrationEvent>> = _currentBudgetMonthEvents
     fun updateYearlyLists(direction: Int, newDate: LocalDate) {
        Log.d("ViewModel", "updateYearlyLists has been called")
        val fundsEvents = _fundsEvents.value ?: emptyList()
        val fundsEventsRecurring = _fundsEventsRecurring.value ?: emptyList()

        suspend fun fetchEventsForYear(
            scope: CoroutineScope,
            year: Int
        ): List<TemporaryLists.DemonstrationEvent> {
            val regularEventsDeferred =
                scope.async(Dispatchers.IO) { getOccurrencesForYearStatic(fundsEvents, year) }
            val recurringEventsDeferred = scope.async(Dispatchers.IO) {
                getOccurrencesForYearRecurring(fundsEventsRecurring, year
                )
            }
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
                    _currentBudgetMonthEvents.postValue(
                        currentEvents.filter { it.date.monthValue == currentDate.value.monthValue
                    })
                    Log.d(
                        "Events Loading", """
                    Loading finished, direction 0:
                    Current Year (${newDate.year}): $currentEvents
                    Previous Year (${newDate.year - 1}): $previousEvents
                    Next Year (${newDate.year + 1}): $nextEvents
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
                    Previous Year (${newDate.year - 1}): $previousEvents
                    Next Year (${newDate.year + 1}): ${_nextYearEvents.value}
                    """.trimIndent()
                    )
                }

                2 -> { // Shift forwards: next -> current, current -> previous
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
                    nextEventsDeferred : $nextEvents
                    Next Year (${newDate.year + 1}): ${_nextYearEvents.value}
                    """.trimIndent()
                    )
                }
            }
            Log.d("ViewModel", "updateYearlyLists finished")
        }
    }


    fun getOccurrencesBetweenLastAndCurrentLogin(lastLogin: LocalDate, newLogin: LocalDate, key: Int): Any {
        val occurrences = mutableListOf<TemporaryLists.DemonstrationEvent>()
        fun alignToStart(date: LocalDate, interval: Long, unit: ChronoUnit, start: LocalDate
        ): LocalDate {
            val periodsBetween = unit.between(date, start) / interval
            return date.plus(periodsBetween * interval, unit).let {
                if (it.isBefore(start)) it.plus(interval, unit) else it
            }
        }
        fun generateDesiredSequence(
            event: DBType.FundsEventRecurring,
            unit: ChronoUnit
        ): List<TemporaryLists.DemonstrationEvent> {
            val firstOccurrence =
                alignToStart(event.startDate, event.repeatInterval.toLong(), unit, lastLogin)
            return generateSequence(firstOccurrence) { current ->
                current.plus(event.repeatInterval.toLong(), unit)
            }.takeWhile { current ->
                current <= newLogin && (event.endDate == null || current <= event.endDate)
            }.map { current ->
                val maxDayOfMonth = current.lengthOfMonth()
                TemporaryLists.DemonstrationEvent(
                    referenceId = event.id,
                    name = event.name,
                    date = LocalDate.of(
                        current.year,
                        current.month,
                        event.startDate.dayOfMonth.coerceAtMost(maxDayOfMonth)
                    ),
                    amount = event.amount,
                    type = "Recurring"
                )
            }.toList()
        }
        if (key == 0) {
            occurrences.addAll(
                fundsEvents.value?.asSequence()
                    ?.filter { event -> event.date in lastLogin..newLogin }
                    ?.map { event ->
                        TemporaryLists.DemonstrationEvent(
                            referenceId = event.id,
                            name = event.name,
                            date = event.date,
                            amount = event.amount,
                            type = "Static"
                        )
                    }
                    ?.toList() ?: emptyList())
        }
        val filteredFundsEventRecurring = fundsEventsRecurring.value?.filter { event ->
            event.startDate <= newLogin
        } ?: emptyList()
        occurrences.addAll(
            filteredFundsEventRecurring.flatMap { event ->
                when (event.repeatUnit) {
                    "Months" -> { generateDesiredSequence(event, ChronoUnit.MONTHS) }
                    "Weeks" -> { generateDesiredSequence(event, ChronoUnit.WEEKS) }
                    "Days" -> { generateDesiredSequence(event, ChronoUnit.DAYS) }
                    "Years" -> { generateDesiredSequence(event, ChronoUnit.YEARS) }
                    else -> emptyList()
                }
            }
        )
        val totalValue = occurrences.fold(0f) { acc, event -> acc + event.amount }
        return if(key == 0) {
            totalValue
        } else{
            occurrences
        }
    }
    fun moneyProjection(startDate: LocalDate, goalDate: LocalDate?, targetAmount: Float?, baseAmount: Float): Pair<Float?, LocalDate?> {
        val occurrences = mutableListOf<TemporaryLists.DemonstrationEvent>()
        fun alignToStart(date: LocalDate, interval: Long, unit: ChronoUnit, start: LocalDate
        ): LocalDate {
            val periodsBetween = unit.between(date, start) / interval
            return date.plus(periodsBetween * interval, unit).let {
                if (it.isBefore(start)) it.plus(interval, unit) else it
            }
        }
        fun generateDesiredSequence(
            event: DBType.FundsEventRecurring,
            unit: ChronoUnit
        ): List<TemporaryLists.DemonstrationEvent> {
            val firstOccurrence = alignToStart(event.startDate, event.repeatInterval.toLong(), unit, startDate)
            return generateSequence(firstOccurrence) { current ->
                current.plusMonths(event.repeatInterval.toLong())
            }.takeWhile { current ->
                current <= goalDate && (event.endDate == null || current <= event.endDate)
            }.map { current ->
                val maxDayOfMonth = current.lengthOfMonth()
                TemporaryLists.DemonstrationEvent(
                    referenceId = event.id,
                    name = event.name,
                    date = LocalDate.of(
                        current.year,
                        current.month,
                        event.startDate.dayOfMonth.coerceAtMost(maxDayOfMonth)
                    ),
                    amount = event.amount,
                    type = "Recurring"
                )
            }.toList()
        }
        if (targetAmount == 0f && goalDate != null) {
            occurrences.addAll(
                fundsEvents.value?.asSequence()
                    ?.filter { event -> event.date in startDate..goalDate }
                    ?.map { event ->
                        TemporaryLists.DemonstrationEvent(
                            referenceId = event.id,
                            name = event.name,
                            date = event.date,
                            amount = event.amount,
                            type = "Static"
                        )
                    }?.toList() ?: emptyList())
            val filteredFundsEventRecurring = fundsEventsRecurring.value?.filter { event ->
                event.startDate <= goalDate
            } ?: emptyList()
            occurrences.addAll(
                filteredFundsEventRecurring.flatMap { event ->
                    when (event.repeatUnit) {
                        "Months" -> { generateDesiredSequence(event, ChronoUnit.MONTHS) }
                        "Weeks" -> { generateDesiredSequence(event, ChronoUnit.WEEKS) }
                        "Days" -> { generateDesiredSequence(event, ChronoUnit.DAYS) }
                        "Years" -> { generateDesiredSequence(event, ChronoUnit.YEARS) }
                        else -> emptyList()
                    }
                }
            )
            val totalValue = occurrences.fold(0f) { acc, event -> acc + event.amount }
            return Pair(totalValue + baseAmount, null)
        }
        else {
            val goalAmount: Float? = targetAmount
            val finalGoalAmount = goalAmount ?: 0f
            val foundDate: LocalDate
            val tempListOfOccurrences = mutableListOf<TemporaryLists.DemonstrationEvent>()
            val differenceBetween: Float = getOccurrencesBetweenLastAndCurrentLogin(startDate, startDate.plusMonths(1), 0) as Float
            val requiredMonthsProjection = ((finalGoalAmount - baseAmount) / differenceBetween)
            Log.d("Money Projection", "FinalGoalAmount is $finalGoalAmount")
            Log.d("Money Projection", "differenceBetween is $differenceBetween")
            Log.d("Money Projection", "requiredMonthsProjection is $requiredMonthsProjection")
            if (requiredMonthsProjection.toDouble() != 0.0) {
                if (requiredMonthsProjection.toDouble() > 50 ){
                    return Pair(null, null)
                }
                val finalRequiredMonthsProjection = requiredMonthsProjection.roundToInt()
                @Suppress("UNCHECKED_CAST")
                tempListOfOccurrences.addAll(getOccurrencesBetweenLastAndCurrentLogin(
                    startDate,
                    startDate.plusMonths((1).toLong()),
                    1
                ) as List<TemporaryLists.DemonstrationEvent>)
                for (i in 1 until finalRequiredMonthsProjection) {
                    val tempOccurrences = getOccurrencesBetweenLastAndCurrentLogin(
                        startDate.plusMonths(i.toLong()),
                        startDate.plusMonths((i + 1).toLong()),
                        1
                    )
                    if (tempOccurrences is List<*>) {
                        @Suppress("UNCHECKED_CAST")
                        tempListOfOccurrences.addAll(tempOccurrences as List<TemporaryLists.DemonstrationEvent>)
                    } else {
                        Log.d("MoneyProjection","Unexpected return type from getOccurrencesBetweenLastAndCurrentLogin: ${tempOccurrences::class}")
                    }
                }
                val occurrencesByDate: Map<LocalDate, List<TemporaryLists.DemonstrationEvent>> =
                    tempListOfOccurrences.groupBy { it.date }

                var accumulatedAmount = baseAmount
                for ((date, events) in occurrencesByDate.toSortedMap()) {
                    for (event in events) {
                        accumulatedAmount += event.amount
                        if (accumulatedAmount >= finalGoalAmount){
                            foundDate = date
                            return Pair(null, foundDate)
                        }
                    }
                    if (accumulatedAmount >= finalGoalAmount){
                        foundDate = date
                        return Pair(null, foundDate)
                    }
                }
            } else {
                Log.d("MoneyProjection", "Division by zero")
            }
            return Pair(null, null)
        }
    }
    suspend fun analyticsGetIncomeAndExpenses(yearWorthOfEvents: List<TemporaryLists.DemonstrationEvent>): List<Pair<Float, Float>> {
        val monthlyResults = Array(12) { 0f to 0f }.toMutableList()
        withContext(Dispatchers.Default) {
            val (incomes, expenses) = yearWorthOfEvents.partition { it.amount > 0f }
            val incomeByMonth = incomes.groupBy { it.date.monthValue - 1 }
            val expenseByMonth = expenses.groupBy { it.date.monthValue - 1 }
            (0..11).map { month ->
                async {
                    val income = incomeByMonth[month]?.sumOf { it.amount.toDouble() }?.toFloat() ?: 0f
                    val expense = expenseByMonth[month]?.sumOf { (-it.amount).toDouble() }?.toFloat() ?: 0f
                    month to (income to expense)
                }
            }.awaitAll().forEach { (month, pair) -> if (pair.first > 0.1f || pair.second > 0.1f) {
                Log.d ("ViewModel", "(${pair.first}), (${pair.second}) pair of income and expense set to the list")
                monthlyResults[month] = pair} }
        }
        return monthlyResults
    }
}