
package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ArchivedOrange = Color(0xFFED741C)
private val ArchivedGray = Color(0xFF757575)

data class ArchivedAlertUi(
    val id: String,
    val title: String,
    val date: String,
    val subject: String,
    val description: String = "",
    val priority: String = "Non Urgent",
    val currentStatus: String = "",
    val applicationUrl: String = "",
    val archiveId: Long = id.toLongOrNull() ?: 0L,
    val alertId: Int = 0,
    val aggregateAlertId: String = "",
    val alertDefId: Int = 0,
    val applicationId: Long = 0L
)

@Composable
fun ArchivedAlertsScreen(
    alerts: List<ArchivedAlertUi>,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAlertClick: (ArchivedAlertUi) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetryClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Orange toolbar with centered heading
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp)
                .background(ArchivedOrange)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Archived Alerts",
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = onRefreshClick,
                modifier = Modifier.align(Alignment.CenterEnd).size(48.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh archived alerts", tint = Color.White)
            }
        }

        // Search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 16.dp)
                .height(50.dp)
                .background(Color(0xFFF5F5F5))
                .clickable(onClick = onSearchClick)
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Search Archived Alerts",
                color = ArchivedGray,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = ArchivedGray
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = ArchivedOrange
                    )
                }

                errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(errorMessage, color = ArchivedGray)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = onRetryClick, colors = ButtonDefaults.buttonColors(containerColor = ArchivedOrange)) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }

                alerts.isEmpty() -> {
                    Text(
                        text = "No records found",
                        modifier = Modifier.align(Alignment.Center),
                        color = ArchivedGray,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(
                            items = alerts,
                            key = { it.id }
                        ) { alert ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onAlertClick(alert)
                                    }
                                    .padding(
                                        horizontal = 5.dp,
                                        vertical = 12.dp
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = alert.title,
                                        color = ArchivedOrange,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(Modifier.width(8.dp))

                                    Text(
                                        text = alert.date,
                                        color = ArchivedGray,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    text = alert.subject,
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )

                                if (alert.description.isNotBlank()) {
                                    Spacer(Modifier.height(5.dp))

                                    Text(
                                        text = alert.description,
                                        color = ArchivedGray,
                                        fontSize = 14.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(top = 16.dp),
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// PREVIEW 1: Empty archived alerts
@Preview(
    name = "1. Archived Alerts - Empty",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=873dp,dpi=420"
)
@Composable
private fun ArchivedAlertsEmptyPreview() {
    MaterialTheme {
        ArchivedAlertsScreen(
            alerts = emptyList(),
            onBackClick = {},
            onSearchClick = {},
            onAlertClick = {}
        )
    }
}

// PREVIEW 3: Archived alerts with sample results
@Preview(
    name = "3. Archived Alerts - Results",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=873dp,dpi=420"
)
@Composable
private fun ArchivedAlertsResultsPreview() {
    val previewAlerts = listOf(
        ArchivedAlertUi(
            id = "1",
            title = "Application Alert",
            date = "12/03/2026",
            subject = "Server Restarted",
            description = "The server has been restarted successfully."
        ),
        ArchivedAlertUi(
            id = "2",
            title = "Security Alert",
            date = "10/03/2026",
            subject = "Multiple Login Attempts",
            description = "Multiple failed login attempts detected."
        ),
        ArchivedAlertUi(
            id = "3",
            title = "System Alert",
            date = "08/03/2026",
            subject = "Database Backup Completed",
            description = "Scheduled database backup completed."
        ),
        ArchivedAlertUi(
            id = "4",
            title = "Performance Alert",
            date = "05/03/2026",
            subject = "High CPU Usage",
            description = "CPU usage is above 90% for more than 5 minutes."
        ),
        ArchivedAlertUi(
            id = "5",
            title = "Network Alert",
            date = "01/03/2026",
            subject = "Network Latency",
            description = "Network latency is higher than the threshold."
        )
    )

    MaterialTheme {
        ArchivedAlertsScreen(
            alerts = previewAlerts,
            onBackClick = {},
            onSearchClick = {},
            onAlertClick = {}
        )
    }
}
