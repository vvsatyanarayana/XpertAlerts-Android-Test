
package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
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

// ============================================================
// HOME SCREEN
// ============================================================

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
    alerts: List<AlertDto> = emptyList()
) {
    var showMenu by remember { mutableStateOf(false) }

    var selectedDestination by remember {
        mutableStateOf("alerts")
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(XpertOrange)
            ) {
                HomeHeader(
                    onMenuClick = {
                        showMenu = !showMenu
                    },
                    modifier = Modifier.statusBarsPadding()
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(White)
            ) {
                android.util.Log.d(
                    "HomeAlertsUI",
                    "Alerts received by Home UI: ${alerts.size}"
                )
                AlertsContent(
                    alerts = alerts,
                    onAlertClick = onAlertClick,
                    onSearchClick = onSearchClick
                )
            }

            if (showMenu) {
                HomeMenu(
                    selectedDestination = selectedDestination,
                    onArchivedClick = {
                        selectedDestination = "archived"
                        showMenu = false
                        onArchivedClick()
                    },
                    onAboutClick = {
                        selectedDestination = "about"
                        showMenu = false
                        onAboutClick()
                    },
                    onPasswordClick = {
                        selectedDestination = "password"
                        showMenu = false
                        onPasswordClick()
                    },
                    onLogoutClick = {
                        selectedDestination = "logout"
                        showMenu = false
                        onLogoutClick()
                    },
                    onForwardAlertsClick = {
                        selectedDestination = "forward"
                        showMenu = false
                        onForwardAlertsClick()
                    },
                    onProfileClick = {
                        selectedDestination = "profile"
                        showMenu = false
                        onProfileClick()
                    }
                )
            }

            BottomNavigationBar(
                menuSelected = showMenu,
                selectedDestination = selectedDestination,
                onDestinationSelected = {
                    selectedDestination = it
                },
                onSubscriptionsClick = onSubscriptionsClick,
                onNotificationsClick = onNotificationsClick,
                onMenuClick = {
                    showMenu = !showMenu
                }
            )
        }
    }
}

// ============================================================
// TOOLBAR
// ============================================================

@Composable
private fun HomeHeader(
    onMenuClick: () -> Unit,
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
        Image(
            painter = painterResource(
                id = R.drawable.ic_alert_list_bel
            ),
            contentDescription = "Alerts logo",
            modifier = Modifier
                .size(42.dp)
                .background(White, CircleShape)
                .padding(4.dp)
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Alerts List",
                color = White,
                fontSize = 20.sp
            )
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clickable(onClick = onMenuClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⋮",
                color = White,
                fontSize = 26.sp
            )
        }
    }
}

// ============================================================
// ALERT CONTENT
// ============================================================

@Composable
fun AlertsContent(
    alerts: List<AlertDto>,
    onAlertClick: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // Search bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 18.dp
                )
                .height(54.dp)
                .clickable(onClick = onSearchClick),
            shape = RoundedCornerShape(30.dp),
            color = White,
            border = BorderStroke(
                width = 1.5.dp,
                color = Color.Gray
            )
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
                    color = Color.DarkGray,
                    fontSize = 22.sp
                )

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Alerts",
                    tint = Color.Gray,
                    modifier = Modifier.size(30.dp)
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
            // Group real API alerts by the date field.
            val groupedAlerts = remember(alerts) {
                alerts.groupBy { alert ->
                    formatAlertDate(alert.alertSendDate).first
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                groupedAlerts.forEach { (date, dateAlerts) ->

                    item(key = "date_$date") {
                        DateHeader(date = date)
                    }

                    items(
                        items = dateAlerts,
                        key = { alert ->
                            "${alert.id}_${alert.alertSendDate}"
                        }
                    ) { alert ->
                        AlertCard(
                            alert = alert,
                            onClick = {
                                // Prefer the API alert ID if available.
                                val selectedId =
                                    if (alert.alertId != 0) {
                                        alert.alertId.toString()
                                    } else {
                                        alert.id.toString()
                                    }

                                onAlertClick(selectedId)
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
            .padding(
                top = 8.dp,
                bottom = 8.dp
            )
            .background(
                color = XpertOrange,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(
                horizontal = 26.dp,
                vertical = 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date,
            color = White,
            fontSize = 18.sp
        )
    }
}

// ============================================================
// ALERT CARD
// ============================================================

@Composable
private fun AlertCard(
    alert: AlertDto,
    onClick: () -> Unit
) {
    val (displayDate, displayTime) = remember(alert.alertSendDate) {
        formatAlertDate(alert.alertSendDate)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(3.dp),
        color = Color(0xFFF4F4F4),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFE4E4E4)
        ),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date and time column
            Column(
                modifier = Modifier
                    .width(112.dp)
                    .padding(
                        horizontal = 8.dp,
                        vertical = 6.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = displayDate,
                    color = Color.Gray,
                    fontSize = 18.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = displayTime,
                    color = Color.Gray,
                    fontSize = 18.sp,
                    maxLines = 1
                )
            }

            // Orange vertical separator
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp)
                    .background(XpertOrange)
            )

            // Alert subject
            Text(
                text = alert.subject
                    ?.takeIf { it.isNotBlank() }
                    ?: alert.message
                        ?.takeIf { it.isNotBlank() }
                    ?: "Alert #${alert.id}",
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    ),
                color = Color.Gray,
                fontSize = 17.sp,
                lineHeight = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============================================================
// DATE FORMATTING
// ============================================================

private fun formatAlertDate(
    rawDate: String?
): Pair<String, String> {
    if (rawDate.isNullOrBlank()) {
        return "Unknown date" to ""
    }

    val patterns = listOf(
        "dd/MM/yyyy HH:mm:ss",
        "dd/MM/yyyy HH:mm",
        "MM/dd/yyyy HH:mm:ss",
        "MM/dd/yyyy HH:mm",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
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

            val parsedDate: Date =
                parser.parse(rawDate) ?: continue

            val dateFormatter = SimpleDateFormat(
                "dd MMM",
                Locale.US
            )

            val timeFormatter = SimpleDateFormat(
                "hh:mm a",
                Locale.US
            )

            val dateText = dateFormatter.format(parsedDate)
            val timeText = if (
                pattern == "yyyy-MM-dd"
            ) {
                ""
            } else {
                timeFormatter.format(parsedDate)
            }

            return dateText to timeText

        } catch (_: ParseException) {
            // Try the next supported API date format.
        }
    }

    // Avoid crashing if the server returns an unfamiliar format.
    return rawDate to ""
}

// ============================================================
// EXPANDED MENU
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
                .height(76.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MenuItem(
                icon = R.drawable.archivedalerts,
                text = "Archived",
                onClick = onArchivedClick,
                modifier = Modifier.weight(1f),
                selected = selectedDestination == "archived"
            )

            MenuItem(
                icon = null,
                text = "About",
                onClick = onAboutClick,
                vectorIcon = Icons.Default.Info,
                modifier = Modifier.weight(1f),
                selected = selectedDestination == "about"
            )

            MenuItem(
                icon = R.drawable.changepassword,
                text = "Password Change",
                onClick = onPasswordClick,
                modifier = Modifier.weight(1f),
                selected = selectedDestination == "password"
            )

            MenuItem(
                icon = R.drawable.new_logout,
                text = "Logout",
                onClick = onLogoutClick,
                modifier = Modifier.weight(1f),
                selected = selectedDestination == "logout"
            )
        }

        HorizontalDivider(
            color = TextGray.copy(alpha = 0.25f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MenuItem(
                icon = R.drawable.archivedalerts,
                text = "Forward Alerts",
                onClick = onForwardAlertsClick,
                modifier = Modifier.weight(1f),
                selected = selectedDestination == "forward"
            )

            MenuItem(
                icon = R.drawable.archivedalerts,
                text = "Profile",
                onClick = onProfileClick,
                modifier = Modifier.weight(1f),
                selected = selectedDestination == "profile"
            )
        }
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
            .height(76.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (vectorIcon != null) {
            Icon(
                imageVector = vectorIcon,
                contentDescription = text,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        } else if (icon != null) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = text,
                colorFilter = ColorFilter.tint(iconColor),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = text,
            fontSize = 10.sp,
            color = if (selected) XpertOrange else TextDark
        )
    }
}

// ============================================================
// BOTTOM NAVIGATION
// ============================================================

@Composable
private fun BottomNavigationBar(
    menuSelected: Boolean,
    selectedDestination: String,
    onDestinationSelected: (String) -> Unit,
    onSubscriptionsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
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
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTab(
                icon = R.drawable.ic_alert_list_bell,
                selected = selectedDestination == "alerts",
                onClick = {
                    onDestinationSelected("alerts")
                },
                modifier = Modifier.weight(1f)
            )

            BottomTab(
                icon = R.drawable.ic_alert_list_subscription,
                selected = selectedDestination == "subscriptions",
                onClick = {
                    onDestinationSelected("subscriptions")
                    onSubscriptionsClick()
                },
                modifier = Modifier.weight(1f)
            )

            BottomTab(
                icon = R.drawable.ic_alert_list_bel,
                selected = selectedDestination == "notifications",
                onClick = {
                    onDestinationSelected("notifications")
                    onNotificationsClick()
                },
                modifier = Modifier.weight(1f)
            )

            BottomTab(
                icon = R.drawable.ic_alert_list_menu,
                selected = menuSelected,
                onClick = {
                    onDestinationSelected("menu")
                    onMenuClick()
                },
                modifier = Modifier.weight(1f)
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
            contentDescription = null,
            colorFilter = ColorFilter.tint(iconColor),
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
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

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun HomeScreenPreview() {
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
