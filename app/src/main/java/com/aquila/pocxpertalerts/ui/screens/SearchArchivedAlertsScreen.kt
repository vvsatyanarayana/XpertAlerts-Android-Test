
package com.aquila.pocxpertalerts.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.data.repository.ArchivedSearchCriteria
import com.aquila.pocxpertalerts.data.repository.ArchivedSearchPreferences
import com.aquila.pocxpertalerts.ui.components.AppTopBar
import com.aquila.pocxpertalerts.ui.components.SearchScreenTopBar
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.PlaceholderColor
import com.aquila.pocxpertalerts.ui.theme.SearchBorderColor
import com.aquila.pocxpertalerts.ui.theme.SearchFieldBackground
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val ArchivedFieldShape = RoundedCornerShape(12.dp)
private val ArchivedButtonShape = RoundedCornerShape(12.dp)

private val ViewByOptions = listOf(
    "All",
    "Alert Definition",
    "Application"
)

private val DeviceOptions = listOf(
    "All",
    "New Alert",
    "Delivered",
    "Dismissed",
    "Action Taken",
    "UnSubscribed",
    "Notification Off"
)

private val EmailOptions = listOf(
    "All",
    "Ready To Send",
    "Email Sent",
    "Notification Off",
    "Failed Mail"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchArchivedAlertsScreen(
    onBackClick: () -> Unit,
    onSearchClick: (ArchivedSearchCriteria) -> Unit,
    onResetClick: () -> Unit
) {
    val context = LocalContext.current

    val searchPreferences = remember(context) {
        ArchivedSearchPreferences(context)
    }

    val initialCriteria = remember(searchPreferences) {
        searchPreferences.load()
    }

    fun displayDate(apiDate: String): String {
        if (apiDate.isBlank()) return ""

        return runCatching {
            val parsed = SimpleDateFormat(
                "MM-dd-yyyy",
                Locale.US
            ).apply {
                isLenient = false
            }.parse(apiDate) ?: return ""

            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.US
            ).format(parsed)
        }.getOrDefault("")
    }

    var keyword by remember {
        mutableStateOf(initialCriteria.keyword)
    }

    var startDate by remember {
        mutableStateOf(displayDate(initialCriteria.startDate))
    }

    var endDate by remember {
        mutableStateOf(displayDate(initialCriteria.endDate))
    }

    var viewBy by remember {
        mutableStateOf(
            ViewByOptions.getOrElse(initialCriteria.viewByFlag) {
                "All"
            }
        )
    }

    var deviceStatus by remember {
        mutableStateOf(
            DeviceOptions.getOrElse(
                initialCriteria.deviceStatusSelection
            ) {
                "All"
            }
        )
    }

    var emailStatus by remember {
        mutableStateOf(
            EmailOptions.getOrElse(
                initialCriteria.emailStatusSelection
            ) {
                "All"
            }
        )
    }

    // Keep the existing saved-search behavior.
    LaunchedEffect(
        keyword,
        startDate,
        endDate,
        viewBy,
        deviceStatus,
        emailStatus
    ) {
        searchPreferences.save(
            ArchivedSearchCriteria(
                keyword = keyword,
                startDate = startDate.toApiDate(),
                endDate = endDate.toApiDate(),
                viewByFlag = ViewByOptions.indexOf(viewBy)
                    .coerceAtLeast(0),
                deviceStatusSelection =
                    DeviceOptions.indexOf(deviceStatus)
                        .coerceAtLeast(0),
                emailStatusSelection =
                    EmailOptions.indexOf(emailStatus)
                        .coerceAtLeast(0)
            )
        )
    }

    fun resetFields() {
        keyword = ""
        startDate = ""
        endDate = ""
        viewBy = "All"
        deviceStatus = "All"
        emailStatus = "All"
        onResetClick()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            SearchScreenTopBar(
                title = "Search Archived Alerts",
                onBackClick = onBackClick,
                onResetClick = { resetFields() }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 16.dp,
                    vertical = 18.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Search Archived Alerts",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Text(
                text = "Enter your search criteria below.",
                fontSize = 14.sp,
                color = PlaceholderColor
            )

            // Keyword
            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Keyword") },
                placeholder = {
                    Text(
                        "Enter keyword",
                        color = PlaceholderColor
                    )
                },
                singleLine = true,
                shape = ArchivedFieldShape,
                colors = archivedFieldColors()
            )

            // Start date
            ArchivedDateField(
                label = "Start Date",
                value = startDate,
                onDateSelected = { startDate = it }
            )

            // End date
            ArchivedDateField(
                label = "End Date",
                value = endDate,
                onDateSelected = { endDate = it }
            )

            // View By
            ArchivedSearchDropdown(
                label = "View By",
                selectedValue = viewBy,
                options = ViewByOptions,
                onValueSelected = { viewBy = it }
            )

            // Device Status
            ArchivedSearchDropdown(
                label = "Device Status",
                selectedValue = deviceStatus,
                options = DeviceOptions,
                onValueSelected = { deviceStatus = it }
            )

            // Email Status
            ArchivedSearchDropdown(
                label = "Email Status",
                selectedValue = emailStatus,
                options = EmailOptions,
                onValueSelected = { emailStatus = it }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Search button
            Button(
                onClick = {
                    val apiStartDate = startDate.toApiDate()
                    val apiEndDate = endDate.toApiDate()

                    if (
                        startDate.isNotBlank() &&
                        apiStartDate.isBlank()
                    ) {
                        Toast.makeText(
                            context,
                            "Please select a valid start date.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    if (
                        endDate.isNotBlank() &&
                        apiEndDate.isBlank()
                    ) {
                        Toast.makeText(
                            context,
                            "Please select a valid end date.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    if (
                        apiStartDate.isNotBlank() &&
                        apiEndDate.isNotBlank()
                    ) {
                        val start = runCatching {
                            SimpleDateFormat(
                                "MM-dd-yyyy",
                                Locale.US
                            ).apply {
                                isLenient = false
                            }.parse(apiStartDate)
                        }.getOrNull()

                        val end = runCatching {
                            SimpleDateFormat(
                                "MM-dd-yyyy",
                                Locale.US
                            ).apply {
                                isLenient = false
                            }.parse(apiEndDate)
                        }.getOrNull()

                        if (
                            start == null ||
                            end == null ||
                            start.after(end)
                        ) {
                            Toast.makeText(
                                context,
                                "Please check the start and end dates.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                    }

                    val criteria = ArchivedSearchCriteria(
                        keyword = keyword.trim(),
                        startDate = apiStartDate,
                        endDate = apiEndDate,
                        viewByFlag = ViewByOptions.indexOf(viewBy)
                            .coerceAtLeast(0),
                        deviceStatusSelection =
                            DeviceOptions.indexOf(deviceStatus)
                                .coerceAtLeast(0),
                        emailStatusSelection =
                            EmailOptions.indexOf(emailStatus)
                                .coerceAtLeast(0)
                    )

                    searchPreferences.save(criteria)
                    onSearchClick(criteria)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = ArchivedButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = XpertOrange,
                    contentColor = White
                )
            ) {
                Text(
                    text = "Search",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun archivedFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = XpertOrange,
        unfocusedBorderColor = SearchBorderColor,
        focusedLabelColor = XpertOrange,
        unfocusedLabelColor = TextDark,
        focusedTextColor = TextDark,
        unfocusedTextColor = TextDark,
        cursorColor = XpertOrange,
        focusedContainerColor = SearchFieldBackground,
        unfocusedContainerColor = SearchFieldBackground
    )

@Composable
private fun ArchivedDateField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        placeholder = {
            Text(
                "Choose date",
                color = PlaceholderColor
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = ArchivedFieldShape,
        colors = archivedFieldColors(),
        trailingIcon = {
            TextButton(
                onClick = {
                    val calendar = Calendar.getInstance()

                    if (value.isNotBlank()) {
                        runCatching {
                            val parsed = SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.US
                            ).apply {
                                isLenient = false
                            }.parse(value)

                            if (parsed != null) {
                                calendar.time = parsed
                            }
                        }
                    }

                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val selected = Calendar.getInstance().apply {
                                set(
                                    year,
                                    month,
                                    day,
                                    12,
                                    0,
                                    0
                                )
                                set(Calendar.MILLISECOND, 0)
                            }

                            onDateSelected(
                                SimpleDateFormat(
                                    "dd/MM/yyyy",
                                    Locale.US
                                ).format(selected.time)
                            )
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            ) {
                Text(
                    text = "Choose",
                    color = XpertOrange,
                    fontWeight = FontWeight.Medium
                )
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
    var expanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextDark,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = ArchivedFieldShape,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (expanded) {
                        XpertOrange
                    } else {
                        SearchBorderColor
                    }
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SearchFieldBackground,
                    contentColor = TextDark
                ),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            ) {
                Text(
                    text = selectedValue,
                    modifier = Modifier.weight(1f),
                    color = TextDark,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (expanded) "▴" else "▾",
                    color = XpertOrange,
                    fontSize = 20.sp
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = TextDark
                            )
                        },
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

private fun String.toApiDate(): String {
    if (isBlank()) return ""

    return runCatching {
        val parsed = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.US
        ).apply {
            isLenient = false
        }.parse(this) ?: return ""

        SimpleDateFormat(
            "MM-dd-yyyy",
            Locale.US
        ).format(parsed)
    }.getOrDefault("")
}

@Preview(
    name = "Search Archived Alerts",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SearchArchivedAlertsScreenPreview() {
    SearchArchivedAlertsScreen(
        onBackClick = {},
        onSearchClick = {},
        onResetClick = {}
    )
}
