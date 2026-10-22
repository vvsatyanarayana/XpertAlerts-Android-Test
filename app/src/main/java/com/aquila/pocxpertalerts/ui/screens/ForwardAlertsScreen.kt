
package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
private val ForwardBackground = Color(0xFFFAFAFA)
private val ForwardSearchPurple = Color(0xFF673AB7)
private val ForwardSearchBorder = Color(0xFF999999)

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
    onSetAllStatus: (Boolean) -> Unit = {},
    onDeleteClick: (ForwardAlertItem) -> Unit = {}
) {
    var pendingStatusChange by remember {
        mutableStateOf<Pair<ForwardAlertItem, Boolean>?>(null)
    }
    var pendingAllStatus by remember {
        mutableStateOf<Boolean?>(null)
    }
    var pendingDelete by remember {
        mutableStateOf<ForwardAlertItem?>(null)
    }

    val allActive = alerts.isNotEmpty() && alerts.all { it.active }

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
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onCreateClick) {
                        Text(
                            text = "Create",
                            color = Color.White
                        )
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
                .padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            // Rounded Search Forward Alerts bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable(onClick = onSearchClick)
                    .border(
                        width = 1.dp,
                        color = ForwardSearchBorder,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Search Forward Alerts",
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF757575),
                    fontSize = 16.sp
                )

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Forward Alerts",
                    tint = Color.Gray
                )
            }

            // Select All / UnSelect All
            if (alerts.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = allActive,
                        onCheckedChange = { checked ->
                            pendingAllStatus = checked
                        }
                    )

                    Text(
                        text = if (allActive) "UnSelect All" else "Select All",
                        fontSize = 14.sp
                    )
                }
            }

            // Loading, error, empty, and alert list states
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ForwardOrange)
                    }
                }

                errorMessage != null && alerts.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color.DarkGray
                        )
                    }
                }

                alerts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No records found",
                            color = Color(0xFF757575),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = alerts,
                            key = { it.id }
                        ) { alert ->

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 8.dp
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = alert.userName.ifBlank {
                                                    "User"
                                                },
                                                color = ForwardOrange,
                                                fontWeight = FontWeight.SemiBold
                                            )

                                            Text(
                                                text = alert.email,
                                                fontSize = 12.sp,
                                                color = Color.DarkGray
                                            )

                                            Text(
                                                text = alert.applicationName,
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                pendingDelete = alert
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete mapping",
                                                tint = ForwardOrange
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Activate",
                                            modifier = Modifier.weight(1f),
                                            fontSize = 13.sp
                                        )

                                        Text(
                                            text = if (alert.active) "Yes" else "No",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )

                                        Checkbox(
                                            checked = alert.active,
                                            onCheckedChange = { checked ->
                                                pendingStatusChange = alert to checked
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

    // Confirm individual alert status change
    pendingStatusChange?.let { (alert, status) ->
        AlertDialog(
            onDismissRequest = {
                pendingStatusChange = null
            },
            title = {
                Text("Confirm")
            },
            text = {
                Text(
                    if (status) {
                        "Are you sure you want to forward alerts to the selected user?"
                    } else {
                        "Are you sure you want to stop forwarding alerts to the selected user?"
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
                    Text("OK", color = ForwardOrange)
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

    // Confirm status change for all alerts
    pendingAllStatus?.let { status ->
        AlertDialog(
            onDismissRequest = {
                pendingAllStatus = null
            },
            title = {
                Text("Confirm")
            },
            text = {
                Text(
                    if (status) {
                        "Are you sure you want to forward alerts to all users?"
                    } else {
                        "Are you sure you want to stop forwarding alerts to all users?"
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
                        containerColor = ForwardOrange
                    )
                ) {
                    Text("OK")
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

    // Confirm deletion
    pendingDelete?.let { alert ->
        AlertDialog(
            onDismissRequest = {
                pendingDelete = null
            },
            title = {
                Text("Delete mapping")
            },
            text = {
                Text("Are you sure you want to delete the mapped user?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick(alert)
                        pendingDelete = null
                    }
                ) {
                    Text("OK", color = ForwardOrange)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
