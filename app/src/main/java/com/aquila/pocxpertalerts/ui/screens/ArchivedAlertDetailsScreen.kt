
package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Locale

private val DetailsOrange = Color(0xFFED741C)
private val DetailsPurple = Color(0xFF4527A0)
private val DetailsGray = Color(0xFF757575)
private val DetailsBorder = Color(0xFFE5E5E5)
private val DetailsLightOrange = Color(0xFFFFF0E6)

@Composable
fun ArchivedAlertDetailsScreen(
    alert: ArchivedAlertUi,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Orange toolbar, matching Alert Details.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(DetailsOrange)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Archived Alert Details",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Subject and date card.
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = DetailsLightOrange
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Archived alert",
                            modifier = Modifier.padding(11.dp),
                            tint = DetailsOrange
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = alert.subject.ifBlank {
                                alert.title.ifBlank { "Archived Alert" }
                            },
                            color = Color(0xFF252525),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = formatArchivedDate(alert.date),
                            color = DetailsGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Message card.
            DetailsCard(title = "Message") {
                Text(
                    text = alert.description
                        .ifBlank { alert.subject }
                        .ifBlank { "No message available" }
                        .replace("\\n", "\n")
                        .replace(Regex("(?i)<br\\s*/?>"), "\n"),
                    color = Color(0xFF303030),
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }

            // Alert Information card.
            DetailsCard(title = "Alert Information") {
                DetailInfoRow("Alert ID", alert.alertId.toString())
                DetailInfoRow(
                    "Received",
                    formatArchivedDate(alert.date)
                )
                DetailInfoRow(
                    "Application ID",
                    alert.applicationId.toString()
                )
                DetailInfoRow(
                    "Archive ID",
                    alert.archiveId.toString()
                )
                DetailInfoRow(
                    "Priority",
                    alert.priority.ifBlank { "Non Urgent" }
                )
                DetailInfoRow(
                    "Current Status",
                    alert.currentStatus.ifBlank { "Archived" }
                )
                DetailInfoRow(
                    "Application URL",
                    alert.applicationUrl.ifBlank { "Not available" }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DetailsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = title,
                color = DetailsPurple,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                color = DetailsGray,
                fontSize = 12.sp
            )

            Text(
                text = value,
                modifier = Modifier.weight(1.2f),
                color = Color(0xFF303030),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        Divider(color = DetailsBorder, thickness = 0.5.dp)
    }
}

private fun formatArchivedDate(value: String): String {
    if (value.isBlank()) return "Date not available"

    val patterns = listOf(
        "MM/dd/yyyy hh:mm a",
        "MM/dd/yyyy HH:mm",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd"
    )

    for (pattern in patterns) {
        try {
            val parser = SimpleDateFormat(pattern, Locale.US).apply {
                isLenient = false
            }
            val parsed = parser.parse(value) ?: continue

            return SimpleDateFormat(
                if (pattern == "yyyy-MM-dd") "dd MMM yyyy"
                else "dd MMM yyyy, hh:mm a",
                Locale.US
            ).format(parsed)
        } catch (_: Exception) {
            // Try the next supported input format.
        }
    }

    return value
}
