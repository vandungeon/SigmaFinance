package com.example.sigmafinance.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.sigmafinance.database.DBType
import com.example.sigmafinance.database.TemporaryLists
import com.example.sigmafinance.navigation.NavigationComponent
import com.example.sigmafinance.ui.theme.Purple40
import com.example.sigmafinance.ui.theme.SigmaFinanceTheme
import com.example.sigmafinance.ui.theme.accentGrey
import com.example.sigmafinance.ui.theme.balanceGreen
import com.example.sigmafinance.ui.theme.balanceRed
import com.example.sigmafinance.ui.theme.customText
import com.example.sigmafinance.ui.theme.customTitle
import com.example.sigmafinance.ui.theme.dialogHeader
import com.example.sigmafinance.ui.theme.periwinkle
import com.example.sigmafinance.ui.theme.richBlack
import com.example.sigmafinance.ui.theme.standardText
import com.example.sigmafinance.viewmodel.ViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.TreeMap

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
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun MainScreen(navController: NavHostController, viewModel: ViewModel) {
    val coroutineScope = rememberCoroutineScope()
    //basic functionality vital information
    val currentAmountOfMoney by viewModel.getMoneyValue().collectAsState(initial = 0.0f)
    var currentDate by remember { viewModel.currentDate }
    var listOfDays by remember { viewModel.listOfDays }
    var selectedEventsLists by remember { mutableStateOf(emptyList<TemporaryLists.DemonstrationEvent>()) }
    val lastLogin by viewModel.getLastLogin().collectAsState(initial = LocalDate.now())
    //ui
    var newDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedNetIncome by remember { mutableFloatStateOf(0f) }
    var selectedDate by remember { mutableStateOf("${currentDate.dayOfMonth}") }
    var selectedEvent by remember { mutableStateOf<TemporaryLists.DemonstrationEvent?>(null) }
    val daysOfWeek = DayOfWeek.entries
    var toggleAddEventDialog by remember { mutableStateOf(false) }
    var toggleEnterNewValueDialog by remember { mutableStateOf(false) }
    //triggers
    var updateListsTrigger by remember { mutableLongStateOf(0L) }
    var updateYearListsTrigger by remember { mutableLongStateOf(0L) }
    var updateYearListsKey by remember { mutableIntStateOf(0) }
    //lists
    val events: List<DBType.FundsEvent> by viewModel.fundsEvents.observeAsState(emptyList())
    val recurringEvents: List<DBType.FundsEventRecurring> by viewModel.fundsEventsRecurring.observeAsState(emptyList())
    var currentMonthEvents by remember { mutableStateOf(emptyList<TemporaryLists.DemonstrationEvent>()) }
    val currentYearEvents by viewModel.currentYearEvents.observeAsState(emptyList())
/*    val eventTree: TreeMap<LocalDate, TemporaryLists.DemonstrationEvent> =
        TreeMap(currentYearEvents.associateBy { it.date })*/
    val eventTree = remember {
        mutableStateOf(TreeMap(currentYearEvents.associateBy { it.date }))
    }

    LaunchedEffect(updateYearListsTrigger) {
        Log.d("Main Activity", "updateYearListsTrigger triggered, newDate is $newDate")
        viewModel.updateYearlyLists(updateYearListsKey, newDate)
    }
    fun incrementDate() {
        newDate = currentDate.plusMonths(1)
        if (newDate.year > currentDate.year) {
            Log.d("incrementDate", "incrementDate caused update yearly lists")
            updateYearListsKey = 2
            updateYearListsTrigger = System.currentTimeMillis()

        }
        currentDate = newDate
        listOfDays = getDaysInMonth(currentDate.year, currentDate.monthValue)
        selectedDate = "1"
        updateListsTrigger = System.currentTimeMillis()
        selectedEventsLists = currentMonthEvents.filter { it.date.dayOfMonth.toString() == selectedDate }
        selectedNetIncome = selectedEventsLists.sumOf { it.amount.toDouble() }.toFloat()
    }

    fun updateCurrentMonthEvents(targetMonth: YearMonth) {
        val startOfMonth = targetMonth.atDay(1)
        val endOfMonth = targetMonth.atEndOfMonth()
        eventTree.value = TreeMap(currentYearEvents.associateBy { it.date })
        currentMonthEvents = eventTree.value.subMap(startOfMonth, true, endOfMonth, true).values.toList()
    }
    fun decrementDate() {
        newDate = currentDate.minusMonths(1)
        if (newDate.year < currentDate.year) {
            Log.d("DecrementDate", "DecrementDate caused update yearly lists")
            updateYearListsKey = 1
            updateYearListsTrigger = System.currentTimeMillis()
        }
        currentDate = newDate
        listOfDays = getDaysInMonth(currentDate.year, currentDate.monthValue)
        selectedDate = "1"
        updateListsTrigger = System.currentTimeMillis()
        selectedEventsLists = currentMonthEvents.filter { it.date.dayOfMonth.toString() == selectedDate }
        selectedNetIncome = selectedEventsLists.sumOf { it.amount.toDouble() }.toFloat()
    }
    LaunchedEffect(updateListsTrigger, events, recurringEvents, currentYearEvents) {
            updateCurrentMonthEvents(YearMonth.of(currentDate.year, currentDate.month))
    }
    LaunchedEffect(events, recurringEvents) {
        updateYearListsKey = 0
        updateYearListsTrigger = System.currentTimeMillis()
    }

    LaunchedEffect(Unit) {
        if (lastLogin != LocalDate.now()) {
            viewModel.saveMoneyValue(
                viewModel.getOccurrencesBetweenLastAndCurrentLogin(
                    lastLogin,
                    LocalDate.now(), 0
                ) as Float + currentAmountOfMoney
            )
            Log.d(
                "Login date check",
                "Last login wasn't today, new money value has to be calculated"
            )
            viewModel.saveLastLogin(LocalDate.now())
        }
    }

    Scaffold(
        floatingActionButton = {
            Column (verticalArrangement = Arrangement.spacedBy(8.dp) ){
                ExtendedFloatingActionButton(modifier = Modifier.padding(PaddingValues(0.dp)) ,
                    onClick = { navController.navigate("MoneyProjection/$currentAmountOfMoney") },
                    content = {
                        Box(
                            modifier = Modifier
                                .size(40.dp), contentAlignment = Alignment.Center
                        ) {
                            GraphImage()
                        }
                    },
                    containerColor = accentGrey,
                )
                ExtendedFloatingActionButton(
                    onClick = { toggleAddEventDialog = true },
                    content = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add button",
                            tint = Color.White
                        )
                    },
                    containerColor = periwinkle
                )
            }
        },
        bottomBar = {
            Card(modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(1f)){
                Row(horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add event",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.DateRange,
                            contentDescription = "Money projection",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { }) {
                        GraphImage()
                    }
                    IconButton(onClick = { /* Add your click logic here */ }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardDoubleArrowRight,
                            contentDescription = "Next Arrow"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize(1f)
                .background(richBlack)
        ) {
            Column(
                modifier = Modifier
                    .background(color = richBlack)
                    .padding(innerPadding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(vertical = 24.dp)
                        .clickable {
                            toggleEnterNewValueDialog = true
                        },
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Top
                ) {
                Text(text = "$currentAmountOfMoney UAH", fontSize = 22.sp, color = periwinkle)
            }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { decrementDate() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Go to previous month",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "${currentDate.month} ${currentDate.year} ",
                        fontWeight = FontWeight.Bold,
                        style = customText,
                        color = Color.White
                    )
                    IconButton(onClick = { incrementDate() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Go to next month",
                            tint = Color.White
                        )
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
                            style = standardText,
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
                            animationSpec = tween(durationMillis = 500),
                            label = "Switching from white to purple if selected"
                        ).value
                        val netIncome = currentMonthEvents.filter { it.date.dayOfMonth == day.dayOfMonth }.sumOf { it.amount.toDouble() }.toFloat()
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = dayNumber,
                                    fontSize = 14.sp,
                                    color = dayColor,
                                    style = standardText,
                                    modifier = Modifier
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }) {
                                            selectedDate = dayNumber
                                            selectedEventsLists = currentMonthEvents.filter { it.date.dayOfMonth.toString() == dayNumber }
                                            selectedNetIncome = netIncome
                                        })
                                    Box(
                                        modifier = Modifier
                                            .size(width = 25.dp, height = 3.dp)
                                            .background(
                                                color = if (netIncome > 0) {
                                                    balanceGreen
                                                } else if (netIncome < 0) {
                                                    balanceRed
                                                } else {
                                                    Color.Transparent
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
                Column {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(vertical = 6.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total: $selectedNetIncome",
                                    style = standardText,
                                    color = if (selectedNetIncome > 0) {
                                        balanceGreen
                                    } else {
                                        balanceRed
                                    }
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(vertical = 6.dp)
                            )
                        }
                    }
                    if (selectedEventsLists.isNotEmpty()) {
                        Card (modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(1f)){
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(selectedEventsLists) { event ->
                                    //Events for days display
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(periwinkle.copy(alpha = 0.4f)),
                                        modifier = Modifier
                                            .combinedClickable(onClick = {},
                                                onLongClick = { selectedEvent = event })
                                            .padding(vertical = 16.dp, horizontal = 8.dp)
                                            .border(
                                                BorderStroke(2.dp, color = periwinkle),
                                                shape = RoundedCornerShape(16.dp)
                                            ),
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        start = 12.dp,
                                                        end = 12.dp,
                                                        top = 16.dp
                                                    ),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            )
                                            {
                                                Text(
                                                    text = event.name,
                                                    style = standardText,
                                                )
                                                Text(
                                                    text = event.amount.toString(),
                                                    style = standardText,
                                                    color = if (event.amount > 0) {
                                                        balanceGreen
                                                    } else {
                                                        balanceRed
                                                    }
                                                )
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 6.dp, horizontal = 12.dp)
                                            )
                                            {
                                                Text(
                                                    text = "Type: ${event.type}",
                                                    style = standardText,
                                                    fontSize = 14.sp,
                                                    color = if (event.amount > 0) {
                                                        balanceGreen
                                                    } else {
                                                        balanceRed
                                                    }
                                                )

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (toggleAddEventDialog) {
                        AddFundsEvent(
                            onConfirm = { eventName: String, eventAmount: String, repeatInterval: Int, repeatUnit: String, endCondition: String, endAfterOccurrences: Int?, endDate: LocalDate? ->
                                if (repeatUnit == "Don't repeat") {
                                    val newEvent = DBType.FundsEvent(
                                        name = eventName,
                                        amount = eventAmount.toFloat(),
                                        date = LocalDate.of(
                                            currentDate.year,
                                            currentDate.month,
                                            selectedDate.toInt()
                                        )
                                    )
                                    viewModel.insertEvent(newEvent)
                                } else {
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
                                        endDate = if (endCondition != "Never") {
                                            calculateEndDate(
                                                startDate = LocalDate.of(
                                                    currentDate.year,
                                                    currentDate.month,
                                                    selectedDate.toInt()
                                                ),
                                                repeatInterval = repeatInterval,
                                                repeatUnit = repeatUnit,
                                                endCondition = endCondition,
                                                endAfterOccurrences = endAfterOccurrences,
                                                endDate = endDate
                                            )
                                        } else {
                                            null
                                        }
                                    )
                                    viewModel.insertRecurringEvent(newEvent)
                                }
                            }, onDismiss = { toggleAddEventDialog = false })
                    }
                    selectedEvent?.let {
                        EditEvent(
                            event = selectedEvent!!,
                            onDismiss = { selectedEvent = null },
                            currentDate = currentDate,
                            selectedDate = selectedDate.toInt(),
                            viewModel = viewModel
                        )
                    }
                    if (toggleEnterNewValueDialog) {
                        EnterNewMoneyValueDialog(
                            previousValue = currentAmountOfMoney,
                            onDismiss = { toggleEnterNewValueDialog = false },
                            onConfirm = { newValue: Float ->
                                coroutineScope.launch {
                                    viewModel.saveMoneyValue(newValue)
                                }
                            })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectionScreen(navController: NavHostController, viewModel: ViewModel, baseAmount: Float){
    //toggles
    var toggleDateByAmountDialog by remember { mutableStateOf(false) }
    var toggleAmountByDate by remember { mutableStateOf(false) }
    //search parameters
    var selectedAmount by remember  { mutableFloatStateOf(0f) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    //search results
    var foundDate by remember { mutableStateOf<LocalDate?>(null) }
    var foundAmount by remember { mutableFloatStateOf(0f) }
        Scaffold(
            containerColor = richBlack,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(top = 40.dp)
                        .padding(horizontal = 12.dp)
                        .background(richBlack)
                ) {
                    Text("Money Projection", style = customTitle)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Here you can find out when you will have certain sum of money, or how much you will have at certain date.",
                        style = standardText, color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { toggleAmountByDate = true }, colors = customButtonColors()) {
                        Text("Find amount by date", style = standardText)
                    }
                    Button(
                        onClick = { toggleDateByAmountDialog = true },
                        colors = customButtonColors()
                    ) {
                        Text("Find date by amount", style = standardText)
                    }
                }
            }, modifier = Modifier
                .background(richBlack)
                .fillMaxSize(1f)
        ) { innerPadding ->
            BackHandler {
                navController.navigate("main")
            }
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp)
                    .fillMaxSize(1f)
                    .background(richBlack), contentAlignment = Alignment.Center
            ) {
                Column(Modifier.padding(top = 0.dp)) {
                    foundAmount.let { amount ->
                        if (amount != 0f) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .padding(vertical = 6.dp)
                            )
                            Text(
                                text = "By the date of $selectedDate, you will have $foundAmount UAH",
                                style = customTitle,
                                fontSize = 32.sp,
                                lineHeight = 48.sp
                            )
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .padding(vertical = 6.dp)
                            )
                        }
                    }
                    foundDate?.let { date ->
                        if (date >= LocalDate.now()) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .padding(vertical = 6.dp)
                            )
                            Text(
                                text = "$selectedAmount UAH you searched will most likely happen around $foundDate",
                                style = customTitle,
                                fontSize = 32.sp,
                                lineHeight = 48.sp
                            )
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .padding(vertical = 6.dp)
                            )
                        }
                    }
                }
                if (toggleAmountByDate) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { toggleAmountByDate = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val millis = datePickerState.selectedDateMillis
                                if (millis != null) {
                                    selectedDate = Instant.ofEpochMilli(millis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                }
                                if (selectedDate!! >= LocalDate.now()) {
                                    val result = viewModel.moneyProjection(
                                        baseAmount = baseAmount,
                                        targetAmount = 0f,
                                        goalDate = selectedDate,
                                        startDate = LocalDate.now()
                                    )
                                    val (floatValue, _) = result
                                    if (floatValue != null) {
                                        foundAmount = floatValue
                                        foundDate = null
                                        toggleAmountByDate = false
                                    }
                                }
                            }) {
                                Text("Ok")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                if (toggleDateByAmountDialog) {
                    var temporaryValue by remember { mutableStateOf("") }
                    fun validateInput(amount: String): Boolean {
                        return amount.toFloatOrNull() != null
                    }
                    Dialog(onDismissRequest = {
                        toggleDateByAmountDialog = !toggleDateByAmountDialog
                    }) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = richBlack
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Enter amount of money to search for",
                                    color = Purple40,
                                    style = dialogHeader
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                CustomTextField(
                                    value = temporaryValue,
                                    onValueChange = { temporaryValue = it },
                                    label = "Desired amount of money",
                                    modifier = Modifier.fillMaxWidth(1f)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(onClick = {
                                        toggleDateByAmountDialog = !toggleDateByAmountDialog
                                    }, colors = customButtonColors()) {
                                        Text("Cancel")
                                    }
                                    Button(
                                        onClick = {
                                            selectedAmount = temporaryValue.toFloat()
                                            val result = viewModel.moneyProjection(
                                                baseAmount = baseAmount,
                                                targetAmount = selectedAmount,
                                                goalDate = null,
                                                startDate = LocalDate.now()
                                            )
                                            Log.d(
                                                "Money Projection",
                                                "Selected amount is $selectedAmount"
                                            )
                                            val (_, dateValue) = result
                                            if (dateValue != null) {
                                                Log.d(
                                                    "Money Projection",
                                                    "Date value is $dateValue"
                                                )
                                                foundAmount = 0f
                                                foundDate = dateValue
                                            }
                                            toggleDateByAmountDialog = !toggleDateByAmountDialog
                                        },
                                        colors = customButtonColors(),
                                        enabled = validateInput(temporaryValue)
                                    ) {
                                        Text("Confirm")
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
}
