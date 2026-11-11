package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.aquila.pocxpertalerts.ui.components.AppTopBar
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import java.text.SimpleDateFormat
import java.util.Locale
private val ArchivedDetailsPurple = Color(0xFF4527A0)
private val ArchivedDetailsBorder = Color(0xFFE5E5E5)
private val ArchivedDetailsLightOrange = Color(0xFFFFF0E6)

@Composable
fun ArchivedAlertDetailsScreen(
    alert: ArchivedAlertUi,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit = {},
    isDeleting: Boolean = false,
    deleteError: String? = null,
    onClearDeleteError: () -> Unit = {}
) {
    var showDeleteConfirmation by remember(alert.id) { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteConfirmation = false },
            title = { Text("Delete archived alert?") },
            text = { Text("This action will process the server-configured delete action for this archived alert.") },
            confirmButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = {
                        showDeleteConfirmation = false
                        onDeleteClick()
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = { showDeleteConfirmation = false }
                ) { Text("Cancel") }
            }
        )
    }

    if (deleteError != null) {
        AlertDialog(
            onDismissRequest = onClearDeleteError,
            title = { Text("Unable to delete alert") },
            text = { Text(deleteError) },
            confirmButton = { TextButton(onClick = onClearDeleteError) { Text("OK") } }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AppTopBar(
                title = "Archived Alert Details",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Subject and date
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(13.dp),
                        color = ArchivedDetailsLightOrange
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Archived alert",
                            modifier = Modifier.padding(12.dp),
                            tint = XpertOrange
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = alert.subject
                                .ifBlank {
                                    alert.title.ifBlank {
                                        "Archived Alert"
                                    }
                                },
                            color = TextDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = formatArchivedDate(alert.date),
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Message
            ArchivedDetailsCard(title = "Message") {
                val message = alert.description
                    .ifBlank { alert.subject }
                    .ifBlank { "No message available" }
                    .replace("\\n", "\n")
                    .replace(
                        Regex("(?i)<br\\s*/?>"),
                        "\n"
                    )

                Text(
                    text = message,
                    color = TextDark,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }

            // Alert information
            ArchivedDetailsCard(title = "Alert Information") {
                ArchivedDetailInfoRow(
                    label = "Alert ID",
                    value = alert.alertId.toString()
                )

                ArchivedDetailInfoRow(
                    label = "Received",
                    value = formatArchivedDate(alert.date)
                )

                ArchivedDetailInfoRow(
                    label = "Application ID",
                    value = alert.applicationId.toString()
                )

                ArchivedDetailInfoRow(
                    label = "Archive ID",
                    value = alert.archiveId.toString()
                )

                ArchivedDetailInfoRow(
                    label = "Alert Definition ID",
                    value = alert.alertDefId.takeIf { it > 0 }?.toString().orEmpty()
                )

                ArchivedDetailInfoRow(
                    label = "Aggregate Alert ID",
                    value = alert.aggregateAlertId
                )

                ArchivedDetailInfoRow(
                    label = "Group",
                    value = alert.title
                )
            }

            Button(
                onClick = { showDeleteConfirmation = true },
                enabled = !isDeleting,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Deleting…")
                } else {
                    Text("Delete")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ArchivedDetailsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Text(
                text = title,
                color = ArchivedDetailsPurple,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
private fun ArchivedDetailInfoRow(
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
                color = TextGray,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Text(
                text = value.ifBlank { "Not available" },
                modifier = Modifier
                    .weight(1.2f)
                    .padding(start = 8.dp),
                color = TextDark,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Divider(
            color = ArchivedDetailsBorder,
            thickness = 0.5.dp
        )
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
            val parser = SimpleDateFormat(
                pattern,
                Locale.US
            ).apply {
                isLenient = false
            }

            val parsed = parser.parse(value) ?: continue

            val outputPattern =
                if (pattern == "yyyy-MM-dd") {
                    "dd MMM yyyy"
                } else {
                    "dd MMM yyyy, hh:mm a"
                }

            return SimpleDateFormat(
                outputPattern,
                Locale.US
            ).format(parsed)

        } catch (_: Exception) {
            // Try the next supported date format.
        }
    }

    return value
}

