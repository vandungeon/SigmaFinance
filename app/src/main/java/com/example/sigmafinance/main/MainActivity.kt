package com.example.sigmafinance.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import com.example.sigmafinance.database.DBType
import com.example.sigmafinance.navigation.NavigationComponent
import com.example.sigmafinance.ui.theme.Purple40
import com.example.sigmafinance.ui.theme.SigmaFinanceTheme
import com.example.sigmafinance.ui.theme.Typography
import com.example.sigmafinance.ui.theme.standartText
import com.example.sigmafinance.viewmodel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController, viewModel: ViewModel){


    var currentDate by remember {
        viewModel.currentDate
    }
    var listOfDays by remember {
        viewModel.listOfDays
    }
    val daysOfWeek = DayOfWeek.entries

    var selectedDate by remember {
        mutableStateOf("${currentDate.dayOfMonth}")
    }

   // val DaysWithEvents: List<DBType.DayWithEvents> by viewModel.DaysWithEvents.observeAsState(emptyList())
    val Events: List<DBType.FundsEvent> by viewModel.FundsEvents.observeAsState(emptyList())
    val recurringEvents: List<DBType.FundsEventRecurring> by viewModel.FundsEventsRecurring.observeAsState(emptyList())
    var currentMonthEvents by remember {
        mutableStateOf(emptyList<DBType.SingleMonthEvents>())
    }
    var toggleAddEventDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(500L)
        val tempList1: MutableList<DBType.SingleMonthEvents> = mutableListOf()
        val tempList2: MutableList<DBType.SingleMonthEvents> = mutableListOf()

/*        DaysWithEvents.forEach { day ->
            day.listOfEvents.forEach { event ->
                tempList1.add(
                    DBType.SingleMonthEvents(
                        day.date.dayOfMonth,
                        event.name,
                        event.amount,
                        event.id,
                        "Regular"
                    )
                )
            }
        }*/
        Events.forEach { event ->
             tempList1.add(
                    DBType.SingleMonthEvents(
                        event.date.dayOfMonth,
                        event.name,
                        event.amount,
                        event.id,
                        "Normal"
                    )
                )

        }

        recurringEvents.forEach { event ->
            when (event.repeatUnit) {
                "Every Month" -> tempList2.add(
                    DBType.SingleMonthEvents(
                        event.startDate.dayOfMonth,
                        event.name,
                        event.amount,
                        event.id,
                        "Recurring"
                    )
                )
                "Every Week" -> {
                    // Add logic for weekly recurring events if needed
                }
            }
        }
        currentMonthEvents = tempList1 + tempList2
        Log.d("Events loading", "Loading finished\n" +
                "DaysWithEvents in month ${currentDate.month}: $tempList1\n" +
                "RecurringEvents in month ${currentDate.month}: $tempList2\n" +
                "Result: $currentMonthEvents")
    }

    fun IncrementDate() {
        val incrementedDate = currentDate.plusMonths(1)
        currentDate = incrementedDate
        listOfDays = getDaysInMonth(currentDate.year, currentDate.monthValue)
        selectedDate = "1"
    }
    fun DecrementDate() {
        val decrementDate = currentDate.minusMonths(1)
        currentDate = decrementDate
        listOfDays = getDaysInMonth(currentDate.year, currentDate.monthValue)
        selectedDate = "1"
    }

    Scaffold (
    floatingActionButton = {
        ExtendedFloatingActionButton(
            onClick = { toggleAddEventDialog = true},
            content = { Icon(Icons.Default.Add, contentDescription = "Add button" ) },
        )
    }
){  innerPadding ->
    Box(modifier = Modifier
        .fillMaxSize(1f)
        .background(Color.Black)){
            Column(modifier = Modifier
                .background(color = Color.Black)
                .padding(innerPadding)) {
                Row(modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.Top){Text(text = "0UAH", fontSize = 22.sp, color = Purple40) }

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
                            text = DayOfWeekDisplay.from(day).shortName,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Create LazyVerticalGrid to show the days
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(4.dp)
                ) {

                    val firstDayOfMonth = listOfDays.first()
                    val offset = firstDayOfMonth.dayOfWeek.value - 1
                    items(offset) {
                        Spacer(modifier = Modifier.size(40.dp))  // Empty space before the first day
                    }

                    // Display the actual days
                    items(listOfDays) { day ->
                        val isTapped by remember { mutableStateOf(false) }
                        val dayNumber = day.dayOfMonth.toString()
                        val dayColor = animateColorAsState(
                            targetValue = if (dayNumber == selectedDate) Purple40 else Color.White,
                            animationSpec = tween(durationMillis = 500)
                        ).value

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = dayNumber, fontSize = 14.sp, color = dayColor, modifier = Modifier
                                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }){ selectedDate = dayNumber
                                    isTapped != isTapped })
                        }
                    }
                }
                Box(){
                    LazyColumn(modifier = Modifier.fillMaxWidth()){

                    }
                }
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
                                    endDate = calculateEndDate(startDate = LocalDate.of(
                                        currentDate.year,
                                        currentDate.month,
                                        selectedDate.toInt()
                                    ),
                                        repeatInterval,
                                    repeatUnit,
                                endCondition,
                                endAfterOccurrences,
                                endDate)
                                )
                                viewModel.InsertRecurringEvent(newEvent)
                            }

                    }, onDismiss = { toggleAddEventDialog = false })
                }
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

