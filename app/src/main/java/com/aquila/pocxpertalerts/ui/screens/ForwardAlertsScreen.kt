
package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.ui.components.AppListSearchBar
import com.aquila.pocxpertalerts.ui.components.AppTopBar
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange

data class ForwardAlertItem(
    val id: String,
    val userName: String,
    val email: String,
    val applicationName: String,
    val active: Boolean,
    val mappedUserId: String = "",
    val userId: String = "",
    val applicationId: String = "",
    val sortOrder: String = "0"
)

@Composable
fun ForwardAlertsScreen(
    onBackClick: () -> Unit,
    onCreateClick: () -> Unit,
    onEditClick: (ForwardAlertItem) -> Unit,
    onSearchClick: () -> Unit,
    alerts: List<ForwardAlertItem>,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onToggleStatus: (ForwardAlertItem, Boolean) -> Unit = { _, _ -> },
    onSetAllStatus: (Boolean) -> Unit = {},
    onDeleteClick: (ForwardAlertItem) -> Unit = {}
) {
    var pendingStatusChange by remember {
        mutableStateOf<Pair<ForwardAlertItem, Boolean>?>(null)
    }

    var pendingAllStatus by remember {
        mutableStateOf<Boolean?>(null)
    }

    val allActive = alerts.isNotEmpty() && alerts.all { it.active }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackground,
        topBar = {
            AppTopBar(
                title = "Forward Alerts",
                onBackClick = onBackClick,
                trailingActionText = "Create",
                onTrailingActionClick = onCreateClick
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground)
        ) {

            AppListSearchBar(
                placeholder = "Search Forward Alerts",
                onClick = onSearchClick,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
            )

            // Select All / UnSelect All
            if (alerts.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 14.dp,
                                vertical = 6.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = allActive,
                            onCheckedChange = { checked ->
                                pendingAllStatus = checked
                            }
                        )

                        Text(
                            text = if (allActive) {
                                "UnSelect All"
                            } else {
                                "Select All"
                            },
                            modifier = Modifier.weight(1f),
                            color = TextDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "${alerts.count { it.active }} of ${alerts.size} active",
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Loading, error, empty, and list states
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = XpertOrange
                        )
                    }
                }

                errorMessage != null && alerts.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                }

                alerts.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No forward alerts found",
                            color = TextGray,
                            fontSize = 15.sp
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 4.dp,
                            bottom = 20.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = alerts,
                            key = { it.id }
                        ) { alert ->

                            ForwardAlertCard(
                                alert = alert,
                                onClick = {
                                    onEditClick(alert)
                                },
                                onStatusChange = { checked ->
                                    pendingStatusChange = alert to checked
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirm individual status change
    pendingStatusChange?.let { (alert, status) ->
        AlertDialog(
            onDismissRequest = {
                pendingStatusChange = null
            },
            title = {
                Text("Confirm status change")
            },
            text = {
                Text(
                    if (status) {
                        "Are you sure you want to forward alerts to ${alert.userName.ifBlank { "this user" }}?"
                    } else {
                        "Are you sure you want to stop forwarding alerts to ${alert.userName.ifBlank { "this user" }}?"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onToggleStatus(alert, status)
                        pendingStatusChange = null
                    }
                ) {
                    Text(
                        text = "Confirm",
                        color = XpertOrange
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingStatusChange = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirm Select All / UnSelect All
    pendingAllStatus?.let { status ->
        AlertDialog(
            onDismissRequest = {
                pendingAllStatus = null
            },
            title = {
                Text("Confirm status change")
            },
            text = {
                Text(
                    if (status) {
                        "Are you sure you want to activate forwarding for all users?"
                    } else {
                        "Are you sure you want to deactivate forwarding for all users?"
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetAllStatus(status)
                        pendingAllStatus = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = XpertOrange
                    )
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingAllStatus = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ------------------------------------------------------------
// FORWARD ALERT CARD
// ------------------------------------------------------------

@Composable
private fun ForwardAlertCard(
    alert: ForwardAlertItem,
    onClick: () -> Unit,
    onStatusChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // User name and active status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = alert.userName.ifBlank { "User" },
                        color = TextDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = alert.email.ifBlank { "No email available" },
                        color = TextGray,
                        fontSize = 13.sp
                    )
                }

                Switch(
                    checked = alert.active,
                    onCheckedChange = onStatusChange,
                    modifier = Modifier.size(
                        width = 50.dp,
                        height = 40.dp
                    ),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White,
                        checkedTrackColor = XpertOrange,
                        checkedBorderColor = XpertOrange,
                        uncheckedThumbColor = White,
                        uncheckedTrackColor = TextGray.copy(alpha = 0.25f),
                        uncheckedBorderColor = TextGray.copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(
                color = TextGray.copy(alpha = 0.18f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Application information
            Text(
                text = "APPLICATION",
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = alert.applicationName.ifBlank {
                    "Application not specified"
                },
                color = TextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Current forwarding status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (alert.active) {
                                XpertOrange
                            } else {
                                TextGray.copy(alpha = 0.5f)
                            },
                            shape = RoundedCornerShape(50)
                        )
                )

                Spacer(modifier = Modifier.width(7.dp))

                Text(
                    text = if (alert.active) {
                        "Forwarding enabled"
                    } else {
                        "Forwarding disabled"
                    },
                    color = if (alert.active) {
                        XpertOrange
                    } else {
                        TextGray
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
