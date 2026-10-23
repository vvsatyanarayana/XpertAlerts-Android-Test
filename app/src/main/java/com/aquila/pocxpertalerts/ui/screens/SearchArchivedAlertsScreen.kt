
package com.aquila.pocxpertalerts.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.aquila.pocxpertalerts.data.repository.ArchivedSearchCriteria
import com.aquila.pocxpertalerts.data.repository.ArchivedSearchPreferences

private val SearchOrange = Color(0xFFED741C)
private val SearchGray = Color(0xFF757575)
private val SearchBackground = Color(0xFFFAFAFA)
private val FieldBorder = Color(0xFFBDBDBD)
private val FieldShape = RoundedCornerShape(50.dp)

private val ViewByOptions = listOf(
    "All", "Alert Definition", "Application"
)

private val DeviceOptions = listOf(
    "All", "New Alert", "Delivered", "Dismissed",
    "Action Taken", "UnSubscribed", "Notification Off"
)

private val EmailOptions = listOf(
    "All", "Ready To Send", "Email Sent",
    "Notification Off", "Failed Mail"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchArchivedAlertsScreen(
    onBackClick: () -> Unit,
    onSearchClick: (ArchivedSearchCriteria) -> Unit,
    onResetClick: () -> Unit
) {
    val context = LocalContext.current

    // Load and save the previous search settings on this device.
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
            ViewByOptions.getOrElse(initialCriteria.viewByFlag) { "All" }
        )
    }

    var deviceStatus by remember {
        mutableStateOf(
            DeviceOptions.getOrElse(
                initialCriteria.deviceStatusSelection
            ) { "All" }
        )
    }

    var emailStatus by remember {
        mutableStateOf(
            EmailOptions.getOrElse(
                initialCriteria.emailStatusSelection
            ) { "All" }
        )
    }

    // Persist changes automatically, including when the user leaves this screen.
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
                deviceStatusSelection = DeviceOptions.indexOf(deviceStatus)
                    .coerceAtLeast(0),
                emailStatusSelection = EmailOptions.indexOf(emailStatus)
                    .coerceAtLeast(0)
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SearchBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Search Archived Alerts",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SearchOrange,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Keyword field
            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Keyword") },
                placeholder = { Text("Enter keyword") },
                singleLine = true,
                shape = FieldShape,
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

            // Device status
            ArchivedSearchDropdown(
                label = "Device Status",
                selectedValue = deviceStatus,
                options = DeviceOptions,
                onValueSelected = { deviceStatus = it }
            )

            // Email status
            ArchivedSearchDropdown(
                label = "Email Status",
                selectedValue = emailStatus,
                options = EmailOptions,
                onValueSelected = { emailStatus = it }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SearchGray
                    )
                ) {
                    Text(
                        text = "Reset",
                        fontWeight = FontWeight.SemiBold
                    )
                }

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

                        // Save immediately before requesting the search.
                        searchPreferences.save(criteria)
                        onSearchClick(criteria)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SearchOrange
                    )
                ) {
                    Text(
                        text = "Search",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun archivedFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SearchOrange,
        unfocusedBorderColor = FieldBorder,
        focusedLabelColor = SearchOrange,
        cursorColor = SearchOrange,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
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
        placeholder = { Text("Choose date") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = FieldShape,
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
                            ).parse(value)

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
                    color = SearchOrange
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
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            color = SearchGray,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = FieldShape,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (expanded) SearchOrange else FieldBorder
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = SearchGray
                ),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            ) {
                Text(
                    text = selectedValue,
                    modifier = Modifier.weight(1f),
                    color = Color.DarkGray
                )

                Text(
                    text = "▾",
                    color = SearchOrange,
                    fontSize = 20.sp
                )
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
