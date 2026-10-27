
package com.aquila.pocxpertalerts.ui.screens

import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.data.model.AlertDto
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.components.AppTopBar
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.AlertAccentColor
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlertDetailScreen(
    alert: AlertDto,
    onBackClick: () -> Unit
) {
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
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AppTopBar(
                title = "Alert Details",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppBackground)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 15.dp,
                    vertical = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Alert summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
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
                        modifier = Modifier.size(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = AlertAccentColor.copy(alpha = 0.12f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Alert",
                                tint = XpertOrange,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = subject,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = receivedDate,
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                }
            }

            // Full alert message
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Message Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = XpertOrange
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = message,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = TextDark
                    )
                }
            }

            // Alert information
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Alert Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = XpertOrange
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

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// ------------------------------------------------------------
// DATE FORMATTING
// ------------------------------------------------------------

private fun formatAlertDate(
    rawDate: String?
): String {
    if (rawDate.isNullOrBlank()) {
        return "Date unavailable"
    }

    val patterns = listOf(
        "MM/dd/yyyy hh:mm a",
        "MM/dd/yyyy HH:mm:ss",
        "MM/dd/yyyy HH:mm",
        "dd/MM/yyyy hh:mm a",
        "dd/MM/yyyy HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
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

            val parsedDate: Date = parser.parse(rawDate) ?: continue

            return SimpleDateFormat(
                "dd MMM yyyy",
                Locale.US
            ).format(parsedDate)

        } catch (_: ParseException) {
            // Try the next supported format.
        }
    }

    return rawDate
}

// ------------------------------------------------------------
// DETAIL ROW
// ------------------------------------------------------------

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = TextGray
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark
        )
    }
}

// ------------------------------------------------------------
// PREVIEW
// ------------------------------------------------------------

@Preview(
    name = "Alert Details",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=873dp,dpi=440"
)
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
