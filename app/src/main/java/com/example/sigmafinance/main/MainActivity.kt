package com.example.sigmafinance.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sigmafinance.database.DBType
import com.example.sigmafinance.database.TemporaryLists
import com.example.sigmafinance.navigation.NavigationComponent
import com.example.sigmafinance.ui.theme.SigmaFinanceTheme
import com.example.sigmafinance.ui.theme.balanceGreen
import com.example.sigmafinance.ui.theme.balanceRed
import com.example.sigmafinance.ui.theme.customText
import com.example.sigmafinance.ui.theme.periwinkle
import com.example.sigmafinance.ui.theme.richBlack
import com.example.sigmafinance.ui.theme.standartText
import com.example.sigmafinance.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SigmaFinanceTheme {
            val viewModel: ViewModel by viewModels()
                NavigationComponent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController, viewModel: ViewModel){
    val coroutineScope = rememberCoroutineScope()
    val currentAmountOfMoney by viewModel.getMoneyValue().collectAsState(initial = 0.0f)
    var currentDate by remember { viewModel.currentDate }
    var listOfDays by remember { viewModel.listOfDays }
    var selectedEventsLists by remember {
        mutableStateOf(emptyList<TemporaryLists.DemonstrationEvent>())}
    var selectedNetIncome by remember { mutableFloatStateOf(0f) }
    val daysOfWeek = DayOfWeek.entries
    var selectedDate by remember { mutableStateOf("${currentDate.dayOfMonth}") }
    var updateListsTrigger by remember { mutableLongStateOf(0L) }
    var updateYearListsTrigger by remember { mutableLongStateOf(0L) }
    var updateYearListsKey by remember { mutableIntStateOf(0) }
    var toggleAddEventDialog by remember { mutableStateOf(false) }
    var toggleEnterNewValueDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember {mutableStateOf<TemporaryLists.DemonstrationEvent?>(null)}
    val lastLogin by viewModel.getLastLogin().collectAsState(initial = LocalDate.now())
    val events: List<DBType.FundsEvent> by viewModel.FundsEvents.observeAsState(emptyList())
    val recurringEvents: List<DBType.FundsEventRecurring> by viewModel.FundsEventsRecurring.observeAsState(emptyList())
    var currentMonthEvents by remember {
        mutableStateOf(emptyList<TemporaryLists.DemonstrationEvent>())
    }
    var newDate by remember { mutableStateOf(LocalDate.now()) }
    val currentYearEvents by viewModel.currentYearEvents.observeAsState(emptyList())
    LaunchedEffect(updateYearListsTrigger) {
        Log.d("Main Activity", "updateYearListsTrigger triggered, newDate is ${newDate}")
            viewModel.updateYearlyLists(updateYearListsKey, newDate)
    }
    fun IncrementDate() {
        newDate = currentDate.plusMonths(1)
        if (newDate.year > currentDate.year){
            Log.d("IncrementDate", "IncrementDate caused update yearly lists")
            Log.d("IncrementDate", "newDate is ${newDate}")
            updateYearListsKey = 2
            updateYearListsTrigger = System.currentTimeMillis()

        }
        currentDate = newDate
        listOfDays = getDaysInMonth(currentDate.year, currentDate.monthValue)
        selectedDate = "1"
        updateListsTrigger = System.currentTimeMillis()
        selectedEventsLists = currentMonthEvents.filter {  it.date.dayOfMonth.toString() == selectedDate } ?: emptyList()
        selectedNetIncome = selectedEventsLists.sumOf { it.amount.toDouble()}.toFloat()
    }
    fun DecrementDate() {
        newDate = currentDate.minusMonths(1)
        if (newDate.year < currentDate.year){
            Log.d("DecrementDate", "DecrementDate caused update yearly lists")
            updateYearListsKey = 1
            updateYearListsTrigger = System.currentTimeMillis()

        }
        currentDate = newDate
        listOfDays = getDaysInMonth(currentDate.year, currentDate.monthValue)
        selectedDate = "1"
        updateListsTrigger = System.currentTimeMillis()
        selectedEventsLists = currentMonthEvents.filter {  it.date.dayOfMonth.toString() == selectedDate } ?: emptyList()
        selectedNetIncome = selectedEventsLists.sumOf { it.amount.toDouble()}.toFloat()
    }
    LaunchedEffect(updateListsTrigger, events, recurringEvents, currentYearEvents) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.d("updateListsTrigger", "updateListsTrigger triggered")
            Log.d("updateListsTrigger", "currentMonthEvents before: $currentMonthEvents")
            Log.d("updateListsTrigger", "New value being taken from: $currentYearEvents")
            currentMonthEvents = currentYearEvents.filter { event -> event.date.month == currentDate.month}
            Log.d("updateListsTrigger", "currentMonthEvents after: $currentMonthEvents")

        }
    }
    LaunchedEffect(events, recurringEvents) {
        Log.d("Main Activity", "Launch Effect for loading events for 3 years has been called")
        updateYearListsKey  = 0
        updateYearListsTrigger = System.currentTimeMillis()
    }

    LaunchedEffect(Unit) {
        if (lastLogin != LocalDate.now()) {
            viewModel.saveMoneyValue(viewModel.getOccurencesBetweenLastAndCurrentLogin(lastLogin, LocalDate.now()))
            Log.d("Login date check", "Last login wasn't today, new money value has to be calculated")
            viewModel.saveLastLogin(LocalDate.now())
        }

    }

    Scaffold (
    floatingActionButton = {
        ExtendedFloatingActionButton(
            onClick = { toggleAddEventDialog = true},
            content = { Icon(Icons.Default.Add, contentDescription = "Add button", tint = Color.White) },
            containerColor = periwinkle
        )
    }
        ){  innerPadding ->
    Box(modifier = Modifier
        .fillMaxSize(1f)
        .background(richBlack)){


            Column(modifier = Modifier
                .background(color = richBlack)
                .padding(innerPadding)) {
                Row(modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(vertical = 24.dp)
                    .clickable {
                        toggleEnterNewValueDialog = true
                    },
                    horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.Top){Text(text = "$currentAmountOfMoney UAH", fontSize = 22.sp, color = periwinkle) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { DecrementDate()}) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Go to previous month", tint = Color.White)
                    }
                    Text(
                        text = "${currentDate.month} ${currentDate.year} ",
                        fontWeight = FontWeight.Bold,
                        style = customText,
                        color = Color.White
                    )
                    IconButton(onClick = { IncrementDate()  }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Go to next month", tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    daysOfWeek.forEach { day ->
                        Text(
                            text = DayOfWeekDisplay.from(day).shortName.first().toString(),
                            fontWeight = FontWeight.Bold,
                            style = standartText,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center
                ) {

                    val firstDayOfMonth = listOfDays.first()
                    val offset = firstDayOfMonth.dayOfWeek.value - 1
                    items(offset) {
                        Spacer(modifier = Modifier.size(40.dp))
                    }

                    items(listOfDays) { day ->
                        val dayNumber = day.dayOfMonth.toString()
                        val dayColor = animateColorAsState(
                            targetValue = if (dayNumber == selectedDate) periwinkle else Color.White,
                            animationSpec = tween(durationMillis = 500)
                        ).value
                        val templist = currentMonthEvents.filter {  it.date.dayOfMonth.toString() == dayNumber }
                        val netIncome = templist.sumOf { it.amount.toDouble()}.toFloat()

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(0.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = dayNumber,
                                    fontSize = 14.sp,
                                    color = dayColor,
                                    style = standartText,
                                    modifier = Modifier
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }) {
                                            selectedDate = dayNumber
                                            selectedEventsLists = templist
                                            selectedNetIncome = netIncome
                                        })
                                if (templist.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .clickable(onClick = { /* Handle click */ })
                                            .padding(all = 0.dp)
                                            .size(width = 25.dp, height = 3.dp)
                                            .padding(0.dp)
                                            .background(
                                                color = if (netIncome > 0) {
                                                    balanceGreen
                                                } else {
                                                    balanceRed
                                                },
                                                shape = RoundedCornerShape(16.dp)
                                            ),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                    }
                                }
                            }
                        }
                    }

                }
                Column(){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total: $selectedNetIncome", style = standartText,color = if (selectedNetIncome> 0){
                            balanceGreen
                        } else {
                            balanceRed
                        })
                    }
                    if( selectedEventsLists.isNotEmpty()){
                    LazyColumn(modifier = Modifier.fillMaxWidth()){

                            items(selectedEventsLists){ event ->
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                                    .combinedClickable(onClick = {},
                                        onLongClick = { selectedEvent = event }),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                )
                                {
                                    Text(text = event.name,
                                        style = TextStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 18.sp,
                                            color = Color.White
                                        ))

                                    Text(text = event.amount.toString(), style = TextStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp,
                                        color = if (event.amount > 0){
                                            Color.Green
                                        } else {
                                            Color.Red
                                        }
                                    ))

                                }
                            }
                        }
                    }
                }
                //Add new event dialog logic
                if(toggleAddEventDialog) {
                    AddFundsEvent(
                        onConfirm = { eventName: String, eventAmount: String, repeatInterval: Int, repeatUnit: String, endCondition: String, endAfterOccurrences: Int?, endDate: LocalDate? ->
                            if(repeatUnit == "Don't repeat") {
                                val newEvent = DBType.FundsEvent(
                                    name = eventName,
                                    amount = eventAmount.toFloat(),
                                    date = LocalDate.of(
                                        currentDate.year,
                                        currentDate.month,
                                        selectedDate.toInt()
                                    )
                                )
                                viewModel.InsertEvent(newEvent)
                            }
                            else{
                                val newEvent = DBType.FundsEventRecurring(
                                    name = eventName,
                                    startDate = LocalDate.of(
                                        currentDate.year,
                                        currentDate.month,
                                        selectedDate.toInt()
                                    ),
                                    amount = eventAmount.toFloat(),
                                    repeatInterval = repeatInterval,
                                    repeatUnit = repeatUnit,
                                    endCondition = endCondition,
                                    endAfterOccurrences = endAfterOccurrences,
                                    endDate = if(endCondition != "Never") {calculateEndDate(startDate = LocalDate.of(
                                        currentDate.year,
                                        currentDate.month,
                                        selectedDate.toInt()
                                    ),
                                        repeatInterval = repeatInterval,
                                    repeatUnit = repeatUnit,
                                        endCondition = endCondition,
                                        endAfterOccurrences =
                                endAfterOccurrences,
                                        endDate =
                                endDate)} else {null}
                                )
                                viewModel.InsertRecurringEvent(newEvent)
                            }

                    }, onDismiss = { toggleAddEventDialog = false })
                }

                selectedEvent?.let {
                    EditEvent(event = selectedEvent!!, onDismiss = { selectedEvent = null }, currentDate = currentDate, selectedDate = selectedDate.toInt(), viewModel = viewModel )
                }
                if(toggleEnterNewValueDialog) {
                    EnterNewMoneyValueDialog(previousValue = currentAmountOfMoney, onDismiss = { toggleEnterNewValueDialog = false}, onConfirm = { newValue: Float ->
                        coroutineScope.launch {
                        viewModel.saveMoneyValue(newValue)
                    }
                    })
                }




/*                Text(
                    text = "DEBUG" +
                            "{$selectedDate}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )*/
            }
        }
}
}

@Preview(showBackground = true)
@Composable
fun PreviewCalendar() {
    val currentDate = LocalDate.of(2024, 11, 1)  // Example for September 2024
    val listOfDays = (1..currentDate.lengthOfMonth()).map { LocalDate.of(currentDate.year, currentDate.month, it) }
    val daysOfWeek = DayOfWeek.entries
    Box(modifier = Modifier
        .fillMaxSize(1f)
        .background(Color.Black)){
        Column(modifier = Modifier
            .background(color = Color.Black)
            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = DayOfWeekDisplay.from(day).shortName,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            val firstDayOfMonth = listOfDays.first()
            val offset = firstDayOfMonth.dayOfWeek.value - 1
            // Create LazyVerticalGrid to show the days
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),  // 7 columns for days of the week
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(4.dp)
            ) {
                // Add blank items for the days before the first day of the month
                 // Adjust if Sunday is day 7
                items(offset) {
                    Spacer(modifier = Modifier.size(40.dp))  // Empty space before the first day
                }

                // Display the actual days
                items(listOfDays) { day ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = day.dayOfMonth.toString(),
                            color = Color.White)
                    }
                }
            }
            Text(text = "", color = Color.White)
        }
    }
}

