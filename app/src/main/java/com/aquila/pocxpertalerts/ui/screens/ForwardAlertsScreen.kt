
package com.aquila.pocxpertalerts.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ForwardOrange = Color(0xFFED741C)
private val ForwardGreen = Color(0xFF008B59)
private val ForwardBackground = Color(0xFFFAFAFA)

data class ForwardAlertItem(
    val id: String,
    val userName: String,
    val email: String,
    val applicationName: String,
    val active: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
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
    onStopAll: () -> Unit = {},
    onStopSelected: (List<ForwardAlertItem>) -> Unit = {}
) {
    var selectedIds by remember { mutableStateOf(setOf<String>()) }
    var showStopAllDialog by remember { mutableStateOf(false) }
    var showStopSelectedDialog by remember { mutableStateOf(false) }
    var pendingStatusChange by remember {
        mutableStateOf<Pair<ForwardAlertItem, Boolean>?>(null)
    }

    val allSelected =
        alerts.isNotEmpty() && selectedIds.containsAll(alerts.map { it.id })

    Scaffold(
        containerColor = ForwardBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Forward Alerts",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onCreateClick) {
                        Text("Create", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ForwardOrange
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp)
        ) {
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Search Forward Alerts") },
                trailingIcon = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSearchClick),
                shape = RoundedCornerShape(28.dp),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = allSelected,
                    onCheckedChange = { checked ->
                        selectedIds = if (checked) {
                            alerts.map { it.id }.toSet()
                        } else {
                            emptySet()
                        }
                    }
                )

                Text(
                    text = if (allSelected) "UnSelect All" else "Select All",
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp
                )

                TextButton(
                    onClick = {
                        if (selectedIds.isEmpty()) {
                            showStopAllDialog = true
                        } else {
                            showStopSelectedDialog = true
                        }
                    }
                ) {
                    Text("Stop Forwarding", color = ForwardOrange)
                }
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ForwardOrange)
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(errorMessage)
                    }
                }

                alerts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No forward alerts found")
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(alerts, key = { it.id }) { alert ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = alert.id in selectedIds,
                                            onCheckedChange = { checked ->
                                                selectedIds =
                                                    if (checked) {
                                                        selectedIds + alert.id
                                                    } else {
                                                        selectedIds - alert.id
                                                    }
                                            }
                                        )

                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    onEditClick(alert)
                                                }
                                        ) {
                                            Text(
                                                text = alert.userName,
                                                color = ForwardOrange,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = alert.email,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = alert.applicationName,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 8.dp),
                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (alert.active) {
                                                "Forwarding enabled"
                                            } else {
                                                "Forwarding disabled"
                                            },
                                            modifier = Modifier.weight(1f),
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )

                                        Checkbox(
                                            checked = alert.active,
                                            onCheckedChange = { checked ->
                                                pendingStatusChange =
                                                    alert to checked
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    pendingStatusChange?.let { (alert, newStatus) ->
        AlertDialog(
            onDismissRequest = { pendingStatusChange = null },
            title = { Text("Confirm") },
            text = {
                Text(
                    if (newStatus) {
                        "Are you sure you want to forward alerts to the selected user?"
                    } else {
                        "Are you sure you want to stop forwarding alerts to the selected user?"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onToggleStatus(alert, newStatus)
                        pendingStatusChange = null
                    }
                ) {
                    Text("OK", color = ForwardOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingStatusChange = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showStopSelectedDialog) {
        AlertDialog(
            onDismissRequest = { showStopSelectedDialog = false },
            title = { Text("Confirm") },
            text = {
                Text("Are you sure you want to stop forwarding alerts to the selected user(s)?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selected = alerts.filter {
                            it.id in selectedIds
                        }
                        onStopSelected(selected)
                        showStopSelectedDialog = false
                    }
                ) {
                    Text("OK", color = ForwardOrange)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showStopSelectedDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showStopAllDialog) {
        AlertDialog(
            onDismissRequest = { showStopAllDialog = false },
            title = { Text("Confirm") },
            text = {
                Text(
                    "Are you sure you want to stop forwarding alerts to all users?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onStopAll()
                        showStopAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForwardOrange
                    )
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showStopAllDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
