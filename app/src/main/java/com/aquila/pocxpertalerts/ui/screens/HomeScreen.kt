
package com.aquila.pocxpertalerts.ui.screens

import android.text.Html
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.data.model.AlertDto
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.theme.IconGreen
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner


private val LightOrange = Color(0xFFFFF1E8)
private val SelectedAlertColor = Color(0xFFFFF4E9)

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
    isLoading: Boolean = false,

) {
    var showBottomMenu by rememberSaveable { mutableStateOf(false) }
    var showTopMenu by remember { mutableStateOf(false) }
    var showSelectionWarning by remember { mutableStateOf(false) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }

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

    // Remove selections that are no longer present in the results.
    val currentAlertIds = remember(alerts) {
        alerts.map { it.id }.toSet()
    }

    androidx.compose.runtime.LaunchedEffect(currentAlertIds) {
        selectedAlertIds = selectedAlertIds.intersect(currentAlertIds)
    }

    fun toggleAlertSelection(alert: AlertDto) {
        selectedAlertIds = if (alert.id in selectedAlertIds) {
            selectedAlertIds - alert.id
        } else {
            selectedAlertIds + alert.id
        }
    }

    fun selectAllAlerts() {
        showTopMenu = false

        if (alerts.isEmpty()) return

        val definitionIds = alerts.map { it.alertDefId }.distinct()

        if (definitionIds.size > 1) {
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

            // TOP ORANGE TOOLBAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(XpertOrange)
            ) {
                HomeHeader(
                    selectionCount = selectedAlertIds.size,
                    onTopMenuClick = {
                        showTopMenu = true
                    },
                    onSelectAllClick = {
                        selectAllAlerts()
                    },
                    onClearSelectionClick = {
                        selectedAlertIds = emptySet()
                        showTopMenu = false
                    },
                    modifier = Modifier.statusBarsPadding()
                )
            }

            // SEARCH BAR AND ALERT LIST
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(White)
            ) {
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
                    onSearchClick = onSearchClick
                )
                // Show the spinner on Home while the API is searching.
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(42.dp),
                        color = XpertOrange,
                        strokeWidth = 4.dp
                    )
                }
            }

            // BOTTOM MENU PANEL
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

            // BOTTOM NAVIGATION
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
                    selectedDestination = "menu"
                }
            )
        }
    }

    // Mixed alert-definition warning
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
    onTopMenuClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onClearSelectionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

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
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.ic_launcherbell
                ),
                contentDescription = "Alerts logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(30.dp)
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (selectionCount > 0) {
                    "$selectionCount selected"
                } else {
                    "Alerts List"
                },
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Box {
            androidx.compose.material3.IconButton(
                onClick = {
                    showMenu = true
                    onTopMenuClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = White
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = {
                    showMenu = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("Select All")
                    },
                    onClick = {
                        showMenu = false
                        onSelectAllClick()
                    }
                )

                if (selectionCount > 0) {
                    DropdownMenuItem(
                        text = {
                            Text("Clear Selection")
                        },
                        onClick = {
                            showMenu = false
                            onClearSelectionClick()
                        }
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
    onSearchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // The complete Search Alerts field is clickable.
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 18.dp,
                    bottom = 14.dp
                )
                .height(54.dp)
                .clickable(onClick = onSearchClick),
            shape = RoundedCornerShape(28.dp),
            color = White,
            border = BorderStroke(1.5.dp, Color.Gray)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Search Alerts",
                    modifier = Modifier.weight(1f),
                    color = Color.Gray,
                    fontSize = 18.sp
                )

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Alerts",
                    tint = Color.Gray,
                    modifier = Modifier.size(25.dp)
                )
            }
        }

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
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                groupedAlerts.forEach { (date, dateAlerts) ->

                    item(key = "date_$date") {
                        DateHeader(date)
                    }

                    items(
                        items = dateAlerts,
                        key = { "${it.id}_${it.alertSendDate}" }
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
                            }
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
            .padding(horizontal = 22.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date,
            color = White,
            fontSize = 15.sp
        )
    }
}

// ============================================================
// ALERT CARD
// ============================================================

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun AlertCard(
    alert: AlertDto,
    selected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val (displayDate, displayTime) = remember(alert.alertSendDate) {
        formatAlertDate(alert.alertSendDate)
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

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(3.dp),
        color = if (selected) {
            SelectedAlertColor
        } else {
            Color(0xFFF4F4F4)
        },
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) XpertOrange else Color(0xFFE4E4E4)
        ),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(82.dp)
                    .padding(horizontal = 5.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = displayDate,
                    color = Color.Gray,
                    fontSize = 13.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = displayTime,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp)
                    .background(XpertOrange)
            )

            Text(
                text = description,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                color = Color.Gray,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

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
                    tint = if (selected) XpertOrange else IconGreen,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(22.dp)
                )
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
        .ifBlank { "Alert #$id" }
}

// ============================================================
// DATE FORMATTING
// ============================================================

private fun formatGroupDate(rawDate: String?): String {
    if (rawDate.isNullOrBlank()) return "Unknown date"

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
            // Try the next format.
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

        // First row: Archived, About, Password Change, Logout
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

        // Second row intentionally empty.
        // Forward Alerts and Profile are removed.
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
    val iconColor = if (selected) XpertOrange else IconGreen

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
            maxLines = 1
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
    val iconColor = if (selected) XpertOrange else IconGreen

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
