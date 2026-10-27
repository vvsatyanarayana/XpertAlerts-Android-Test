package com.aquila.pocxpertalerts.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.data.local.SearchAlertsPersistence
import com.aquila.pocxpertalerts.data.local.SavedAlertSearchFilters
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.components.AppTopBar
import com.aquila.pocxpertalerts.ui.components.AppSearchTextField
import com.aquila.pocxpertalerts.ui.components.AppSearchDropdown
import com.aquila.pocxpertalerts.ui.theme.SearchScreenBackground
import com.aquila.pocxpertalerts.ui.theme.SearchToolbarColor
import com.aquila.pocxpertalerts.ui.theme.SearchTextColor
import com.aquila.pocxpertalerts.ui.theme.PlaceholderColor
import com.aquila.pocxpertalerts.ui.theme.SearchBorderColor
import com.aquila.pocxpertalerts.ui.theme.SearchFieldBackground
import com.aquila.pocxpertalerts.viewmodel.AlertsViewModel
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.layout.heightIn
import com.aquila.pocxpertalerts.ui.components.SearchScreenTopBar

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

    // Restore saved filters for the current user.
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

    // Persist form changes for later use.
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

    fun resetSearch() {
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SearchScreenBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column {
                SearchScreenTopBar(
                    title = "Search Alerts",
                    onBackClick = onBackClick,
                    onResetClick = { resetSearch() }
                )
            }
        },
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
                .background(SearchScreenBackground)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 18.dp,
                    vertical = 18.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ------------------------------------------------
            // KEYWORD
            // ------------------------------------------------

            Column {
                SearchFieldLabel("Search By")

                AppSearchTextField(
                    value = keyword,
                    onValueChange = { keyword = it },
                    placeholder = "Enter keyword",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ------------------------------------------------
            // START DATE
            // ------------------------------------------------

            SearchDateField(
                label = "Start Date",
                value = startDate,
                onDateSelected = { startDate = it }
            )

            // ------------------------------------------------
            // END DATE
            // ------------------------------------------------

            SearchDateField(
                label = "End Date",
                value = endDate,
                onDateSelected = { endDate = it }
            )

            // ------------------------------------------------
            // VIEW BY
            // ------------------------------------------------

            AppSearchDropdown(
                label = "View By",
                selectedValue = viewBy.takeUnless {
                    it == "- Select -"
                }.orEmpty(),
                placeholder = "Select View By",
                options = listOf(
                    "- Select -",
                    "Alert Definition",
                    "Application"
                ),
                onValueSelected = { selected ->
                    viewBy = selected
                }
            )

            // ------------------------------------------------
            // APPLICATION
            // ------------------------------------------------

            AppSearchDropdown(
                label = "Application",
                selectedValue = applicationName.takeUnless {
                    it == "- Select Application -"
                }.orEmpty(),
                placeholder = if (isLoadingApplications) {
                    "Loading applications..."
                } else {
                    "Select Application"
                },
                options = listOf("- Select Application -") +
                        applications.map { it.name },
                enabled = !isLoadingApplications,
                onValueSelected = { selected ->
                    val application = applications.firstOrNull {
                        it.name == selected
                    }

                    applicationName =
                        application?.name ?: "- Select Application -"

                    applicationId = application?.id.orEmpty()

                    // Reset definition whenever the application changes.
                    alertDefinitionName = "- Select Alert Definition -"
                    alertDefinitionId = ""

                    alertsViewModel.loadAlertDefinitions(
                        applicationId = applicationId,
                        sessionId = sessionId
                    )
                }
            )

            if (isLoadingApplications) {
                LoadingRow(message = "Loading applications...")
            }

            // ------------------------------------------------
            // ALERT DEFINITION
            // ------------------------------------------------

            AppSearchDropdown(
                label = "Alert Definition",
                selectedValue = alertDefinitionName.takeUnless {
                    it == "- Select Alert Definition -"
                }.orEmpty(),
                placeholder = if (isLoadingDefinitions) {
                    "Loading alert definitions..."
                } else {
                    "Select Alert Definition"
                },
                options = listOf("- Select Alert Definition -") +
                        if (applicationId.isBlank()) {
                            emptyList()
                        } else {
                            alertDefinitions.map { it.name }
                        },
                enabled = !isLoadingDefinitions &&
                        applicationId.isNotBlank(),
                onValueSelected = { selected ->
                    val definition = alertDefinitions.firstOrNull {
                        it.name == selected
                    }

                    alertDefinitionName =
                        definition?.name ?: "- Select Alert Definition -"

                    alertDefinitionId = definition?.id.orEmpty()
                }
            )

            if (isLoadingDefinitions) {
                LoadingRow(message = "Loading alert definitions...")
            }

            // ------------------------------------------------
            // DROPDOWN ERROR
            // ------------------------------------------------

            if (!dropdownErrorMessage.isNullOrBlank()) {
                Text(
                    text = dropdownErrorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            // ------------------------------------------------
            // SEARCH BUTTON
            // ------------------------------------------------

            Button(
                onClick = {
                    onSearchClick(
                        startDate,
                        endDate,
                        keyword.trim(),
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
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SearchToolbarColor,
                    contentColor = Color.White,
                    disabledContainerColor =
                        SearchToolbarColor.copy(alpha = 0.5f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = if (isLoading) "Searching..." else "Search",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // ------------------------------------------------
            // SEARCH ERROR
            // ------------------------------------------------

            if (!isLoading && !errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ============================================================
// FORM LABEL
// ============================================================

@Composable
private fun SearchFieldLabel(
    text: String
) {
    Text(
        text = text,
        color = SearchTextColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

// ============================================================
// DATE FIELD
// ============================================================

@Composable
private fun SearchDateField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current

    Column {
        SearchFieldLabel(label)

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    val calendar = Calendar.getInstance()

                    // Existing date format is MM/dd/yyyy.
                    val parts = value.split("/")

                    if (parts.size == 3) {
                        val first = parts[0].toIntOrNull()
                        val second = parts[1].toIntOrNull()
                        val year = parts[2].toIntOrNull()

                        if (
                            first != null &&
                            second != null &&
                            year != null
                        ) {
                            calendar.set(
                                year,
                                first - 1,
                                second
                            )
                        }
                    }

                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val selectedDate = String.format(
                                Locale.US,
                                "%02d/%02d/%04d",
                                month + 1,
                                day,
                                year
                            )

                            onDateSelected(selectedDate)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
            shape = RoundedCornerShape(14.dp),
            color = SearchFieldBackground,
            border = BorderStroke(
                1.dp,
                SearchBorderColor
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value.ifBlank { "Select date" },
                    modifier = Modifier.weight(1f),
                    color = if (value.isBlank()) {
                        PlaceholderColor
                    } else {
                        SearchTextColor
                    },
                    fontSize = 14.sp
                )

                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Choose $label",
                    tint = SearchToolbarColor
                )
            }
        }
    }
}

// ============================================================
// LOADING ROW
// ============================================================

@Composable
private fun LoadingRow(
    message: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            color = SearchToolbarColor,
            strokeWidth = 2.dp
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = message,
            color = PlaceholderColor,
            fontSize = 12.sp
        )
    }
}

// ============================================================
// BOTTOM NAVIGATION
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
        color = SearchFieldBackground,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(48.dp),
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

// ============================================================
// BOTTOM NAVIGATION ITEM
// ============================================================

@Composable
private fun SearchBottomItem(
    icon: Int,
    description: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val tint = if (selected) {
        SearchToolbarColor
    } else {
        com.aquila.pocxpertalerts.ui.theme.IconGreen
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(5.dp))

        Image(
            painter = painterResource(id = icon),
            contentDescription = description,
            colorFilter = ColorFilter.tint(tint),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(2.dp)
                .background(
                    if (selected) SearchToolbarColor
                    else SearchFieldBackground
                )
        )
    }
}
@Preview(
    name = "Search Alerts Screen",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=852dp"
)
@Composable
private fun SearchAlertsScreenPreview() {
    XpertAlertsTheme {
        SearchAlertsScreen(
            onBackClick = {},
            onSearchClick = { _, _, _, _, _, _ -> },
            alertsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
            userId = "preview_user",
            userTypeId = "1",
            sessionId = "preview_session",
            onAlertsTabClick = {},
            onSubscriptionsTabClick = {},
            onForwardAlertsTabClick = {},
            onMenuTabClick = {}
        )
    }
}
