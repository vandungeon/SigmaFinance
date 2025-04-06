package com.example.sigmafinance.main
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection.Companion.Content
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.sigmafinance.R
import com.example.sigmafinance.database.TemporaryLists
import com.example.sigmafinance.ui.theme.Purple40
import com.example.sigmafinance.ui.theme.balanceGreen
import com.example.sigmafinance.ui.theme.balanceRed
import com.example.sigmafinance.ui.theme.customText
import com.example.sigmafinance.ui.theme.customTitle
import com.example.sigmafinance.ui.theme.dialogHeader
import com.example.sigmafinance.ui.theme.jordyBlue
import com.example.sigmafinance.ui.theme.montserratFontFamily
import com.example.sigmafinance.ui.theme.periwinkle
import com.example.sigmafinance.ui.theme.richBlack
import com.example.sigmafinance.ui.theme.standardText
import com.example.sigmafinance.viewmodel.ViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
enum class MonthName {
    JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE,
    JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER;

    fun displayName(): String = name.lowercase().replaceFirstChar { it.uppercase() }
}
@Composable
fun customTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
            focusedTextColor = jordyBlue,
            focusedIndicatorColor = jordyBlue,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = jordyBlue.copy(alpha = 0.1f),
            unfocusedContainerColor = Color.Transparent

    )
}

@Composable
fun customButtonColors(): ButtonColors {
    return ButtonDefaults.buttonColors(
        containerColor = periwinkle,
        contentColor = Color.White,
        disabledContainerColor = Color.Gray.copy(alpha = 0.2f),
        disabledContentColor = Color.White.copy(alpha = 0.1f)
    )
}
//@Composable
//fun GraphImage() {
//    Image(
//        painter = painterResource(id = R.drawable.graph1),
//        contentDescription = "Graph icon",
//        contentScale = ContentScale.Fit,
//
//    )
//}
@Composable
fun GraphImage() {
    Image(
        painter = painterResource(id = R.drawable.graph2),
        contentDescription = "Graph icon",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize(0.7f)
            .scale(1.5f)
        )
}
/*@Composable
fun CustomButton(modifier: Modifier = Modifier,
                 content: @Composable () -> Unit, onClick: () -> Unit){
    Button(
        onClick = onClick,
        modifier = Modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFBA57D5)
        ),
*//*        shape = RoundedCornerShape(8.dp),*//*
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF9F2BB1),
                            Color(0xFFEBA4FF)
                        )
                    )
                )
               .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            content()
        }
    }
}*/
@Composable
fun GradientItemBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF9F2BB1),
                        Color.Transparent
                    )
                )
            )
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                drawLine(
                    color = Color.White,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = strokeWidth
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        content()
    }
}
@Composable
fun GreyScaleCard(modifier: Modifier = Modifier,
                  content: @Composable () -> Unit){
    Card (modifier = Modifier
        .fillMaxWidth(1f)
        .padding(horizontal = 11.dp),
        shape = RoundedCornerShape(30.dp)
    ) {
        Box(modifier = modifier
            .padding(horizontal = 23.dp)
            .padding(vertical = 15.dp)){
            content()
        }
    }
}


@Composable
fun AnalyticsGraph(income: Float, expenses: Float, onclick: () -> Unit){

    Row(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .padding(0.dp)
            .clickable { onclick() },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val columnHeight = 100f
        var leftColumnHeight = columnHeight
        var rightColumnHeight = columnHeight
        if ((income + expenses) < 0.1f){
            leftColumnHeight = 0f
            rightColumnHeight = 0f}
        else {
        if (income < 0.1f) { leftColumnHeight = 0f
        } else if (expenses < 0.1f) { rightColumnHeight = 0f
        }
            if (income > expenses) {
                rightColumnHeight = (expenses / income) * columnHeight
            } else {
                leftColumnHeight = (income / expenses) * columnHeight
            }

    }
        //Income
        Box(
            modifier = Modifier
                .weight(1f)
                .height((leftColumnHeight).dp) // Different height
                .background(balanceGreen) // First color
        )
        //Expenses
        Box(
            modifier = Modifier
                .weight(1f)
                .height((rightColumnHeight).dp) // Different height
                .background(balanceRed) // Second color
        )
    }
}

@Composable
fun AnalyticsGraphYearly(monthlyData: List<Pair<Float, Float>>) {
    val totalIncome = monthlyData.sumOf { it.first.toDouble() }.toFloat()
    val totalExpenses = monthlyData.sumOf { it.second.toDouble() }.toFloat()
    val monthlyResultsIncome = Array(12) { 0f }.toMutableList()
    val monthlyResultsExpenses = Array(12) { 0f }.toMutableList()

    monthlyData.forEachIndexed { index, (income, expenses) ->
        monthlyResultsIncome[index] = income
        monthlyResultsExpenses[index] = expenses
    }
    val incomeColors = listOf(
        Color(0xFF4CAF50), Color(0xFF66BB6A), Color(0xFF81C784), Color(0xFF9CCC65),
        Color(0xFFB2FF59), Color(0xFFC6FF00), Color(0xFFEEFF41), Color(0xFFFFF176),
        Color(0xFFFFF9C4), Color(0xFFE6EE9C), Color(0xFF66BB6A), Color(0xFF9CCC65)
    )
    val expenseColors = listOf(
        Color(0xFFF44336), Color(0xFFE57373), Color(0xFFEF9A9A), Color(0xFFFF8A80),
        Color(0xFFFF5252), Color(0xFFFF1744), Color(0xFFD81B60), Color(0xFFC2185B),
        Color(0xFFAD1457), Color(0xFFF06292), Color(0xFFF48FB1), Color(0xFFF8BBD0)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between columns
    ) {
        Column(
            modifier = Modifier
                .weight(1f) // Equal width for both columns
                .clip(RoundedCornerShape(20.dp))
                .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(20.dp))
                .fillMaxHeight(0.9f)
        ) {
            monthlyResultsIncome.forEachIndexed { index, item ->
                val incomePercent =
                    if (totalIncome > 0.1f && item > 0.1f) (item / totalIncome) else 0.083f
                if (incomePercent > 0.0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(incomePercent)
                            .widthIn(min = 80.dp) // Wide enough for text
                            .background(incomeColors[index]),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${item} ${
                                MonthName.entries[index].displayName().substring(0, 3)
                            }",
                            style = standardText,
                            color = Color.Black
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .weight(1f) // Equal width for both columns
                .clip(RoundedCornerShape(20.dp))
                .fillMaxHeight(0.9f)
                .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(20.dp))
        ) {
            monthlyResultsExpenses.forEachIndexed { index, item ->
                val expensePercent =
                    if (totalExpenses > 0.1f && item > 0.1f) (item / totalExpenses) else 0.0833f
                if (expensePercent > 0.0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(expensePercent)
                            .widthIn(min = 80.dp) // Wide enough for text
                            .background(expenseColors[index]),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${item} ${
                                MonthName.entries[index].displayName().substring(0, 3)
                            }",
                            style = standardText,
                        )
                    }
                }
            }
        }
    }

}
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    labelColor: Color = periwinkle,
    placeholder: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    colors: TextFieldColors = customTextFieldColors(),
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label?.let { { Text(text = it, fontFamily = montserratFontFamily, color = labelColor) } },
        placeholder = placeholder?.let { { Text(text = it, fontFamily = montserratFontFamily, color = periwinkle) } },
        isError = isError,
        enabled = enabled,
        singleLine = singleLine,
        colors = colors,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        readOnly = readOnly
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundsEvent(
    onConfirm: (eventName: String, eventAmount: String, repeatInterval: Int, repeatUnit: String, endCondition: String, endAfterOccurrences: Int?, endDate: LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    var eventName by remember { mutableStateOf("") }
    var eventAmount by remember { mutableStateOf("") }
    var repeatInterval by remember { mutableStateOf("1") }
    var repeatUnit by remember { mutableStateOf("Don't repeat") }
    var endCondition by remember { mutableStateOf("Never") }
    var endAfterOccurrences by remember { mutableStateOf("0") }
    var endDateInput by remember { mutableStateOf("") }
    var isDateValid by remember { mutableStateOf(true) }

    val repeatUnits = listOf("Don't repeat","Days", "Weeks", "Months", "Years")
    val endConditions = listOf("Never", "After N times", "Until date")
    var expandedUnit by remember { mutableStateOf(false) }
    var expandedEndCondition by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun validateInput(name: String, amount: String): Boolean {
        val isNameValid = name.isNotEmpty()
        val isAmountValid = amount.isNotEmpty() && amount.matches(Regex("^-?\\d+\\.?\\d+$")) && amount.toFloatOrNull() != null
        val isEndConditionValid = when (endCondition) {
            "Until date" -> endDateInput.isNotEmpty() && isDateValid
            "After N times" -> endAfterOccurrences.isNotEmpty() && endAfterOccurrences.toIntOrNull() != null && endAfterOccurrences.toInt() > 0
            else -> true
        }
        return isNameValid && isAmountValid && isEndConditionValid
    }
    fun validateDate(input: String): Boolean {
        return try {
            LocalDate.parse(input, dateFormatter)
            true
        } catch (e: Exception) {
            false
        }
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = richBlack
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("New Event", color = Purple40, style = dialogHeader)
                Spacer(modifier = Modifier.height(8.dp))
                CustomTextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = "Title",
                    modifier = Modifier.fillMaxWidth(1f)
                )
                CustomTextField(
                    value = eventAmount,
                    onValueChange = { eventAmount = it},
                    label = "Amount",
                    modifier = Modifier.fillMaxWidth(1f)
                )
                ExposedDropdownMenuBox(
                    expanded = expandedUnit,
                    onExpandedChange = { expandedUnit = it }
                ) {
                    CustomTextField(
                        value = repeatUnit,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit)
                        },
                        label = "Repeat",
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(1f)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedUnit,
                        onDismissRequest = { expandedUnit = false }
                    ) {
                        repeatUnits.forEach { unit ->
                            DropdownMenuItem(
                                onClick = {
                                    repeatUnit = unit
                                    expandedUnit = false
                                },
                                text = { Text(unit) }
                            )
                        }
                    }
                }
                if(repeatUnit != "Don't repeat") {
                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Every", color = Color.White)
                        CustomTextField(
                            value = repeatInterval.toString(),
                            onValueChange = { repeatInterval = it },
                            modifier = Modifier
                                .width(110.dp)
                                .padding(horizontal = 16.dp)
                        )
                        Text(repeatUnit, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedEndCondition,
                        onExpandedChange = { expandedEndCondition = it }
                    ) {
                        CustomTextField(
                            value = endCondition,
                            onValueChange = {},
                            readOnly = true,
                            label = "End condition",
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEndCondition)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedEndCondition,
                            onDismissRequest = { expandedEndCondition = false }
                        ) {
                            endConditions.forEach { condition ->
                                DropdownMenuItem(
                                    onClick = {
                                        endCondition = condition
                                        expandedEndCondition = false
                                    },
                                    text = { Text(condition) }
                                )
                            }
                        }
                    }
                    when (endCondition) {
                        "After N times" -> {
                            CustomTextField(
                                value = endAfterOccurrences,
                                onValueChange = {
                                    endAfterOccurrences = it},
                                label = "Occurrences",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        "Until date" -> {
                            CustomTextField(
                                value = endDateInput,
                                onValueChange = {
                                    endDateInput = it
                                    isDateValid = validateDate(it)
                                },
                                label = "End Date (yyyy-MM-dd)",
                                labelColor = if (isDateValid) Purple40 else Color.Red,
                                modifier = Modifier.fillMaxWidth(),
                                isError = !isDateValid
                            )
                            if (!isDateValid) {
                                Text("Invalid date format", color = Color.Red)
                            }
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { onDismiss() }, colors = customButtonColors()) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        val endDate = if (endCondition == "Until date" && isDateValid) {
                            LocalDate.parse(endDateInput, dateFormatter)
                        } else {
                            null
                        }
                        onConfirm(eventName, eventAmount, repeatInterval.toInt(), repeatUnit, endCondition, endAfterOccurrences.toInt(), endDate)
                        onDismiss()
                    }, colors = customButtonColors(), enabled = validateInput(eventName, eventAmount)) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEvent(
    event: TemporaryLists.DemonstrationEvent,
    onDismiss: () -> Unit,
    viewModel: ViewModel,
    currentDate: LocalDate,
    selectedDate: Int
) {
    val coroutineScope = rememberCoroutineScope()
    var eventName by remember { mutableStateOf(event.name) }
    var eventAmount by remember { mutableStateOf(event.amount.toString()) }
    var repeatInterval by remember { mutableIntStateOf(1) }
    var repeatUnit by remember { mutableStateOf("Days") }
    var endCondition by remember { mutableStateOf("Never") }
    var endAfterOccurrences by remember { mutableStateOf("0") }
    var endDateInput by remember { mutableStateOf("") }
    var isDateValid by remember { mutableStateOf(true) }

    val repeatUnits = listOf("Days", "Weeks", "Months", "Years")
    val endConditions = listOf("Never", "After N times", "Until date")
    var expandedUnit by remember { mutableStateOf(false) }
    var expandedEndCondition by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun validateInput(name: String, amount: String): Boolean {
        return name.isNotEmpty() && amount.toFloatOrNull() != null && (endCondition != "Until date" || isDateValid)
    }

    fun validateDate(input: String): Boolean {
        return try {
            LocalDate.parse(input, dateFormatter)
            true
        } catch (e: Exception) {
            false
        }
    }
    if (event.type == "Static"){
        val selectedEvent = viewModel.getEventById(event.referenceId)
        Dialog(onDismissRequest = { onDismiss() }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = richBlack
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Update event details", color = Purple40, style = dialogHeader)
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomTextField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        label = "Title",
                        modifier = Modifier.fillMaxWidth(1f)
                    )
                    CustomTextField(
                        value = eventAmount,
                        onValueChange = { eventAmount = if (it.toIntOrNull() != null){ it } else {""}},
                        label = "Amount",
                        modifier = Modifier.fillMaxWidth(1f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(onClick = { onDismiss() }, colors = customButtonColors(), contentPadding = PaddingValues(vertical = 2.dp, horizontal = 15.dp)) {
                            Text("Cancel")
                        }
                        Button(onClick = {
                            coroutineScope.launch {
                                if (selectedEvent != null) {
                                    viewModel.deleteEvent(selectedEvent)
                                }
                            }
                            onDismiss()
                        }, colors = customButtonColors(),
                            contentPadding = PaddingValues(vertical = 2.dp, horizontal = 15.dp)) {
                            Text("Delete")
                        }
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val newEvent = selectedEvent?.copy(
                                        name = eventName,
                                        amount = eventAmount.toFloat()
                                    )
                                    if (newEvent != null) {
                                        viewModel.updateEvent(newEvent)
                                    }

                                }
                                onDismiss()
                            },
                            colors = customButtonColors(),
                            contentPadding = PaddingValues(vertical = 2.dp, horizontal = 30.dp),
                        enabled = validateInput(eventName, eventAmount)
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
    else {
        val selectedEvent =  viewModel.getRecurringEventById(event.referenceId)
        expandedUnit = true
        Dialog(onDismissRequest = { onDismiss() }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = richBlack
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column {


                        Text("Update event details", color = Purple40, style = dialogHeader)
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomTextField(
                            value = eventName,
                            onValueChange = { eventName = it },
                            label = "Title",
                            modifier = Modifier.fillMaxWidth(1f)
                        )
                        CustomTextField(
                            value = eventAmount,
                            onValueChange = { eventAmount = if (it.toIntOrNull() != null){ it } else {""}},
                            label = "Amount",
                            modifier = Modifier.fillMaxWidth(1f)
                        )
                        ExposedDropdownMenuBox(
                            expanded = expandedUnit,
                            onExpandedChange = { expandedUnit = it }
                        ) {
                            CustomTextField(
                                value = repeatUnit,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit)
                                },
                                label = "Repeat",
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(1f)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedUnit,
                                onDismissRequest = { expandedUnit = false }
                            ) {
                                repeatUnits.forEach { unit ->
                                    DropdownMenuItem(
                                        onClick = {
                                            repeatUnit = unit
                                            expandedUnit = false
                                        },
                                        text = { Text(unit) }
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Every", color = Color.White)
                            CustomTextField(
                                value = repeatInterval.toString(),
                                onValueChange = {
                                    if (it.toIntOrNull() != null) {
                                        repeatInterval = it.toInt()
                                    }
                                },
                                modifier = Modifier
                                    .width(110.dp)
                                    .padding(horizontal = 16.dp)
                            )
                            Text(repeatUnit, color = Color.White)
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandedEndCondition,
                            onExpandedChange = { expandedEndCondition = it }
                        ) {
                            CustomTextField(
                                value = endCondition,
                                onValueChange = {},
                                readOnly = true,
                                label = "End condition",
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEndCondition)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedEndCondition,
                                onDismissRequest = { expandedEndCondition = false }
                            ) {
                                endConditions.forEach { condition ->
                                    DropdownMenuItem(
                                        onClick = {
                                            endCondition = condition
                                            expandedEndCondition = false
                                        },
                                        text = { Text(condition) }
                                    )
                                }
                            }
                        }
                        when (endCondition) {
                            "After N times" -> {
                                CustomTextField(
                                    value = endAfterOccurrences,
                                    onValueChange = { endAfterOccurrences = if (it.toIntOrNull() != null && it.toInt() > 0){ it } else {""}},
                                    label = "Occurrences",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            "Until date" -> {
                                CustomTextField(
                                    value = endDateInput,
                                    onValueChange = {
                                        endDateInput = it
                                        isDateValid = validateDate(it)
                                    },
                                    label = "End Date (yyyy-MM-dd)",
                                    labelColor = if (isDateValid) Purple40 else Color.Red,
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = !isDateValid
                                )
                                if (!isDateValid) {
                                    Text("Invalid date format", color = Color.Red)
                                }
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(onClick = { onDismiss() }, colors = customButtonColors(), contentPadding = PaddingValues(vertical = 2.dp, horizontal = 15.dp)) {
                            Text("Cancel")
                        }
                        Button(onClick = {
                            onDismiss()
                                if (selectedEvent != null) {
                                    viewModel.deleteEventRecurring(selectedEvent)
                                }  }, contentPadding = PaddingValues(vertical = 2.dp, horizontal = 25.dp),
                            colors = customButtonColors()) {
                            Text("Delete")
                        }
                        Button(onClick = {
                            val endDate = if (endCondition == "Until date" && isDateValid) {
                                LocalDate.parse(endDateInput, dateFormatter)
                            } else {
                                null
                            }
                                val newEvent = selectedEvent?.copy(name = eventName,
                                    amount = eventAmount.toFloat(),
                                    repeatInterval = repeatInterval,
                                    repeatUnit = repeatUnit,
                                    endCondition = endCondition,
                                    endAfterOccurrences = endAfterOccurrences.toInt(),
                                    endDate = if(endCondition != "Never") {calculateEndDate(startDate = LocalDate.of(
                                        currentDate.year,
                                        currentDate.month,
                                        selectedDate
                                    ),
                                        repeatInterval,
                                        repeatUnit,
                                        endCondition,
                                        endAfterOccurrences.toInt(),
                                        endDate)} else {null})
                                if (newEvent != null) {
                                    viewModel.updateEventRecurring(newEvent)
                                }
                            onDismiss()
                        }
                            ,contentPadding = PaddingValues(vertical = 2.dp, horizontal = 25.dp),
                            colors = customButtonColors(), enabled = validateInput(eventName, eventAmount)) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun EnterNewMoneyValueDialog(
    previousValue: Float,
    onDismiss: () -> Unit,
    onConfirm: (newValue: Float) -> Unit
){
    var temporaryValue by remember { mutableStateOf(previousValue.toString()) }
    fun validateInput(amount: String): Boolean { return amount.toFloatOrNull() != null }
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = richBlack
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Enter new amount of money", color = Purple40, style = dialogHeader)
                Spacer(modifier = Modifier.height(8.dp))
                CustomTextField(
                    value = temporaryValue,
                    onValueChange = { temporaryValue = it },
                    label = "New value",
                    modifier = Modifier.fillMaxWidth(1f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { onDismiss() }, colors = customButtonColors()) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        onConfirm(temporaryValue.toFloat())
                        onDismiss()
                    }, colors = customButtonColors(), enabled = validateInput(temporaryValue)) {
                        Text("Confirm")
                    }
                }
            }

        }
    }
}
@Composable
fun BudgetCard(
    budgetAmount: String,
    totalBudget: String,
    progress: Float,
    statusText: String,
    timeProgress: Float,
    modifier: Modifier = Modifier,
    viewModel: ViewModel
) {
    var isEditingBudget by remember { mutableStateOf(false) }
    var newBudget by remember { mutableStateOf("") }
    LaunchedEffect(key1 = newBudget) {
        if (newBudget.toFloatOrNull() != null){
        viewModel.saveBudgetValue(newBudget.toFloat()) }
    }
    val currentDate by remember {
        mutableStateOf(viewModel.currentDate)
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { isEditingBudget = !isEditingBudget },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            HeaderSection(budgetAmount = budgetAmount, totalBudget = totalBudget)
            ProgressSection(progress = progress, statusText = statusText, timeProgress = timeProgress)
            if (isEditingBudget) {
                NewBudgetTextField(
                    pastBudget = newBudget,
                    onNewBudgetChange = { newBudget = it }
                )
            }
        }
    }
}

@Composable
fun HeaderSection(budgetAmount: String, totalBudget: String) {
    Surface(
        color = periwinkle, // Purple background for header
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush =
                    horizontalGradient(
                        colors = listOf(
                            Color(0xFFBA57D5),
                            Color(0xFFEBA4FF)
                        )
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = "Your budget for this month is:",
                style = customText,
                color = Color.White,
                fontSize = 16.sp,
            )
            Text(
                text = "$budgetAmount out of $totalBudget",
                style = customTitle,
                color = Color.White,
                fontSize = 20.sp,
            )
        }
    }
}

@Composable
fun ProgressSection(progress: Float, statusText: String, timeProgress: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(timeProgress - 0.05f))
            Text(
                text = "today",
                fontSize = 12.sp,
                fontFamily = montserratFontFamily,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f - timeProgress + 0.05f))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        ) {
            // Progress bar for money spent
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
                color = periwinkle,
                trackColor = Color.Gray
            )

            // Draw the month progress line using Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                val lineWidth = 2.dp.toPx()
                val lineHeight = size.height
                val xPosition = size.width * timeProgress.coerceIn(0f, 1f)

                drawLine(
                    color = Color.White,
                    start = Offset(x = xPosition - lineWidth / 2, y = 0f),
                    end = Offset(x = xPosition - lineWidth / 2, y = lineHeight),
                    strokeWidth = lineWidth
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = statusText,
            style = standardText,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}
@Composable
fun NewBudgetTextField(pastBudget: String, onNewBudgetChange: (String) -> Unit) {
    var budget by remember {
        mutableStateOf(pastBudget)
    }
    var isEnabled by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = budget) {
        if (budget.toFloatOrNull() != null ){
            if (budget.toFloat() > 0f){
                isEnabled = true
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // Purple background for text field section
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "New budget",
            color = Color.White,
            fontSize = 16.sp
        )
        TextField(
            value = budget,
            onValueChange = { newValue ->
                budget = newValue
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
            placeholder = {
                Text(
                    text = "Enter new budget",
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = periwinkle,
                focusedIndicatorColor = periwinkle,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = periwinkle.copy(alpha = 1f),
                unfocusedContainerColor = periwinkle.copy(alpha = 0.7f)
            ),
            trailingIcon = {
                IconButton(
                    onClick = { onNewBudgetChange(budget) },
                    enabled = isEnabled
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Add event",
                        tint = Color.White
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(21.dp))
    }
}

