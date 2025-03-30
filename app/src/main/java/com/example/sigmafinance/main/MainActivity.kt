package com.example.sigmafinance.main

import android.annotation.SuppressLint
import android.icu.text.Collator.getDisplayName
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChartOutlined
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.LineAxis
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.sigmafinance.R
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
import com.example.sigmafinance.ui.theme.montserratFontFamily
import com.example.sigmafinance.ui.theme.periwinkle
import com.example.sigmafinance.ui.theme.projectionHighlight
import com.example.sigmafinance.ui.theme.richBlack
import com.example.sigmafinance.ui.theme.standardText
import com.example.sigmafinance.viewmodel.ViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
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
        bottomBar = {
/*            Card(modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(1f)
                .height(60.dp)){*/
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 48.dp)
                        .fillMaxWidth(1f)
                        .height(80.dp)
                        .paint(
                            painter = painterResource(id = R.drawable.gradient1),
                            contentScale = ContentScale.FillBounds
                        ),

                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.matchParentSize()) {
                        IconButton(onClick = { toggleAddEventDialog = true }) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add event",
                                tint = Color.White,
                                modifier = Modifier.scale(1.3f)
                            )
                        }
                        IconButton(onClick = { navController.navigate("MoneyProjection/$currentAmountOfMoney")}) {
                            Icon(
                                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                                tint = Color.White,
                                contentDescription = "Money projection",
                                modifier = Modifier.scale(1.3f)
                            )
                        }
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Filled.InsertChartOutlined,
                                contentDescription = "Analytics",
                                tint = Color.White,
                                modifier = Modifier.scale(1.3f)
                            )
                        }
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Filled.DateRange,
                                contentDescription = "Budget",
                                tint = Color.White,
                                modifier = Modifier.scale(1.3f)
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
                Text(text = "$currentAmountOfMoney UAH", fontSize = 22.sp, color = periwinkle, style = customTitle)
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
                            tint = Color.White,
                            modifier = Modifier.scale(1.5f)
                        )
                    }
                    Text(
                        text = "${currentDate.month} ${currentDate.year} ",
                        style = customText,
                        fontFamily = montserratFontFamily,
                        color = Color.White
                    )
                    IconButton(onClick = { incrementDate() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Go to next month",
                            tint = Color.White,
                            modifier = Modifier.scale(1.5f)
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
                Column (modifier = Modifier.padding(vertical = 16.dp)){
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
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(color = Color.White)) {
                                            append("Total: ")
                                        }
                                        withStyle(
                                            style = SpanStyle(
                                                color = if (selectedNetIncome > 0) balanceGreen else balanceRed
                                            )
                                        ) {
                                            append("$selectedNetIncome")
                                        }
                                    },
                                    style = standardText
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
                            .fillMaxWidth(1f)
                            .fillMaxHeight(0.9f),
                            shape = RoundedCornerShape(16.dp
                            )){
                            LazyColumn(modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)) {
                                items(selectedEventsLists) { event ->
                                   GradientItemBackground {
                                       Row(
                                           modifier = Modifier
                                               .fillMaxWidth()
                                               .height(32.dp)
                                               .padding(
                                                   start = 12.dp,
                                                   end = 12.dp,
                                               ),
                                           horizontalArrangement = Arrangement.SpaceBetween,
                                           verticalAlignment = Alignment.CenterVertically
                                       )
                                       {
                                           Text(
                                               text = event.name,
                                               style = standardText,
                                           )
                                           Text(
                                               text = "${event.type}",
                                               style = standardText,
                                               color = Color.White
                                           )
                                           Text(
                                               text = event.amount.toString() + " UAH",
                                               style = standardText,
                                               color = Color.White
                                           )

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
                Box(modifier = Modifier
                    .fillMaxWidth(1f)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF9F2BB1),
                                Color(0xFFEBA4FF)
                            )
                        )
                    ), contentAlignment = Alignment.CenterStart) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(vertical = 19.dp)
                            .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Absolute.SpaceAround
                    ) {
                        Text("Money Projection", style = customTitle, color = Color.White, modifier = Modifier.fillMaxWidth(0.9f))
                        IconButton(onClick = { navController.navigate("main")}) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                tint = Color.White,
                                contentDescription = "Money projection",
                                modifier = Modifier.scale(1.3f)
                            )
                        }
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
                    .fillMaxSize(1f)
                    .background(richBlack), contentAlignment = Alignment.TopCenter
            ) {
                Column(Modifier.padding(top = 17.dp)) {
                    GreyScaleCard {
                        Column {
                            Text(
                                "Here you can find out when you will have certain sum of money, or how much you will have at certain date.",
                                style = standardText, color = Color.White, fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(1f)){
                                Button(onClick = { toggleAmountByDate = true },
                                    colors = customButtonColors(),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp),
                                    modifier = Modifier
                                        .width(140.dp)
                                        .height(37.dp),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 8.dp,
                                        pressedElevation = 12.dp,
                                        disabledElevation = 0.dp
                                    )
                                    ) {
                                Text("Amount by date", style = standardText, fontSize = 12.sp)
                            }
                                Button(
                                    onClick = { toggleDateByAmountDialog = true },
                                    colors = customButtonColors(),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp),
                                    modifier = Modifier
                                        .width(140.dp)
                                        .height(37.dp)
                                ) {
                                    Text("Date by amount", style = standardText, fontSize = 12.sp)
                                }
                            }

                        }
                    }

                    foundAmount.let { amount ->
                        if (amount != 0f) {
                                Column(modifier = Modifier
                                    .padding(start = 0.dp)) {
                                    Text(text = "Projection:", style = customTitle, color = Color.White,
                                        modifier = Modifier
                                            .padding(start = 11.dp)
                                            .padding(vertical = 9.dp), fontWeight = FontWeight.Light)
                                    GreyScaleCard() {
                                        Column() {
                                            Text(
                                                text = "You searched for how much you will have around",
                                                style = customText,
                                                fontSize = 16.sp,
                                                lineHeight = 24.sp
                                            )
                                            Text(
                                                text = selectedDate?.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")) ?: "No date selected",
                                                style = projectionHighlight
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                    GreyScaleCard() {
                                        Column() {
                                            Text(
                                                text = "Which means, in ${
                                                    viewModel.currentDate.value.until(
                                                        selectedDate,
                                                        ChronoUnit.DAYS
                                                    )
                                                } days, you will have",
                                                style = customText,
                                                fontSize = 16.sp,
                                                lineHeight = 24.sp
                                            )
                                            Text(
                                                text = "$amount UAH",
                                                style = projectionHighlight
                                            )
                                        }
                                    }
                                }
                        }
                        foundDate?.let { date ->
                            if (date >= LocalDate.now()) {
                                Column {
                                    Text(
                                        text = "Projection:",
                                        style = customTitle,
                                        color = Color.White,
                                        modifier = Modifier
                                            .padding(start = 11.dp)
                                            .padding(vertical = 9.dp),
                                        fontWeight = FontWeight.Light
                                    )
                                    GreyScaleCard() {
                                        Column() {
                                            Text(
                                                text = "You searched for",
                                                style = customText,
                                                fontSize = 16.sp,
                                                lineHeight = 24.sp
                                            )
                                            Text(
                                                text = date.toString(),
                                                style = projectionHighlight
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                    GreyScaleCard() {
                                        Column() {
                                            Text(
                                                text = "It will most likely happen around",
                                                style = customText,
                                                fontSize = 16.sp,
                                                lineHeight = 24.sp
                                            )
                                            Text(
                                                text = date.toString(),
                                                style = projectionHighlight
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                    GreyScaleCard() {
                                        Column() {
                                            Text(
                                                text = "Which is...",
                                                style = customText,
                                                fontSize = 16.sp,
                                                lineHeight = 24.sp
                                            )
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(style = SpanStyle(color = periwinkle)) {
                                                        append(
                                                            "${
                                                                viewModel.currentDate.value.until(
                                                                    date,
                                                                    ChronoUnit.DAYS
                                                                )
                                                            }"
                                                        )
                                                    }
                                                    withStyle(style = SpanStyle(color = Color.White)) {
                                                        append(" days\n or ")
                                                    }
                                                    withStyle(style = SpanStyle(color = periwinkle)) {
                                                        append(
                                                            "${
                                                                viewModel.currentDate.value.until(
                                                                    date,
                                                                    ChronoUnit.WEEKS
                                                                )
                                                            }"
                                                        )
                                                    }
                                                    withStyle(style = SpanStyle(color = Color.White)) {
                                                        append(" weeks\n or ")
                                                    }
                                                    withStyle(style = SpanStyle(color = periwinkle)) {
                                                        append(
                                                            "${
                                                                viewModel.currentDate.value.until(
                                                                    date,
                                                                    ChronoUnit.MONTHS
                                                                )
                                                            }"
                                                        )
                                                    }
                                                    withStyle(style = SpanStyle(color = Color.White)) {
                                                        append(" months\n or ")
                                                    }
                                                    withStyle(style = SpanStyle(color = periwinkle)) {
                                                        append(
                                                            "${
                                                                viewModel.currentDate.value.until(
                                                                    date,
                                                                    ChronoUnit.YEARS
                                                                )
                                                            }"
                                                        )
                                                    }
                                                    withStyle(style = SpanStyle(color = Color.White)) {
                                                        append(" years\n ...away from now")
                                                    }
                                                },
                                                style = standardText,
                                                fontSize = 16.sp,
                                                lineHeight = 24.sp
                                            )
                                        }
                                    }
                                }
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
}

@Composable
fun AnalyticsScren(navController: NavHostController, viewModel: ViewModel){
    var selectedPeriod by remember {
        mutableStateOf("Months")
    }
    var currentDate by remember { viewModel.currentDate }
    fun decrementDate() {
        currentDate = currentDate.minusMonths(1)
    }
    fun incrementDate() {
        currentDate = currentDate.plusMonths(1)
    }
    LaunchedEffect (currentDate) {

    }
    Scaffold(containerColor = colorScheme.primaryContainer,
        topBar = {
            Box(modifier = Modifier
                .fillMaxWidth(1f)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF9F2BB1),
                            Color(0xFFEBA4FF)
                        )
                    )
                ), contentAlignment = Alignment.CenterStart) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(vertical = 19.dp)
                        .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.SpaceAround
                ) {
                    Text("Money Projection", style = customTitle, color = Color.White, modifier = Modifier.fillMaxWidth(0.9f))
                    IconButton(onClick = { navController.navigate("main")}) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            tint = Color.White,
                            contentDescription = "Analytics",
                            modifier = Modifier.scale(1.3f)
                        )
                    }
                }
            }
        }, modifier = Modifier
            .background(colorScheme.primaryContainer,)
            .fillMaxSize(1f)
        ) { innerPadding ->
        BackHandler {
            navController.navigate("main")
        }
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(1f)
                .background(colorScheme.primaryContainer,), contentAlignment = Alignment.TopCenter
        ) {
            Column(Modifier.padding(top = 17.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Months",
                            style = customText,
                            fontFamily = montserratFontFamily,
                            color = if (selectedPeriod == "Months") {
                                periwinkle} else {Color.White},
                            modifier = Modifier.clickable { selectedPeriod = "Months"}
                        )
                        VerticalDivider(thickness = 2.dp)
                        Text(
                            text = "Years",
                            style = customText,
                            fontFamily = montserratFontFamily,
                            color = if (selectedPeriod == "Years") {
                                periwinkle} else {Color.White},
                            modifier = Modifier.clickable {  selectedPeriod = "Years"}
                        )
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
                            tint = Color.White,
                            modifier = Modifier.scale(1.5f)
                        )
                    }
                    Text(
                        text = "${currentDate.month} ${currentDate.year} ",
                        style = customText,
                        fontFamily = montserratFontFamily,
                        color = Color.White
                    )
                    IconButton(onClick = { incrementDate() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Go to next month",
                            tint = Color.White,
                            modifier = Modifier.scale(1.5f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center
                ) {
                  /*  items() { day ->
                    }*/
                }
                }

            }

    }
}
