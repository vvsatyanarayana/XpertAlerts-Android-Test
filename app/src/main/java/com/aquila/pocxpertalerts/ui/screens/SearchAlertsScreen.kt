
package com.aquila.pocxpertalerts.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.viewmodel.AlertsViewModel
import java.util.Calendar
import java.util.Locale
import com.aquila.pocxpertalerts.data.local.SearchAlertsPersistence
import com.aquila.pocxpertalerts.data.local.SavedAlertSearchFilters
import com.aquila.pocxpertalerts.ui.screens.FieldBorder


private val SearchOrange = Color(0xFFED741C)
private val SearchGreen = Color(0xFF009B62)
private val ScreenBackground = Color(0xFFFAFAFA)
private val FieldBorder = Color(0xFFBDBDBD)
private val TextBlack = Color(0xFF000000)
private val HintGray = Color(0xFF757575)

// ============================================================
// SEARCH ALERTS SCREEN
// ============================================================

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
    alertsViewModel: AlertsViewModel,
    userId: String,
    userTypeId: String,
    sessionId: String,
    onAlertsTabClick: () -> Unit = onBackClick,
    onSubscriptionsTabClick: () -> Unit = {},
    onForwardAlertsTabClick: () -> Unit = {},
    onMenuTabClick: () -> Unit = onBackClick
) {
    val isLoading by alertsViewModel.isLoading.collectAsState()
    val errorMessage by alertsViewModel.errorMessage.collectAsState()
    val applications by alertsViewModel.applications.collectAsState()
    val alertDefinitions by alertsViewModel.alertDefinitions.collectAsState()
    val isLoadingApplications by
    alertsViewModel.isLoadingApplications.collectAsState()
    val isLoadingDefinitions by
    alertsViewModel.isLoadingDefinitions.collectAsState()
    val dropdownErrorMessage by
    alertsViewModel.dropdownErrorMessage.collectAsState()

    LaunchedEffect(userId, userTypeId, sessionId) {
        alertsViewModel.loadApplications(
            userId = userId,
            userTypeId = userTypeId,
            sessionId = sessionId
        )
    }


    val context = LocalContext.current.applicationContext

    val searchPersistence = remember(context) {
        SearchAlertsPersistence(context)
    }

// Load the saved form once for the current user.
    val initialFilters = remember(userId) {
        searchPersistence.loadFilters(userId)
    }

    var keyword by rememberSaveable(userId) {
        mutableStateOf(initialFilters.keyword)
    }

    var startDate by rememberSaveable(userId) {
        mutableStateOf(initialFilters.startDate)
    }

    var endDate by rememberSaveable(userId) {
        mutableStateOf(initialFilters.endDate)
    }

    var viewBy by rememberSaveable(userId) {
        mutableStateOf(initialFilters.viewBy)
    }

    var applicationName by rememberSaveable(userId) {
        mutableStateOf(initialFilters.applicationName)
    }

    var applicationId by rememberSaveable(userId) {
        mutableStateOf(initialFilters.applicationId)
    }

    var alertDefinitionName by rememberSaveable(userId) {
        mutableStateOf(initialFilters.alertDefinitionName)
    }

    var alertDefinitionId by rememberSaveable(userId) {
        mutableStateOf(initialFilters.alertDefinitionId)
    }

    var selectedTab by rememberSaveable(userId) {
        mutableStateOf("alerts")
    }

// Save form changes locally so they survive logout and app closure.
    LaunchedEffect(
        userId,
        keyword,
        startDate,
        endDate,
        viewBy,
        applicationName,
        applicationId,
        alertDefinitionName,
        alertDefinitionId
    ) {
        if (userId.isNotBlank()) {
            searchPersistence.saveFilters(
                userId = userId,
                filters = SavedAlertSearchFilters(
                    keyword = keyword,
                    startDate = startDate,
                    endDate = endDate,
                    viewBy = viewBy,
                    applicationName = applicationName,
                    applicationId = applicationId,
                    alertDefinitionName = alertDefinitionName,
                    alertDefinitionId = alertDefinitionId
                )
            )
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            SearchBottomNavigation(
                selectedTab = selectedTab,
                onAlertsClick = {
                    selectedTab = "alerts"
                    onAlertsTabClick()
                },
                onSubscriptionsClick = {
                    selectedTab = "subscriptions"
                    onSubscriptionsTabClick()
                },
                onForwardAlertsClick = {
                    selectedTab = "forward"
                    onForwardAlertsTabClick()
                },
                onMenuClick = {
                    selectedTab = "menu"
                    onMenuTabClick()
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ScreenBackground)
        ) {
            // Orange toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SearchOrange)
                    .statusBarsPadding()
                    .height(52.dp)
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        keyword = ""
                        startDate = ""
                        endDate = ""
                        viewBy = "- Select -"
                        applicationName = "- Select Application -"
                        applicationId = ""
                        alertDefinitionName = "- Select Alert Definition -"
                        alertDefinitionId = ""

                        alertsViewModel.loadAlertDefinitions(
                            applicationId = "",
                            sessionId = sessionId
                        )
                        alertsViewModel.clearError()
                        alertsViewModel.clearDropdownError()
                    }
                )
            }

            // Scrollable search form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 12.dp)
            ) {
                CompactTextField(
                    value = keyword,
                    onValueChange = { keyword = it },
                    placeholder = "Search By",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 18.dp)
                        .height(45.dp)
                )

                FormLabel(
                    text = "Start Date",
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 18.dp
                    )
                )

                CompactDateField(
                    value = startDate,
                    onDateSelected = { startDate = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .height(45.dp)
                )

                FormLabel(
                    text = "End Date",
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 10.dp
                    )
                )

                CompactDateField(
                    value = endDate,
                    onDateSelected = { endDate = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .height(45.dp)
                )

                CompactDropdown(
                    selectedValue = viewBy,
                    placeholder = "- Select -",
                    options = listOf(
                        "- Select -",
                        "Alert Definition",
                        "Application"
                    ),
                    onSelected = { viewBy = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .height(45.dp)
                )

                CompactDropdown(
                    selectedValue = applicationName,
                    placeholder = if (isLoadingApplications) {
                        "Loading applications..."
                    } else {
                        "- Select Application -"
                    },
                    options = listOf("- Select Application -") +
                            applications.map { it.name },
                    onSelected = { name ->
                        val app = applications.firstOrNull {
                            it.name == name
                        }

                        applicationName =
                            app?.name ?: "- Select Application -"
                        applicationId = app?.id.orEmpty()

                        alertDefinitionName = "- Select Alert Definition -"
                        alertDefinitionId = ""

                        alertsViewModel.loadAlertDefinitions(
                            applicationId = applicationId,
                            sessionId = sessionId
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .height(45.dp)
                )

                CompactDropdown(
                    selectedValue = alertDefinitionName,
                    placeholder = if (isLoadingDefinitions) {
                        "Loading alert definitions..."
                    } else {
                        "- Select Alert Definition -"
                    },
                    options = listOf("- Select Alert Definition -") +
                            if (applicationId.isBlank()) {
                                emptyList()
                            } else {
                                alertDefinitions.map { it.name }
                            },
                    onSelected = { name ->
                        val definition = alertDefinitions.firstOrNull {
                            it.name == name
                        }

                        alertDefinitionName =
                            definition?.name ?: "- Select Alert Definition -"
                        alertDefinitionId = definition?.id.orEmpty()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .height(45.dp)
                )

                Button(
                    onClick = {
                        onSearchClick(
                            startDate,
                            endDate,
                            keyword,
                            when (viewBy) {
                                "Alert Definition" -> "1"
                                "Application" -> "2"
                                else -> "0"
                            },
                            applicationId.ifBlank { "0" },
                            alertDefinitionId.ifBlank { "0" }
                        )
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 18.dp)
                        .height(46.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SearchOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (isLoading) "Searching..." else "Search",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (dropdownErrorMessage != null) {
                    Text(
                        text = dropdownErrorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 8.dp
                        ),
                        fontSize = 13.sp
                    )
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SearchOrange)
                    }
                }

                if (!isLoading && errorMessage != null) {
                    Text(
                        text = errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 12.dp
                        ),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// ============================================================
// LABEL
// ============================================================

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

// ============================================================
// KEYWORD FIELD
// ============================================================

@Composable
private fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .border(
                BorderStroke(1.dp, Color.LightGray),
                RoundedCornerShape(24.dp)
            )
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
                fontSize = 16.sp
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = HintGray,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

// ============================================================
// DATE FIELD
// ============================================================

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

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = value.ifBlank { "Select date" },
                color = if (value.isBlank()) HintGray else TextBlack,
                fontSize = 16.sp
            )
        }
    }
}

// ============================================================
// DROPDOWN FIELD
// ============================================================

@Composable
private fun CompactDropdown(
    selectedValue: String,
    placeholder: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var fieldSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    val selected = selectedValue.isNotBlank() &&
            selectedValue != placeholder

    Box(
        modifier = modifier.onGloballyPositioned {
            fieldSize = it.size
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (options.isNotEmpty()) expanded = !expanded
                },
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedValue.ifBlank { placeholder },
                    modifier = Modifier.weight(1f),
                    color = if (selected) Color(0xFF202020) else HintGray,
                    fontSize = 13.sp,
                    maxLines = 1
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Open dropdown",
                    tint = Color.DarkGray
                )
            }
        }

        if (expanded && fieldSize.width > 0) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, fieldSize.height),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true)
            ) {
                Surface(
                    modifier = Modifier
                        .width(with(density) { fieldSize.width.toDp() })
                        .heightIn(max = 280.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFDDDDDD)),
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(
                            rememberScrollState()
                        )
                    ) {
                        options.forEachIndexed { index, option ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelected(option)
                                        expanded = false
                                    }
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 12.dp
                                    )
                            ) {
                                Text(
                                    text = option,
                                    color = Color(0xFF202020),
                                    fontSize = 13.sp
                                )
                            }

                            if (index < options.lastIndex) {
                                HorizontalDivider(
                                    color = Color(0xFFEEEEEE),
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// BOTTOM NAVIGATION - SAME SIZE AS HOME
// ============================================================

@Composable
private fun SearchBottomNavigation(
    selectedTab: String,
    onAlertsClick: () -> Unit,
    onSubscriptionsClick: () -> Unit,
    onForwardAlertsClick: () -> Unit,
    onMenuClick: () -> Unit
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
                .height(44.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBottomItem(
                icon = R.drawable.ic_alert_list_bell,
                description = "Alerts",
                selected = selectedTab == "alerts",
                modifier = Modifier.weight(1f),
                onClick = onAlertsClick
            )

            SearchBottomItem(
                icon = R.drawable.ic_alert_list_subscription,
                description = "Subscriptions",
                selected = selectedTab == "subscriptions",
                modifier = Modifier.weight(1f),
                onClick = onSubscriptionsClick
            )

            SearchBottomItem(
                icon = R.drawable.ic_alert_list_bel,
                description = "Forward Alerts",
                selected = selectedTab == "forward",
                modifier = Modifier.weight(1f),
                onClick = onForwardAlertsClick
            )

            SearchBottomItem(
                icon = R.drawable.ic_alert_list_menu,
                description = "Menu",
                selected = selectedTab == "menu",
                modifier = Modifier.weight(1f),
                onClick = onMenuClick
            )
        }
    }
}

@Composable
private fun SearchBottomItem(
    icon: Int,
    description: String,
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
        Spacer(modifier = Modifier.height(4.dp))

        Image(
            painter = painterResource(id = icon),
            contentDescription = description,
            colorFilter = ColorFilter.tint(
                if (selected) SearchOrange else SearchGreen
            ),
            modifier = Modifier.size(24.dp)
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

// ============================================================
// PREVIEW
// ============================================================

// ============================================================
// PREVIEW
// ============================================================


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchAlertsScreenPreview() {
    XpertAlertsTheme {
        var keyword by remember { mutableStateOf("") }
        var startDate by remember { mutableStateOf("10/09/2026") }
        var endDate by remember { mutableStateOf("10/12/2026") }
        var viewBy by remember { mutableStateOf("- Select -") }
        var application by remember {
            mutableStateOf("- Select Application -")
        }
        var definition by remember {
            mutableStateOf("- Select Alert Definition -")
        }
        var selectedTab by remember { mutableStateOf("alerts") }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ScreenBackground,
            bottomBar = {
                SearchBottomNavigation(
                    selectedTab = selectedTab,
                    onAlertsClick = { selectedTab = "alerts" },
                    onSubscriptionsClick = {
                        selectedTab = "subscriptions"
                    },
                    onForwardAlertsClick = {
                        selectedTab = "forward"
                    },
                    onMenuClick = { selectedTab = "menu" }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(ScreenBackground)
            ) {
                // Toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SearchOrange)
                        .height(52.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            keyword = ""
                            startDate = ""
                            endDate = ""
                            viewBy = "- Select -"
                            application = "- Select Application -"
                            definition = "- Select Alert Definition -"
                        }
                    )
                }

                // Scrollable form
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 12.dp)
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
                                top = 18.dp
                            )
                            .height(45.dp)
                    )

                    FormLabel(
                        text = "Start Date",
                        modifier = Modifier.padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 18.dp
                        )
                    )

                    CompactDateField(
                        value = startDate,
                        onDateSelected = { startDate = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 8.dp
                            )
                            .height(45.dp)
                    )

                    FormLabel(
                        text = "End Date",
                        modifier = Modifier.padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 10.dp
                        )
                    )

                    CompactDateField(
                        value = endDate,
                        onDateSelected = { endDate = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 8.dp
                            )
                            .height(45.dp)
                    )

                    CompactDropdown(
                        selectedValue = viewBy,
                        placeholder = "- Select -",
                        options = listOf(
                            "- Select -",
                            "Alert Definition",
                            "Application"
                        ),
                        onSelected = { viewBy = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 10.dp
                            )
                            .height(45.dp)
                    )

                    CompactDropdown(
                        selectedValue = application,
                        placeholder = "- Select Application -",
                        options = listOf(
                            "- Select Application -",
                            "Preview Application"
                        ),
                        onSelected = { application = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 10.dp
                            )
                            .height(45.dp)
                    )

                    CompactDropdown(
                        selectedValue = definition,
                        placeholder = "- Select Alert Definition -",
                        options = listOf(
                            "- Select Alert Definition -",
                            "Preview Alert Definition"
                        ),
                        onSelected = { definition = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 10.dp
                            )
                            .height(45.dp)
                    )

                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 18.dp
                            )
                            .height(46.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SearchOrange,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Search",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
