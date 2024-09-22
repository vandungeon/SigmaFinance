package com.example.sigmafinance.main

import android.widget.Space
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.sigmafinance.ui.theme.Purple40
import com.example.sigmafinance.ui.theme.Purple80
import com.example.sigmafinance.ui.theme.SigmaFinanceTheme
import com.example.sigmafinance.ui.theme.accentGrey
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun customButtonColors(): ButtonColors {
    return ButtonDefaults.buttonColors(
        containerColor = Purple40,
        contentColor = Color.White,
        disabledContainerColor = Color.Gray.copy(alpha = 0.2f),
        disabledContentColor = Color.White.copy(alpha = 0.1f)
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
    var repeatInterval by remember { mutableStateOf(1) } // Default repeat every 1 unit
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
            color = accentGrey
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("New Event", color = Purple40)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = { Text("Title", color = Purple40) },
                    modifier = Modifier.fillMaxWidth(1f)
                )
                TextField(
                    value = eventAmount,
                    onValueChange = { eventAmount = it },
                    label = { Text("Amount", color = Purple40) },
                    modifier = Modifier.fillMaxWidth(1f)
                )
                ExposedDropdownMenuBox(
                    expanded = expandedUnit,
                    onExpandedChange = { expandedUnit = it }
                ) {
                    TextField(
                        value = repeatUnit,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit)
                        },
                        label = { Text("Repeat", color = Purple40) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(1f)
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
                        Text("Every", color = Purple40)
                        TextField(
                            value = repeatInterval.toString(),
                            onValueChange = {
                                if(it.toIntOrNull() != null){
                                    repeatInterval = it.toInt()
                                }
                                            },
                            modifier = Modifier.width(110.dp).padding(horizontal = 16.dp)
                        )
                        Text(repeatUnit, color = Purple40)
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedEndCondition,
                        onExpandedChange = { expandedEndCondition = it }
                    ) {
                        TextField(
                            value = endCondition,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("End condition", color = Purple40) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEndCondition)
                            },
                            modifier = Modifier.fillMaxWidth()
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
                            TextField(
                                value = endAfterOccurrences?.toString() ?: "",
                                onValueChange = { endAfterOccurrences = it.toIntOrNull() },
                                label = { Text("Occurrences", color = Purple40) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        "Until date" -> {
                            TextField(
                                value = endDateInput,
                                onValueChange = {
                                    endDateInput = it
                                    isDateValid = validateDate(it)
                                },
                                label = {
                                    Text(
                                        "End Date (yyyy-MM-dd)",
                                        color = if (isDateValid) Purple40 else Color.Red
                                    )
                                },
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

