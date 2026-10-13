
package com.aquila.pocxpertalerts.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.data.model.AlertDto
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import java.text.SimpleDateFormat
import java.util.Locale
import android.text.Html
private val XpertOrange = Color(0xFFED741C)
private val XpertPurple = Color(0xFF3F237D)
private val ScreenBackground = Color(0xFFF7F7F7)
private val DarkText = Color(0xFF222222)
private val GrayText = Color(0xFF777777)
private val LightOrange = Color(0xFFFFF1E8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailScreen(
    alert: AlertDto,
    onBackClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf<String?>(null) }

    val displayedId = if (alert.alertId != 0) {
        alert.alertId.toString()
    } else {
        alert.id.toString()
    }

    val subject = alert.subject
        ?.takeIf { it.isNotBlank() }
        ?: "System Alert"

    val message = remember(alert.message) {
        val rawMessage = alert.message
            ?.takeIf { it.isNotBlank() }
            ?: "No message is available for this alert."

        Html.fromHtml(
            rawMessage,
            Html.FROM_HTML_MODE_LEGACY
        ).toString().trim()
    }

    val receivedDate = remember(alert.alertSendDate) {
        formatAlertDate(alert.alertSendDate)
    }

    Scaffold(
        containerColor = ScreenBackground,
        topBar = {


            TopAppBar(
                title = {
                    Text(
                        text = "Alert Details",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
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
                    // Empty spacer balances the back button,
                    // keeping the title visually centered.
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = XpertOrange,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )


        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Alert summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = LightOrange
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Alert",
                                tint = XpertOrange,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = subject,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = receivedDate,
                            fontSize = 13.sp,
                            color = GrayText
                        )
                    }
                }
            }

            // Full alert message
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Message",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = XpertPurple
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = message,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        color = DarkText
                    )
                }
            }

            // Alert information
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Alert Information",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = XpertPurple
                    )

                    DetailRow(
                        label = "Alert ID",
                        value = displayedId
                    )

                    DetailRow(
                        label = "Received",
                        value = receivedDate
                    )

                    DetailRow(
                        label = "Application ID",
                        value = alert.applicationId.toString()
                    )


                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    selectedAction?.let { action ->
        AlertActionConfirmationDialog(
            action = action,
            onConfirm = {
                // Connect the action API here when available.
                selectedAction = null
            },
            onDismiss = {
                selectedAction = null
            }
        )
    }
}

private fun formatAlertDate(rawDate: String?): String {
    if (rawDate.isNullOrBlank()) {
        return "Date unavailable"
    }

    val patterns = listOf(
        "MM/dd/yyyy hh:mm a",
        "MM/dd/yyyy HH:mm:ss",
        "MM/dd/yyyy HH:mm",
        "dd/MM/yyyy hh:mm a",
        "dd/MM/yyyy HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd"
    )

    for (pattern in patterns) {
        try {
            val parser = java.text.SimpleDateFormat(
                pattern,
                java.util.Locale.US
            ).apply {
                isLenient = false
            }

            val parsedDate = parser.parse(rawDate) ?: continue

            return java.text.SimpleDateFormat(
                "dd MMM yyyy",
                java.util.Locale.US
            ).format(parsedDate)

        } catch (_: java.text.ParseException) {
            // Try the next supported format.
        }
    }

    return rawDate
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = GrayText
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DarkText
        )
    }
}

@Composable
private fun AlertActionConfirmationDialog(
    action: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Confirmation",
                tint = XpertOrange,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Confirm Action",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Are you sure you want to \"$action\" this alert?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm", color = XpertOrange)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AlertDetailScreenPreview() {
    XpertAlertsTheme {
        AlertDetailScreen(
            alert = AlertDto(
                id = 635L,
                alertId = 635,
                subject = "System Alert",
                message = "New alert received from Xpert Alerts.\n\n" +
                        "Please review this alert and take the required action.",
                alertSendDate = "2026-10-13",
                applicationId = 45L
            ),
            onBackClick = {}
        )
    }
}
