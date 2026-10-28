package com.aquila.pocxpertalerts.ui.screens

import android.text.Html
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.data.model.AlertDto
import com.aquila.pocxpertalerts.data.repository.AlertDynamicAction
import com.aquila.pocxpertalerts.data.repository.AlertDynamicActionRepository
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.theme.AlertAccentColor
import com.aquila.pocxpertalerts.ui.theme.AlertCardBackground
import com.aquila.pocxpertalerts.ui.theme.AlertCardBorder
import com.aquila.pocxpertalerts.ui.theme.AlertSearchBorder
import com.aquila.pocxpertalerts.ui.theme.IconGreen
import com.aquila.pocxpertalerts.ui.theme.SelectedAlertColor
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.aquila.pocxpertalerts.ui.components.AppListSearchBar
import kotlinx.coroutines.launch
// ============================================================
// HOME SCREEN
// ============================================================

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onAlertClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSubscriptionsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onArchivedClick: () -> Unit,
    onForwardAlertsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onPasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAboutClick: () -> Unit,
    alerts: List<AlertDto> = emptyList(),
    userId: String = "",
    sessionId: String = "",
    isRefreshing: Boolean = false,
    alertsError: String? = null,
    onRefreshClick: () -> Unit = {},
    onAlertsChanged: (List<AlertDto>) -> Unit = {}
) {
    var showBottomMenu by rememberSaveable {
        mutableStateOf(false)
    }
    var showTopMenu by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val actionRepository = remember {
        AlertDynamicActionRepository()
    }
    var dynamicActions by remember {
        mutableStateOf<List<AlertDynamicAction>>(emptyList())
    }
    var isLoadingActions by remember {
        mutableStateOf(false)
    }
    var selectedDynamicAction by remember {
        mutableStateOf<AlertDynamicAction?>(null)
    }
    var singleActionAlert by remember {
        mutableStateOf<AlertDto?>(null)
    }
    var revealedActionAlertId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }
    var swipeActions by remember {
        mutableStateOf<List<AlertDynamicAction>>(emptyList())
    }
    var isLoadingSwipeActions by remember {
        mutableStateOf(false)
    }
    var confirmationMessage by remember {
        mutableStateOf<String?>(null)
    }
    var isExecutingAction by remember {
        mutableStateOf(false)
    }
    var showSelectionWarning by remember {
        mutableStateOf(false)
    }
    var showLogoutConfirmation by remember {
        mutableStateOf(false)
    }

    var selectedDestination by rememberSaveable {
        mutableStateOf("alerts")
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                selectedDestination = "alerts"
                showBottomMenu = false
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var selectedAlertIds by remember {
        mutableStateOf<Set<Long>>(emptySet())
    }

    val selectionMode = selectedAlertIds.isNotEmpty()

    val currentAlertIds = remember(alerts) {
        alerts.map { it.id }.toSet()
    }

    LaunchedEffect(currentAlertIds) {
        selectedAlertIds = selectedAlertIds.intersect(currentAlertIds)
    }

    fun toggleAlertSelection(alert: AlertDto) {
        if (alert.id in selectedAlertIds) {
            // Allow deselecting an already selected alert.
            selectedAlertIds = selectedAlertIds - alert.id
            return
        }

        // Check whether the newly selected alert has a different definition.
        val selectedAlerts = alerts.filter {
            it.id in selectedAlertIds
        }

        val hasDifferentTypeOrApplication = selectedAlerts.any {
            it.alertDefId != alert.alertDefId ||
                    it.applicationId != alert.applicationId
        }

        if (hasDifferentTypeOrApplication) {
            // Bulk actions must target alerts from the same application and definition.
            showSelectionWarning = true
            return
        }

        // Select the alert only if both its application and definition match.
        selectedAlertIds = selectedAlertIds + alert.id
    }

    fun selectAllAlerts() {
        showTopMenu = false

        if (alerts.isEmpty()) return

        val alertGroups = alerts
            .map { it.applicationId to it.alertDefId }
            .distinct()

        if (alertGroups.size > 1) {
            showSelectionWarning = true
            return
        }

        selectedAlertIds = alerts.map { it.id }.toSet()
    }

    BackHandler(
        enabled = selectionMode || showBottomMenu || showTopMenu
    ) {
        when {
            showTopMenu -> showTopMenu = false

            selectionMode -> selectedAlertIds = emptySet()

            showBottomMenu -> {
                showBottomMenu = false
                selectedDestination = "alerts"
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(White)
        ) {

            // Top toolbar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(XpertOrange)
            ) {
                HomeHeader(
                    selectionCount = selectedAlertIds.size,
                    menuExpanded = showTopMenu,
                    dynamicActions = dynamicActions,
                    isLoadingActions = isLoadingActions,
                    onTopMenuClick = {
                        showTopMenu = true
                        singleActionAlert = null
                        dynamicActions = emptyList()

                        val selectedAlerts = alerts.filter { it.id in selectedAlertIds }
                        val firstSelected = selectedAlerts.firstOrNull()
                        val sameDefinition = firstSelected != null &&
                                selectedAlerts.all {
                                    it.applicationId == firstSelected.applicationId &&
                                            it.alertDefId == firstSelected.alertDefId
                                }

                        if (
                            selectedAlerts.isNotEmpty() &&
                            sameDefinition &&
                            userId.isNotBlank() &&
                            sessionId.isNotBlank()
                        ) {
                            coroutineScope.launch {
                                isLoadingActions = true
                                try {
                                    val result = actionRepository.getActions(
                                        userId = userId,
                                        sessionId = sessionId,
                                        applicationId = firstSelected!!.applicationId,
                                        alertDefId = firstSelected.alertDefId
                                    )
                                    result.fold(
                                        onSuccess = { actions ->
                                            dynamicActions = actions.filter {
                                                it.caption.isNotBlank() &&
                                                        !it.caption.equals("null", ignoreCase = true)
                                            }
                                        },
                                        onFailure = { error ->
                                            dynamicActions = emptyList()
                                            Toast.makeText(
                                                context,
                                                error.message ?: "Unable to load alert actions.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    )
                                } finally {
                                    isLoadingActions = false
                                }
                            }
                        }
                    },
                    onTopMenuDismiss = {
                        showTopMenu = false
                    },
                    onSelectAllClick = {
                        selectAllAlerts()
                    },
                    onClearSelectionClick = {
                        selectedAlertIds = emptySet()
                        dynamicActions = emptyList()
                        showTopMenu = false
                    },
                    onActionClick = { action ->
                        showTopMenu = false
                        selectedDynamicAction = action
                        confirmationMessage = action.confirmationPrompt
                            .ifBlank { "Are you sure you want to ${action.caption.lowercase()} the selected alert(s)?" }
                    },
                    modifier = Modifier.statusBarsPadding()
                )
            }

            // Search bar and alert list
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefreshClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(White)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AlertsContent(
                        alerts = alerts,
                        selectedAlertIds = selectedAlertIds,
                        selectionMode = selectionMode,
                        onAlertClick = { alert ->
                            if (selectionMode) {
                                toggleAlertSelection(alert)
                            } else {
                                val selectedId =
                                    if (alert.alertId != 0) {
                                        alert.alertId.toString()
                                    } else {
                                        alert.id.toString()
                                    }

                                onAlertClick(selectedId)
                            }
                        },
                        onAlertLongClick = { alert ->
                            toggleAlertSelection(alert)
                        },
                        revealedActionAlertId = revealedActionAlertId,
                        swipeActions = swipeActions,
                        isLoadingSwipeActions = isLoadingSwipeActions,
                        onSwipeReveal = { alert ->
                            if (revealedActionAlertId == alert.id) {
                                revealedActionAlertId = null
                                swipeActions = emptyList()
                            } else {
                                revealedActionAlertId = alert.id
                                swipeActions = emptyList()
                                if (userId.isBlank() || sessionId.isBlank()) {
                                    Toast.makeText(context, "Your session has expired. Please sign in again.", Toast.LENGTH_LONG).show()
                                } else {
                                    coroutineScope.launch {
                                        isLoadingSwipeActions = true
                                        try {
                                            val result = actionRepository.getActions(
                                                userId = userId,
                                                sessionId = sessionId,
                                                applicationId = alert.applicationId,
                                                alertDefId = alert.alertDefId
                                            )
                                            result.fold(
                                                onSuccess = { actions ->
                                                    if (revealedActionAlertId == alert.id) {
                                                        swipeActions = actions.filter { it.caption.isNotBlank() }
                                                        if (swipeActions.isEmpty()) {
                                                            Toast.makeText(context, "There are no actions for this alert.", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                },
                                                onFailure = { error ->
                                                    swipeActions = emptyList()
                                                    Toast.makeText(context, error.message ?: "Unable to load alert actions.", Toast.LENGTH_LONG).show()
                                                }
                                            )
                                        } finally {
                                            isLoadingSwipeActions = false
                                        }
                                    }
                                }
                            }
                        },
                        onSwipeActionClick = { alert, action ->
                            singleActionAlert = alert
                            selectedDynamicAction = action
                            confirmationMessage = action.confirmationPrompt
                                .takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
                                ?: "Are you sure you want to ${action.caption} ?"
                        },
                        onSwipeMoreClick = { alert ->
                            val selectedId = if (alert.alertId != 0) alert.alertId.toString() else alert.id.toString()
                            onAlertClick(selectedId)
                        },
                        onSearchClick = onSearchClick
                    )

                    // Keep existing alerts visible if refresh fails and allow a retry.
                    if (!alertsError.isNullOrBlank()) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = AlertCardBackground,
                            border = BorderStroke(1.dp, AlertCardBorder),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = alertsError,
                                    modifier = Modifier.weight(1f),
                                    color = TextDark,
                                    fontSize = 12.sp
                                )
                                TextButton(onClick = onRefreshClick) {
                                    Text(text = "Retry", color = AlertAccentColor)
                                }
                            }
                        }
                    }
                }
            }

            // Bottom menu
            if (showBottomMenu) {
                HomeMenu(
                    selectedDestination = selectedDestination,
                    onArchivedClick = {
                        showBottomMenu = false
                        selectedDestination = "archived"
                        onArchivedClick()
                    },
                    onAboutClick = {
                        showBottomMenu = false
                        selectedDestination = "about"
                        onAboutClick()
                    },
                    onPasswordClick = {
                        showBottomMenu = false
                        selectedDestination = "password"
                        onPasswordClick()
                    },
                    onLogoutClick = {
                        showBottomMenu = false
                        showLogoutConfirmation = true
                    },
                    onForwardAlertsClick = {
                        showBottomMenu = false
                        selectedDestination = "forward"
                        onForwardAlertsClick()
                    },
                    onProfileClick = {
                        showBottomMenu = false
                        selectedDestination = "profile"
                        onProfileClick()
                    }
                )
            }

            // Bottom navigation
            BottomNavigationBar(
                selectedDestination = selectedDestination,
                onAlertsClick = {
                    selectedDestination = "alerts"
                    showBottomMenu = false
                    selectedAlertIds = emptySet()
                },
                onSubscriptionsClick = {
                    selectedDestination = "subscriptions"
                    showBottomMenu = false
                    onSubscriptionsClick()
                },
                onForwardAlertsClick = {
                    selectedDestination = "forward"
                    showBottomMenu = false
                    onForwardAlertsClick()
                },
                onMenuClick = {
                    showBottomMenu = !showBottomMenu
                    selectedDestination =
                        if (showBottomMenu) "menu" else "alerts"
                }
            )
        }
    }

    // Warning when alerts have different definitions
    if (showSelectionWarning) {
        AlertDialog(
            onDismissRequest = {
                showSelectionWarning = false
            },
            title = {
                Text("Cannot select all alerts")
            },
            text = {
                Text(
                    "Different type of alerts cannot be selected at once."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSelectionWarning = false
                    }
                ) {
                    Text("OK", color = XpertOrange)
                }
            }
        )
    }

    // Confirm and execute the server-provided action for the selected alert(s).
    val pendingAction = selectedDynamicAction
    val pendingPrompt = confirmationMessage
    if (pendingAction != null && pendingPrompt != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isExecutingAction) {
                    selectedDynamicAction = null
                    singleActionAlert = null
                    confirmationMessage = null
                }
            },
            title = { Text(pendingAction.caption) },
            text = { Text(pendingPrompt) },
            confirmButton = {
                TextButton(
                    enabled = !isExecutingAction,
                    onClick = {
                        val alertsToProcess = singleActionAlert?.let { listOf(it) }
                            ?: alerts.filter { it.id in selectedAlertIds }
                        if (alertsToProcess.isEmpty()) {
                            selectedDynamicAction = null
                            singleActionAlert = null
                            confirmationMessage = null
                            return@TextButton
                        }

                        coroutineScope.launch {
                            isExecutingAction = true
                            val successfulIds = mutableSetOf<Long>()
                            var failureMessage: String? = null

                            try {
                                val result = actionRepository.executeActionForAlerts(
                                    userId = userId,
                                    sessionId = sessionId,
                                    alerts = alertsToProcess,
                                    action = pendingAction
                                )
                                result.fold(
                                    onSuccess = { successfulIds.addAll(alertsToProcess.map { it.id }) },
                                    onFailure = { error ->
                                        failureMessage = error.message ?: "Unable to process the selected alert(s)."
                                    }
                                )

                                if (successfulIds.isNotEmpty()) {
                                    val updatedAlerts = alerts.filterNot { it.id in successfulIds }
                                    onAlertsChanged(updatedAlerts)
                                    selectedAlertIds = selectedAlertIds - successfulIds
                                }

                                if (failureMessage == null) {
                                    Toast.makeText(
                                        context,
                                        "${pendingAction.caption} completed successfully.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    selectedAlertIds = emptySet()
                                    selectedDynamicAction = null
                                    singleActionAlert = null
                                    confirmationMessage = null
                                    revealedActionAlertId = null
                                    swipeActions = emptyList()
                                    dynamicActions = emptyList()
                                    showTopMenu = false
                                } else {
                                    Toast.makeText(
                                        context,
                                        failureMessage ?: "Unable to process the selected alert(s).",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    // Keep the dialog open so the user can retry or cancel.
                                }
                            } finally {
                                isExecutingAction = false
                            }
                        }
                    }
                ) {
                    Text(if (isExecutingAction) "Processing..." else "OK", color = XpertOrange)
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isExecutingAction,
                    onClick = {
                        selectedDynamicAction = null
                        confirmationMessage = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Logout confirmation
    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showLogoutConfirmation = false
            },
            title = {
                Text("Logout")
            },
            text = {
                Text("Are you sure you want to log out?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirmation = false
                        selectedAlertIds = emptySet()
                        onLogoutClick()
                    }
                ) {
                    Text("OK", color = XpertOrange)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirmation = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ============================================================
// TOP TOOLBAR AND THREE-DOT MENU
// ============================================================

@Composable
private fun HomeHeader(
    selectionCount: Int,
    menuExpanded: Boolean,
    dynamicActions: List<AlertDynamicAction>,
    isLoadingActions: Boolean,
    onTopMenuClick: () -> Unit,
    onTopMenuDismiss: () -> Unit,
    onSelectAllClick: () -> Unit,
    onClearSelectionClick: () -> Unit,
    onActionClick: (AlertDynamicAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(XpertOrange)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcherbell),
                contentDescription = "Alerts logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(30.dp)
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (selectionCount > 0) "$selectionCount selected" else "Alerts List",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box {
            androidx.compose.material3.IconButton(onClick = onTopMenuClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = White
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = onTopMenuDismiss
            ) {
                if (dynamicActions.isNotEmpty()) {
                    dynamicActions.forEach { action ->
                        DropdownMenuItem(
                            text = { Text(action.caption) },
                            onClick = { onActionClick(action) }
                        )
                    }

                } else {
                    DropdownMenuItem(
                        text = { Text("Select All") },
                        onClick = onSelectAllClick
                    )

                }
            }
        }
    }
}

// ============================================================
// SEARCH BAR AND ALERT LIST
// ============================================================

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun AlertsContent(
    alerts: List<AlertDto>,
    selectedAlertIds: Set<Long>,
    selectionMode: Boolean,
    onAlertClick: (AlertDto) -> Unit,
    onAlertLongClick: (AlertDto) -> Unit,
    revealedActionAlertId: Long?,
    swipeActions: List<AlertDynamicAction>,
    isLoadingSwipeActions: Boolean,
    onSwipeReveal: (AlertDto) -> Unit,
    onSwipeActionClick: (AlertDto, AlertDynamicAction) -> Unit,
    onSwipeMoreClick: (AlertDto) -> Unit,
    onSearchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // Shared search bar — same style as Subscriptions.
        AppListSearchBar(
            placeholder = "Search Alerts",
            onClick = onSearchClick,
            modifier = Modifier.padding(
                start = 15.dp,
                end = 15.dp,
                top = 14.dp,
                bottom = 8.dp
            )
        )

        if (alerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No records found",
                    color = TextGray,
                    fontSize = 14.sp
                )
            }
        } else {
            val groupedAlerts = remember(alerts) {
                alerts.groupBy { alert ->
                    formatGroupDate(alert.groupBy)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                groupedAlerts.forEach { (date, dateAlerts) ->
                    item(key = "date_$date") {
                        DateHeader(date)
                    }

                    items(
                        items = dateAlerts,
                        key = {
                            "${it.id}_${it.alertSendDate}"
                        }
                    ) { alert ->
                        AlertCard(
                            alert = alert,
                            selected = alert.id in selectedAlertIds,
                            selectionMode = selectionMode,
                            onClick = {
                                onAlertClick(alert)
                            },
                            onLongClick = {
                                onAlertLongClick(alert)
                            },
                            actionsRevealed = revealedActionAlertId == alert.id,
                            swipeActions = if (revealedActionAlertId == alert.id) swipeActions else emptyList(),
                            isLoadingSwipeActions = revealedActionAlertId == alert.id && isLoadingSwipeActions,
                            onSwipeReveal = { onSwipeReveal(alert) },
                            onSwipeActionClick = { action -> onSwipeActionClick(alert, action) },
                            onSwipeMoreClick = { onSwipeMoreClick(alert) }
                        )
                    }
                }
            }
        }
    }
}
// ============================================================
// DATE HEADER
// ============================================================

@Composable
private fun DateHeader(date: String) {
    Box(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 6.dp)
            .background(
                color = XpertOrange,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(
                horizontal = 22.dp,
                vertical = 6.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date,
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ============================================================
// RESPONSIVE ALERT CARD
// ============================================================

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun AlertCard(
    alert: AlertDto,
    selected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    actionsRevealed: Boolean,
    swipeActions: List<AlertDynamicAction>,
    isLoadingSwipeActions: Boolean,
    onSwipeReveal: () -> Unit,
    onSwipeActionClick: (AlertDynamicAction) -> Unit,
    onSwipeMoreClick: () -> Unit
) {
    val (displayDate, displayTime) = remember(alert.alertSendDate) {
        formatAlertDate(alert.alertSendDate)
    }

    val title = remember(alert.subject, alert.id, alert.alertId) {
        cleanAlertDescription(
            message = alert.subject,
            subject = null,
            id = alert.id
        ).ifBlank {
            "Alert ${alert.alertId}"
        }
    }

    val description = remember(
        alert.message,
        alert.subject,
        alert.id
    ) {
        cleanAlertDescription(
            message = alert.message,
            subject = alert.subject,
            id = alert.id
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(alert.id, actionsRevealed) {
                    var totalDrag = 0f
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount -> totalDrag += dragAmount },
                        onDragEnd = {
                            if (totalDrag < -48f) onSwipeReveal()
                            totalDrag = 0f
                        },
                        onDragCancel = { totalDrag = 0f }
                    )
                }
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                ),
            shape = RoundedCornerShape(12.dp),
            color = if (selected) {
                SelectedAlertColor
            } else {
                AlertCardBackground
            },
            border = BorderStroke(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) {
                    AlertAccentColor
                } else {
                    AlertCardBorder
                }
            ),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Date and time
                Column(
                    modifier = Modifier.width(66.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = displayDate,
                        color = TextDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = displayTime,
                        color = TextGray,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Orange accent line
                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(3.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AlertAccentColor)
                )

                // Alert information
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        color = TextDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )


                    if (
                        description.isNotBlank() &&
                        description != title
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = description,
                            color = TextGray,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }



                }

                // Selection icon
                if (selectionMode) {
                    Icon(
                        imageVector = if (selected) {
                            Icons.Default.CheckCircle
                        } else {
                            Icons.Default.RadioButtonUnchecked
                        },
                        contentDescription = if (selected) {
                            "Selected"
                        } else {
                            "Not selected"
                        },
                        tint = if (selected) {
                            AlertAccentColor
                        } else {
                            IconGreen
                        },
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
        if (actionsRevealed) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoadingSwipeActions) {
                    Text("Loading actions…", color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                } else if (swipeActions.isNotEmpty()) {
                    swipeActions.take(2).forEachIndexed { index, action ->
                        TextButton(onClick = { onSwipeActionClick(action) }) {
                            Text(action.caption, color = if (index == 0) IconGreen else XpertOrange, maxLines = 1)
                        }
                    }
                    if (swipeActions.size > 2) {
                        TextButton(onClick = onSwipeMoreClick) {
                            Text("More", color = XpertOrange)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// CLEAN ALERT DESCRIPTION
// ============================================================

private fun cleanAlertDescription(
    message: String?,
    subject: String?,
    id: Long
): String {
    val source = message
        ?.takeIf { it.isNotBlank() }
        ?: subject
        ?: return "Alert #$id"

    return Html.fromHtml(
        source,
        Html.FROM_HTML_MODE_LEGACY
    ).toString()
        .replace(Regex("\\s+"), " ")
        .trim()
        .ifBlank {
            "Alert #$id"
        }
}

// ============================================================
// DATE FORMATTING
// ============================================================

private fun formatGroupDate(rawDate: String?): String {
    if (rawDate.isNullOrBlank()) {
        return "Unknown date"
    }

    val patterns = listOf(
        "MM/dd/yyyy",
        "dd/MM/yyyy",
        "yyyy-MM-dd"
    )

    for (pattern in patterns) {
        try {
            val parser = SimpleDateFormat(
                pattern,
                Locale.US
            ).apply {
                isLenient = false
            }

            val parsed = parser.parse(rawDate) ?: continue

            return SimpleDateFormat(
                "dd MMM",
                Locale.US
            ).format(parsed)

        } catch (_: ParseException) {
            // Try the next supported format.
        }
    }

    return rawDate
}

private fun formatAlertDate(
    rawDate: String?
): Pair<String, String> {
    if (rawDate.isNullOrBlank()) {
        return "Unknown date" to ""
    }

    val patterns = listOf(
        "MM/dd/yyyy hh:mm a",
        "MM/dd/yyyy HH:mm:ss",
        "MM/dd/yyyy HH:mm",
        "dd/MM/yyyy hh:mm a",
        "dd/MM/yyyy HH:mm:ss",
        "dd/MM/yyyy HH:mm",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm"
    )

    for (pattern in patterns) {
        try {
            val parser = SimpleDateFormat(
                pattern,
                Locale.US
            ).apply {
                isLenient = false
            }

            val date: Date = parser.parse(rawDate) ?: continue

            val dateText = SimpleDateFormat(
                "dd MMM",
                Locale.US
            ).format(date)

            val timeText = SimpleDateFormat(
                "hh:mm a",
                Locale.US
            ).format(date)

            return dateText to timeText

        } catch (_: ParseException) {
            // Try the next supported format.
        }
    }

    return rawDate to ""
}

// ============================================================
// EXPANDED BOTTOM MENU
// ============================================================

@Composable
private fun HomeMenu(
    onArchivedClick: () -> Unit,
    onForwardAlertsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onPasswordClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit,
    selectedDestination: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
    ) {
        HorizontalDivider(
            color = TextGray.copy(alpha = 0.25f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MenuItem(
                icon = R.drawable.archivedalerts,
                text = "Archived",
                selected = selectedDestination == "archived",
                modifier = Modifier.weight(1f),
                onClick = onArchivedClick
            )

            MenuItem(
                icon = null,
                vectorIcon = Icons.Default.Info,
                text = "About",
                selected = selectedDestination == "about",
                modifier = Modifier.weight(1f),
                onClick = onAboutClick
            )

            MenuItem(
                icon = R.drawable.changepassword,
                text = "Password Change",
                selected = selectedDestination == "password",
                modifier = Modifier.weight(1f),
                onClick = onPasswordClick
            )

            MenuItem(
                icon = R.drawable.new_logout,
                text = "Logout",
                selected = selectedDestination == "logout",
                modifier = Modifier.weight(1f),
                onClick = onLogoutClick
            )
        }

        HorizontalDivider(
            color = TextGray.copy(alpha = 0.25f)
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
        )
    }
}

// ============================================================
// MENU ITEM
// ============================================================

@Composable
private fun MenuItem(
    icon: Int?,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    vectorIcon: ImageVector? = null,
    selected: Boolean = false
) {
    val iconColor = if (selected) {
        XpertOrange
    } else {
        IconGreen
    }

    Column(
        modifier = modifier
            .height(66.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (vectorIcon != null) {
            Icon(
                imageVector = vectorIcon,
                contentDescription = text,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        } else if (icon != null) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = text,
                colorFilter = ColorFilter.tint(iconColor),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = text,
            fontSize = 10.sp,
            color = if (selected) XpertOrange else TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ============================================================
// BOTTOM NAVIGATION
// ============================================================

@Composable
private fun BottomNavigationBar(
    selectedDestination: String,
    onAlertsClick: () -> Unit,
    onSubscriptionsClick: () -> Unit,
    onForwardAlertsClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(44.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTab(
                icon = R.drawable.ic_alert_list_bell,
                selected = selectedDestination == "alerts",
                description = "Alerts",
                modifier = Modifier.weight(1f),
                onClick = onAlertsClick
            )

            BottomTab(
                icon = R.drawable.ic_alert_list_subscription,
                selected = selectedDestination == "subscriptions",
                description = "Subscriptions",
                modifier = Modifier.weight(1f),
                onClick = onSubscriptionsClick
            )

            BottomTab(
                icon = R.drawable.ic_alert_list_bel,
                selected = selectedDestination == "forward",
                description = "Forward Alerts",
                modifier = Modifier.weight(1f),
                onClick = onForwardAlertsClick
            )

            BottomTab(
                icon = R.drawable.ic_alert_list_menu,
                selected = selectedDestination == "menu",
                description = "Menu",
                modifier = Modifier.weight(1f),
                onClick = onMenuClick
            )
        }
    }
}

// ============================================================
// BOTTOM TAB
// ============================================================

@Composable
private fun BottomTab(
    icon: Int,
    selected: Boolean,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = if (selected) {
        XpertOrange
    } else {
        IconGreen
    }

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
            colorFilter = ColorFilter.tint(iconColor),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .height(2.dp)
                .background(
                    if (selected) XpertOrange else White
                )
        )
    }
}

// ============================================================
// PREVIEW
// ============================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    XpertAlertsTheme {
        HomeScreen(
            onAlertClick = {},
            onSearchClick = {},
            onSubscriptionsClick = {},
            onNotificationsClick = {},
            onArchivedClick = {},
            onForwardAlertsClick = {},
            onProfileClick = {},
            onPasswordClick = {},
            onLogoutClick = {},
            onAboutClick = {},
            alerts = emptyList()
        )
    }
}
