package com.example.sigmafinance.main


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.sigmafinance.R
import com.example.sigmafinance.database.TemporaryLists
import com.example.sigmafinance.ui.theme.Purple40
import com.example.sigmafinance.ui.theme.dialogHeader
import com.example.sigmafinance.ui.theme.jordyBlue
import com.example.sigmafinance.ui.theme.montserratFontFamily
import com.example.sigmafinance.ui.theme.periwinkle
import com.example.sigmafinance.ui.theme.richBlack
import com.example.sigmafinance.viewmodel.ViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        containerColor = Purple40,
        contentColor = Color.White,
        disabledContainerColor = Color.Gray.copy(alpha = 0.2f),
        disabledContentColor = Color.White.copy(alpha = 0.1f)
    )
}
@Composable
fun GraphImage() {
    Image(
        painter = painterResource(id = R.drawable.graph1),
        contentDescription = "Graph icon",
        contentScale = ContentScale.Fit,

    )
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
    var repeatInterval by remember { mutableIntStateOf(1) } // Default repeat every 1 unit
    var repeatUnit by remember { mutableStateOf("Don't repeat") }
    var endCondition by remember { mutableStateOf("Never") }
    var endAfterOccurrences by remember { mutableStateOf<Int?>(null) }
    var endDateInput by remember { mutableStateOf("") } // String to take user input for date
    var isDateValid by remember { mutableStateOf(true) } // To check date validity

    val repeatUnits = listOf("Don't repeat","Days", "Weeks", "Months", "Years")
    val endConditions = listOf("Never", "After N times", "Until date")
    var expandedUnit by remember { mutableStateOf(false) }
    var expandedEndCondition by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun validateInput(name: String, amount: String): Boolean {
        return name.isNotEmpty() && amount.toFloatOrNull() != null && (endCondition != "Until date" || isDateValid)
    }

    // Date validation function
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
                if(repeatUnit != "Don't repeat") {
                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Every", color = Color.White)
                        CustomTextField(
                            value = repeatInterval.toString(),
                            onValueChange = {
                                if(it.toIntOrNull() != null){
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
                                value = endAfterOccurrences?.toString() ?: "",
                                onValueChange = { endAfterOccurrences = it.toIntOrNull() },
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
                        onConfirm(eventName, eventAmount, repeatInterval, repeatUnit, endCondition, endAfterOccurrences, endDate)
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
    var endAfterOccurrences by remember { mutableStateOf<Int?>(null) }
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
                                    value = endAfterOccurrences?.toString() ?: "",
                                    onValueChange = { endAfterOccurrences = it.toIntOrNull() },
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
                            coroutineScope.launch {
                                if (selectedEvent != null) {
                                    viewModel.deleteEventRecurring(selectedEvent)
                                } } }, contentPadding = PaddingValues(vertical = 2.dp, horizontal = 25.dp),
                            colors = customButtonColors()) {
                            Text("Delete")
                        }
                        Button(onClick = {
                            val endDate = if (endCondition == "Until date" && isDateValid) {
                                LocalDate.parse(endDateInput, dateFormatter)
                            } else {
                                null
                            }
                            coroutineScope.launch {
                                val newEvent = selectedEvent?.copy(name = eventName,
                                    amount = eventAmount.toFloat(),
                                    repeatInterval = repeatInterval,
                                    repeatUnit = repeatUnit,
                                    endCondition = endCondition,
                                    endAfterOccurrences = endAfterOccurrences,
                                    endDate = if(endCondition != "Never") {calculateEndDate(startDate = LocalDate.of(
                                        currentDate.year,
                                        currentDate.month,
                                        selectedDate
                                    ),
                                        repeatInterval,
                                        repeatUnit,
                                        endCondition,
                                        endAfterOccurrences,
                                        endDate)} else {null})
                                if (newEvent != null) {
                                    viewModel.updateEventRecurring(newEvent)
                                }

                            }
                            onDismiss()
                        }
                            , contentPadding = PaddingValues(vertical = 2.dp, horizontal = 25.dp),
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

    var temporaryValue by remember {
        mutableStateOf(previousValue.toString())
    }
    fun validateInput(amount: String): Boolean {
        return amount.toFloatOrNull() != null
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
                        onConfirm(
                            temporaryValue.toFloat()
                        )
                        onDismiss()
                    }, colors = customButtonColors(), enabled = validateInput(temporaryValue)) {
                        Text("Confirm")
                    }
                }
            }

        }
    }
}

