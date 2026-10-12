
package com.aquila.pocxpertalerts.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.data.model.AlertDto
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.viewmodel.AlertsViewModel
import java.util.Calendar
import java.util.Locale

// ------------------------------------------------------------
// COLORS
// ------------------------------------------------------------

private val SearchOrange = Color(0xFFED741C)
private val SearchGreen = Color(0xFF009B62)
private val ScreenBackground = Color(0xFFFAFAFA)
private val FieldBorder = Color(0xFFBDBDBD)
private val TextBlack = Color(0xFF000000)
private val HintGray = Color(0xFF757575)

// ------------------------------------------------------------
// SEARCH ALERTS SCREEN
// ------------------------------------------------------------

@Composable
fun SearchAlertsScreen(
    onBackClick: () -> Unit,
    onSearchClick: (
        startDate: String,
        endDate: String,
        keyword: String,
        viewBy: String,
        application: String,
        alertDefinition: String
    ) -> Unit,
    alertsViewModel: AlertsViewModel
) {
    val alerts by alertsViewModel.alerts.collectAsState()
    val isLoading by alertsViewModel.isLoading.collectAsState()
    val errorMessage by alertsViewModel.errorMessage.collectAsState()

    var keyword by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("10/09/2026") }
    var endDate by remember { mutableStateOf("10/12/2026") }

    var viewBy by remember { mutableStateOf("") }
    var application by remember { mutableStateOf("") }
    var alertDefinition by remember { mutableStateOf("") }

    var selectedTab by remember { mutableStateOf("alerts") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            SearchBottomNavigation(
                selectedTab = selectedTab,
                onTabClick = { selectedTab = it }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ScreenBackground)
        ) {

            // ORANGE TOOLBAR

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SearchOrange)
                    .statusBarsPadding()
                    .height(60.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onBackClick)
                )

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Search Alerts",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Reset",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        keyword = ""
                        startDate = ""
                        endDate = ""
                        viewBy = ""
                        application = ""
                        alertDefinition = ""
                    }
                )
            }

            // SEARCH FORM AND RESULTS

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 8.dp)
            ) {

                CompactTextField(
                    value = keyword,
                    onValueChange = { keyword = it },
                    placeholder = "Search By",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 20.dp
                        )
                        .height(45.dp)
                )

                FormLabel(
                    text = "Start Date",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 20.dp
                        )
                )

                CompactDateField(
                    value = startDate,
                    onDateSelected = { startDate = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 10.dp
                        )
                        .height(45.dp)
                )

                FormLabel(
                    text = "End Date",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 20.dp
                        )
                )

                CompactDateField(
                    value = endDate,
                    onDateSelected = { endDate = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 10.dp
                        )
                        .height(45.dp)
                )

                // View By dropdown.
                // These are display labels, not backend IDs.

                CompactDropdown(
                    selectedValue = viewBy,
                    placeholder = "- Select -",
                    options = listOf(
                        "Alert Definition",
                        "Application"
                    ),
                    onSelected = {
                        viewBy = it
                        application = ""
                        alertDefinition = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 20.dp
                        )
                        .height(45.dp)
                )

                // TODO: Replace placeholder application options
                // with the actual application list from the API.

                CompactDropdown(
                    selectedValue = application,
                    placeholder = "- Select Application -",
                    options = emptyList(),
                    onSelected = { application = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 20.dp
                        )
                        .height(45.dp)
                )

                // TODO: Replace placeholder alert-definition options
                // with the actual alert-definition list from the API.

                CompactDropdown(
                    selectedValue = alertDefinition,
                    placeholder = "- Select Alert Definition -",
                    options = emptyList(),
                    onSelected = { alertDefinition = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 20.dp
                        )
                        .height(45.dp)
                )

                Button(
                    onClick = {
                        onSearchClick(
                            startDate,
                            endDate,
                            keyword,
                            viewBy,
                            application,
                            alertDefinition
                        )
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 30.dp
                        )
                        .height(45.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SearchOrange,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 12.dp,
                        vertical = 0.dp
                    )
                ) {
                    Text(
                        text = if (isLoading) "Searching..." else "Search",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // API LOADING INDICATOR

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = SearchOrange
                        )
                    }
                }

                // API ERROR MESSAGE

                if (!isLoading && errorMessage != null) {
                    Text(
                        text = errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 16.dp
                        ),
                        fontSize = 14.sp
                    )
                }

                // REAL ALERT RESULTS

                if (!isLoading && errorMessage == null) {
                    if (alerts.isEmpty()) {
                        Text(
                            text = "Search for alerts to see results.",
                            color = HintGray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            fontSize = 14.sp
                        )
                    } else {
                        Text(
                            text = "Alerts (${alerts.size})",
                            modifier = Modifier.padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 24.dp,
                                bottom = 8.dp
                            ),
                            color = TextBlack,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        alerts.forEach { alert ->
                            AlertResultItem(alert = alert)
                        }
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------
// ALERT RESULT ITEM
// ------------------------------------------------------------

@Composable
private fun AlertResultItem(alert: AlertDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = alert.subject?.takeIf { it.isNotBlank() }
                ?: "No subject",
            color = TextBlack,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = alert.message?.takeIf { it.isNotBlank() }
                ?: "No message",
            color = HintGray,
            fontSize = 14.sp
        )

        if (!alert.alertSendDate.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = alert.alertSendDate,
                color = HintGray,
                fontSize = 12.sp
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 12.dp),
            color = FieldBorder
        )
    }
}

// ------------------------------------------------------------
// FORM LABEL
// ------------------------------------------------------------

@Composable
private fun FormLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = TextBlack,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
}

// ------------------------------------------------------------
// SEARCH BY FIELD
// ------------------------------------------------------------

@Composable
private fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(ScreenBackground)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = TextStyle(
                color = TextBlack,
                fontSize = 16.5.sp
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = HintGray,
                            fontSize = 16.5.sp
                        )
                    }

                    innerTextField()
                }
            }
        )
    }
}

// ------------------------------------------------------------
// DATE FIELD
// ------------------------------------------------------------

@Composable
private fun CompactDateField(
    value: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier.clickable {
            val calendar = Calendar.getInstance()
            val parts = value.split("/")

            if (parts.size == 3) {
                calendar.set(
                    parts[2].toIntOrNull()
                        ?: calendar.get(Calendar.YEAR),
                    (parts[0].toIntOrNull() ?: 1) - 1,
                    parts[1].toIntOrNull() ?: 1
                )
            }

            DatePickerDialog(
                context,
                { _, year, month, day ->
                    onDateSelected(
                        String.format(
                            Locale.US,
                            "%02d/%02d/%04d",
                            month + 1,
                            day,
                            year
                        )
                    )
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        },
        shape = RoundedCornerShape(24.dp),
        color = ScreenBackground,
        border = BorderStroke(1.dp, FieldBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Choose date",
                tint = HintGray,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = value.ifBlank { "Select date" },
                color = if (value.isBlank()) HintGray else TextBlack,
                fontSize = 16.5.sp
            )
        }
    }
}

// ------------------------------------------------------------
// DROPDOWN FIELD
// ------------------------------------------------------------

@Composable
private fun CompactDropdown(
    selectedValue: String,
    placeholder: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (options.isNotEmpty()) {
                        expanded = true
                    }
                },
            shape = RoundedCornerShape(24.dp),
            color = ScreenBackground,
            border = BorderStroke(1.dp, FieldBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp,
                        end = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedValue.ifEmpty { placeholder },
                    modifier = Modifier.weight(1f),
                    color = HintGray,
                    fontSize = 14.sp,
                    maxLines = 1
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select $placeholder",
                    tint = HintGray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontSize = 14.sp
                        )
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ------------------------------------------------------------
// BOTTOM NAVIGATION
// ------------------------------------------------------------

@Composable
private fun SearchBottomNavigation(
    selectedTab: String,
    onTabClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBottomItem(
                icon = R.drawable.ic_alert_list_bell,
                selected = selectedTab == "alerts",
                onClick = { onTabClick("alerts") },
                modifier = Modifier.weight(1f)
            )

            SearchBottomItem(
                icon = R.drawable.ic_alert_list_subscription,
                selected = selectedTab == "subscriptions",
                onClick = { onTabClick("subscriptions") },
                modifier = Modifier.weight(1f)
            )

            SearchBottomItem(
                icon = R.drawable.ic_alert_list_bel,
                selected = selectedTab == "notifications",
                onClick = { onTabClick("notifications") },
                modifier = Modifier.weight(1f)
            )

            SearchBottomItem(
                icon = R.drawable.ic_alert_list_menu,
                selected = selectedTab == "menu",
                onClick = { onTabClick("menu") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SearchBottomItem(
    icon: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(3.dp))

        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                if (selected) SearchOrange else SearchGreen
            ),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    if (selected) SearchOrange else Color.White
                )
        )
    }
}

// ------------------------------------------------------------
// PREVIEW
// ------------------------------------------------------------

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SearchAlertsScreenPreview() {
    val alertsViewModel: AlertsViewModel = viewModel()

    XpertAlertsTheme {
        SearchAlertsScreen(
            onBackClick = {},
            onSearchClick = { _, _, _, _, _, _ -> },
            alertsViewModel = alertsViewModel
        )
    }
}
