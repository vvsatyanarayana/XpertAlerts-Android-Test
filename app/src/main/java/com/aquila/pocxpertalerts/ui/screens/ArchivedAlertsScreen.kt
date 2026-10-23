
package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ArchivedOrange = Color(0xFFED741C)
private val ArchivedGray = Color(0xFF757575)
private val ArchivedSearchBackground = Color(0xFFF4F5F7)
private val ArchivedCardBackground = Color(0xFFF1F1F1)

// Keep the existing model and its fields.
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

@OptIn(ExperimentalMaterial3Api::class)
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
    // Group alerts by their actual date while preserving their existing order.
    val groupedAlerts = remember(alerts) {
        alerts.groupBy { alert ->
            formatGroupDate(alert.date)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Orange toolbar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(ArchivedOrange)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Archived Alerts",
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        // Existing search interaction retained.
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .height(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onSearchClick),
            shape = CircleShape,
            color = ArchivedSearchBackground,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Search Archived Alerts",
                    modifier = Modifier.weight(1f),
                    color = ArchivedGray,
                    fontSize = 13.sp,
                    maxLines = 1
                )

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search archived alerts",
                    tint = ArchivedGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Keep pull-to-refresh and existing loading states.
        PullToRefreshBox(
            isRefreshing = isLoading && alerts.isNotEmpty(),
            onRefresh = onRefreshClick,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 4.dp,
                    end = 4.dp,
                    top = 2.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                when {
                    isLoading && alerts.isEmpty() -> {
                        item(key = "initial_loading") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = ArchivedOrange
                                )
                            }
                        }
                    }

                    errorMessage != null && alerts.isEmpty() -> {
                        item(key = "error_state") {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = errorMessage,
                                    color = ArchivedGray,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = onRetryClick,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ArchivedOrange
                                    )
                                ) {
                                    Text("Retry", color = Color.White)
                                }
                            }
                        }
                    }

                    alerts.isEmpty() -> {
                        item(key = "empty_state") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No records found",
                                    color = ArchivedGray,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    else -> {
                        groupedAlerts.forEach { (dateHeader, dateAlerts) ->
                            // Orange rounded date header.
                            item(key = "date_$dateHeader") {
                                DateGroupHeader(date = dateHeader)
                            }

                            // Compact card for each alert.
                            items(
                                items = dateAlerts,
                                key = { alert -> alert.id }
                            ) { alert ->
                                ArchivedAlertRow(
                                    alert = alert,
                                    onClick = {
                                        onAlertClick(alert)
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

@Composable
private fun DateGroupHeader(date: String) {
    Box(
        modifier = Modifier
            .padding(
                top = 3.dp,
                bottom = 8.dp
            )
            .height(34.dp)
            .defaultMinSize(minWidth = 82.dp)
            .clip(CircleShape)
            .background(ArchivedOrange)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun ArchivedAlertRow(
    alert: ArchivedAlertUi,
    onClick: () -> Unit
) {
    val dateTime = splitAlertDateTime(alert.date)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 62.dp)
            .background(
                color = ArchivedCardBackground,
                shape = MaterialTheme.shapes.extraSmall
            )
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left column: date and time.
        Column(
            modifier = Modifier
                .width(78.dp)
                .padding(horizontal = 3.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dateTime.first,
                color = ArchivedGray,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = dateTime.second,
                color = ArchivedGray,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Thin orange vertical line.
        Box(
            modifier = Modifier
                .width(1.dp)
                .heightIn(min = 48.dp)
                .align(Alignment.CenterVertically)
                .background(ArchivedOrange)
        )

        // Display only the subject in the archived alerts list.
        Text(
            text = alert.subject
                .ifBlank { alert.title }
                .ifBlank { "No subject available" },
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = 12.dp,
                    end = 8.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
            color = ArchivedGray,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private val supportedDateFormats = listOf(
    "MM/dd/yyyy hh:mm a",
    "MM/dd/yyyy h:mm a",
    "MM/dd/yyyy HH:mm:ss",
    "MM/dd/yyyy HH:mm",
    "MM/dd/yyyy",
    "MM-dd-yyyy hh:mm a",
    "MM-dd-yyyy HH:mm:ss",
    "MM-dd-yyyy HH:mm",
    "MM-dd-yyyy",
    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
    "yyyy-MM-dd'T'HH:mm:ss",
    "yyyy-MM-dd HH:mm:ss",
    "yyyy-MM-dd HH:mm",
    "yyyy-MM-dd"
)

private fun parseAlertDate(rawDate: String): Date? {
    for (pattern in supportedDateFormats) {
        try {
            val formatter = SimpleDateFormat(
                pattern,
                Locale.US
            ).apply {
                isLenient = false
            }

            formatter.parse(rawDate)?.let { parsedDate ->
                return parsedDate
            }
        } catch (_: Exception) {
            // Try another supported date format.
        }
    }

    return null
}

private fun formatGroupDate(rawDate: String): String {
    val parsedDate = parseAlertDate(rawDate)

    return if (parsedDate != null) {
        SimpleDateFormat("dd MMM", Locale.ENGLISH).format(parsedDate)
    } else {
        rawDate.substringBefore(" ").ifBlank { "Unknown date" }
    }
}

private fun splitAlertDateTime(rawDate: String): Pair<String, String> {
    val parsedDate = parseAlertDate(rawDate)

    return if (parsedDate != null) {
        val date = SimpleDateFormat(
            "dd MMM",
            Locale.ENGLISH
        ).format(parsedDate)

        val time = if (rawDate.contains(":")) {
            SimpleDateFormat(
                "hh:mm a",
                Locale.ENGLISH
            ).format(parsedDate).uppercase(Locale.ENGLISH)
        } else {
            ""
        }

        Pair(date, time)
    } else {
        Pair(
            rawDate.substringBefore(" ").ifBlank { "--" },
            ""
        )
    }
}

@Preview(
    name = "Archived Alerts - Results",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ArchivedAlertsResultsPreview() {
    val previewAlerts = listOf(
        ArchivedAlertUi(
            id = "1",
            title = "Application Alert",
            date = "01/27/2026 04:09 AM",
            subject = "Estimate Request",
            description = "Estimate Request Sent/Ack for the Event# 1770 (Walter - Tuesday January)"
        ),
        ArchivedAlertUi(
            id = "2",
            title = "Application Alert",
            date = "01/27/2026 04:09 AM",
            subject = "Estimate Request",
            description = "Estimate Request Sent/Ack for the Event# 1770 (Walter - Tuesday January)"
        ),
        ArchivedAlertUi(
            id = "3",
            title = "Task Alert",
            date = "01/22/2026 04:00 AM",
            subject = "Task Due and Past Due",
            description = "Task Due and Past Due"
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

@Preview(
    name = "Archived Alerts - Empty",
    showBackground = true
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
