
package com.aquila.pocxpertalerts.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import com.aquila.pocxpertalerts.data.repository.ArchivedSearchCriteria
import java.util.Locale
private val SearchOrange = Color(0xFFED741C)
private val SearchGray = Color(0xFF757575)

@Composable
fun SearchArchivedAlertsScreen(
    onBackClick: () -> Unit,
    onSearchClick: (ArchivedSearchCriteria) -> Unit,
    onResetClick: () -> Unit
) {
    var keyword by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var viewBy by remember { mutableStateOf("All") }
    var deviceStatus by remember { mutableStateOf("All") }
    var emailStatus by remember { mutableStateOf("All") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Centered toolbar title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(SearchOrange)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Search Archived Alerts",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 52.dp),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 15.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Keyword") },
                singleLine = true
            )

            ArchivedDateField(
                label = "Start Date",
                value = startDate,
                onDateSelected = { startDate = it }
            )

            ArchivedDateField(
                label = "End Date",
                value = endDate,
                onDateSelected = { endDate = it }
            )

            ArchivedSearchDropdown(
                label = "View By",
                selectedValue = viewBy,
                options = listOf("All", "Alert Definition", "Application"),
                onValueSelected = { viewBy = it }
            )

            ArchivedSearchDropdown(
                label = "Device Status",
                selectedValue = deviceStatus,
                options = listOf("All", "New Alert", "Delivered", "Dismissed", "Action Taken", "UnSubscribed", "Notification Off"),
                onValueSelected = { deviceStatus = it }
            )

            ArchivedSearchDropdown(
                label = "Email Status",
                selectedValue = emailStatus,
                options = listOf("All", "Ready To Send", "Email Sent", "Notification Off", "Failed Mail"),
                onValueSelected = { emailStatus = it }
            )

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        keyword = ""
                        startDate = ""
                        endDate = ""
                        viewBy = "All"
                        deviceStatus = "All"
                        emailStatus = "All"
                        onResetClick()
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Reset", color = SearchGray)
                }

                Button(
                    onClick = {
                        val apiStartDate = startDate.toApiDate()
                        val apiEndDate = endDate.toApiDate()
                        if (startDate.isNotBlank() && apiStartDate.isBlank()) {
                            Toast.makeText(context, "Please select a valid start date.", Toast.LENGTH_SHORT).show()
                        } else if (endDate.isNotBlank() && apiEndDate.isBlank()) {
                            Toast.makeText(context, "Please select a valid end date.", Toast.LENGTH_SHORT).show()
                        } else if (apiStartDate.isNotBlank() && apiEndDate.isNotBlank() &&
                            runCatching {
                                SimpleDateFormat("MM-dd-yyyy", Locale.US).parse(apiStartDate)!!
                                    .after(SimpleDateFormat("MM-dd-yyyy", Locale.US).parse(apiEndDate)!!)
                            }.getOrDefault(false)
                        ) {
                            Toast.makeText(context, "Start date cannot be after end date.", Toast.LENGTH_SHORT).show()
                        } else {
                            onSearchClick(
                                ArchivedSearchCriteria(
                                    keyword = keyword,
                                    startDate = apiStartDate,
                                    endDate = apiEndDate,
                                    viewByFlag = when (viewBy) {
                                        "Alert Definition" -> 1
                                        "Application" -> 2
                                        else -> 0
                                    },
                                    deviceStatusSelection = listOf(
                                        "All", "New Alert", "Delivered", "Dismissed",
                                        "Action Taken", "UnSubscribed", "Notification Off"
                                    ).indexOf(deviceStatus).coerceAtLeast(0),
                                    emailStatusSelection = listOf(
                                        "All", "Ready To Send", "Email Sent",
                                        "Notification Off", "Failed Mail"
                                    ).indexOf(emailStatus).coerceAtLeast(0)
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SearchOrange
                    )
                ) {
                    Text("Search", color = Color.White)
                }
            }
        }
    }
}



private fun String.toApiDate(): String {
    if (isBlank()) return ""
    return runCatching {
        val parsed = SimpleDateFormat("dd/MM/yyyy", Locale.US).apply { isLenient = false }.parse(this)
            ?: return ""
        SimpleDateFormat("MM-dd-yyyy", Locale.US).format(parsed)
    }.getOrDefault("")
}

@Composable
private fun ArchivedDateField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        placeholder = { Text("Choose") },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            TextButton(
                onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val selected = Calendar.getInstance().apply {
                                set(year, month, day)
                            }

                            onDateSelected(
                                SimpleDateFormat(
                                    "dd/MM/yyyy",
                                    Locale.getDefault()
                                ).format(selected.time)
                            )
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            ) {
                Text("Choose")
            }
        }
    )
}

@Composable
private fun ArchivedSearchDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            color = SearchGray
        )

        Spacer(Modifier.height(5.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 12.dp
                )
            ) {
                Text(
                    text = selectedValue,
                    modifier = Modifier.weight(1f),
                    color = SearchGray
                )
                Text("▾", color = SearchGray)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// PREVIEW 2: Search archived alerts
@Preview(
    name = "2. Search Archived Alerts",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=873dp,dpi=420"
)
@Composable
private fun SearchArchivedAlertsPreview() {
    MaterialTheme {
        SearchArchivedAlertsScreen(
            onBackClick = {},
            onSearchClick = {},
            onResetClick = {}
        )
    }
}
