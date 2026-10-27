package com.aquila.pocxpertalerts.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.BorderGray
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange


@Composable
fun AlertCard(
    subject: String,
    message: String,
    date: String,
    groupBy: String,
    alertId: String,
    isArchived: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Title and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = subject.ifBlank { "Alert" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Alert ID: $alertId",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }

                Text(
                    text = if (isArchived) "Archived" else "Alert",
                    modifier = Modifier
                        .background(
                            color = if (isArchived) {
                                AppBackground
                            } else {
                                XpertOrange.copy(alpha = 0.12f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        ),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isArchived) {
                        TextGray
                    } else {
                        XpertOrange
                    }
                )
            }

            // Alert description
            Text(
                text = message.ifBlank {
                    "No description available"
                },
                fontSize = 14.sp,
                color = TextDark,
                lineHeight = 20.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider(
                color = BorderGray.copy(alpha = 0.45f),
                thickness = 0.7.dp
            )

            // Application/group and date
            if (groupBy.isNotBlank()) {
                Text(
                    text = groupBy,
                    fontSize = 12.sp,
                    color = TextGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date.ifBlank {
                        "Date unavailable"
                    },
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    color = TextGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "View details  ›",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = XpertOrange
                )
            }
        }
    }
}

@Preview(
    name = "Active Alert",
    showBackground = true
)
@Composable
private fun ActiveAlertCardPreview() {
    XpertAlertsTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppBackground)
                .padding(16.dp)
        ) {
            AlertCard(
                subject = "Server Connection Alert",
                message = "The server connection failed. Please check the network connection and try again.",
                date = "26 Oct 2026, 10:30 AM",
                groupBy = "Application: XpertAlerts",
                alertId = "1024",
                isArchived = false,
                onClick = {}
            )
        }
    }
}

@Preview(
    name = "Archived Alert",
    showBackground = true
)
@Composable
private fun ArchivedAlertCardPreview() {
    XpertAlertsTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppBackground)
                .padding(16.dp)
        ) {
            AlertCard(
                subject = "Database Response Alert",
                message = "The database response time exceeded the configured threshold.",
                date = "25 Oct 2026, 04:15 PM",
                groupBy = "Application: Database Monitor",
                alertId = "2048",
                isArchived = true,
                onClick = {}
            )
        }
    }
}