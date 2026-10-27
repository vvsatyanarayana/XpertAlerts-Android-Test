
package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.components.AppListSearchBar
import com.aquila.pocxpertalerts.ui.components.AppTopBar
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ------------------------------------------------------------
// MODEL
// Keep the existing fields for API and navigation compatibility.
// ------------------------------------------------------------

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

// ------------------------------------------------------------
// ARCHIVED ALERTS SCREEN
// ------------------------------------------------------------

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
    // Preserve the original alert order while grouping by date.
    val groupedAlerts = remember(alerts) {
        alerts.groupBy { alert ->
            formatGroupDate(alert.date)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AppTopBar(
                title = "Archived Alerts",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppBackground)
        ) {

            // Shared search bar, matching the other app screens.
            AppListSearchBar(
                placeholder = "Search Archived Alerts",
                onClick = onSearchClick,
                modifier = Modifier.padding(
                    start = 15.dp,
                    end = 15.dp,
                    top = 14.dp,
                    bottom = 10.dp
                )
            )

            // Pull-to-refresh and alert content.
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
                        start = 15.dp,
                        end = 15.dp,
                        top = 4.dp,
                        bottom = 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                        color = XpertOrange
                                    )
                                }
                            }
                        }

                        errorMessage != null && alerts.isEmpty() -> {
                            item(key = "error_state") {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = 16.dp,
                                            vertical = 48.dp
                                        ),
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = errorMessage,
                                        color = TextGray,
                                        fontSize = 14.sp
                                    )

                                    Spacer(
                                        modifier = Modifier.height(16.dp)
                                    )

                                    Button(
                                        onClick = onRetryClick,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = XpertOrange
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = "Retry",
                                            color = White
                                        )
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
                                        color = TextGray,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        else -> {
                            groupedAlerts.forEach { (dateHeader, dateAlerts) ->

                                // Date header.
                                item(key = "date_$dateHeader") {
                                    DateGroupHeader(
                                        date = dateHeader
                                    )
                                }

                                // Alert cards.
                                items(
                                    items = dateAlerts,
                                    key = { alert -> alert.id }
                                ) { alert ->
                                    ArchivedAlertCard(
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
}

// ------------------------------------------------------------
// DATE GROUP HEADER
// ------------------------------------------------------------

@Composable
private fun DateGroupHeader(
    date: String
) {
    Box(
        modifier = Modifier
            .padding(
                top = 4.dp,
                bottom = 2.dp
            )
            .clip(CircleShape)
            .background(XpertOrange)
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date,
            color = White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

// ------------------------------------------------------------
// ARCHIVED ALERT CARD
// ------------------------------------------------------------

@Composable
private fun ArchivedAlertCard(
    alert: ArchivedAlertUi,
    onClick: () -> Unit
) {
    val dateTime = splitAlertDateTime(alert.date)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                .heightIn(min = 76.dp)
                .padding(
                    horizontal = 12.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Date and time.
            Column(
                modifier = Modifier.width(76.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dateTime.first,
                    color = TextDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (dateTime.second.isNotBlank()) {
                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = dateTime.second,
                        color = TextGray,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            // Orange divider.
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(XpertOrange)
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            // Subject and alert title.
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = alert.subject
                        .ifBlank { alert.title }
                        .ifBlank { "No subject available" },
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 19.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (alert.title.isNotBlank() &&
                    alert.subject.isNotBlank() &&
                    !alert.title.equals(alert.subject, ignoreCase = true)
                ) {
                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = alert.title,
                        color = TextGray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            // Subtle navigation indicator.
            Text(
                text = "›",
                color = TextGray,
                fontSize = 25.sp,
                lineHeight = 25.sp
            )
        }

        HorizontalDivider(
            color = TextGray.copy(alpha = 0.12f),
            thickness = 0.5.dp
        )
    }
}

// ------------------------------------------------------------
// DATE PARSING
// ------------------------------------------------------------

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

private fun parseAlertDate(
    rawDate: String
): Date? {
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
            // Try the next supported date format.
        }
    }

    return null
}

private fun formatGroupDate(
    rawDate: String
): String {
    val parsedDate = parseAlertDate(rawDate)

    return if (parsedDate != null) {
        SimpleDateFormat(
            "dd MMM",
            Locale.ENGLISH
        ).format(parsedDate)
    } else {
        rawDate.substringBefore(" ")
            .ifBlank { "Unknown date" }
    }
}

private fun splitAlertDateTime(
    rawDate: String
): Pair<String, String> {
    val parsedDate = parseAlertDate(rawDate)

    return if (parsedDate != null) {
        val date = SimpleDateFormat(
            "dd MMM",
            Locale.ENGLISH
        ).format(parsedDate)

        val hasTime = rawDate.contains(":")

        val time = if (hasTime) {
            SimpleDateFormat(
                "hh:mm a",
                Locale.ENGLISH
            ).format(parsedDate)
                .uppercase(Locale.ENGLISH)
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

// ------------------------------------------------------------
// PREVIEWS
// ------------------------------------------------------------

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
            description = "Estimate Request Sent/Ack for Event 1770"
        ),
        ArchivedAlertUi(
            id = "2",
            title = "Application Alert",
            date = "01/27/2026 05:15 AM",
            subject = "Database Alert",
            description = "Database alert notification"
        ),
        ArchivedAlertUi(
            id = "3",
            title = "Task Alert",
            date = "01/22/2026 04:00 AM",
            subject = "Task Due and Past Due",
            description = "Task due notification"
        )
    )

    XpertAlertsTheme {
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
    XpertAlertsTheme {
        ArchivedAlertsScreen(
            alerts = emptyList(),
            onBackClick = {},
            onSearchClick = {},
            onAlertClick = {}
        )
    }
}
