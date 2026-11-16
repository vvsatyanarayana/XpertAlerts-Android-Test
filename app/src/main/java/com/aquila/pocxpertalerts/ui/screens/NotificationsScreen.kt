package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.notifications.LocalNotification
import com.aquila.pocxpertalerts.notifications.NotificationHistoryStore
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import com.aquila.pocxpertalerts.ui.theme.XpertPurple
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
    userId: String,
    historyStore: NotificationHistoryStore,
    onBackClick: () -> Unit,
    onNotificationClick: (LocalNotification) -> Unit
) {
    val notifications by historyStore.notifications(userId).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(White)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = XpertPurple,
                    modifier = Modifier.size(26.dp)
                )
            }
            Text(
                text = "Notifications",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = XpertPurple
            )
            Spacer(Modifier.weight(1f))
            if (notifications.isNotEmpty()) {
                TextButton(onClick = { scope.launch { historyStore.clear(userId) } }) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = XpertOrange)
                    Text("Clear", color = XpertOrange)
                }
            }
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(XpertOrange.copy(alpha = 0.10f), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_alert_list_bel),
                            contentDescription = "Notifications",
                            modifier = Modifier.size(42.dp)
                        )
                    }
                    Spacer(Modifier.height(18.dp))
                    Text("No notifications yet", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(Modifier.height(6.dp))
                    Text("New XpertAlerts notifications will appear here.", fontSize = 14.sp, color = TextGray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch { historyStore.markRead(userId, item.id) }
                                onNotificationClick(item)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(XpertOrange.copy(alpha = 0.10f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_alert_list_bel),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f).padding(start = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(item.title, color = TextDark, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                Text(item.body, color = TextGray, fontSize = 14.sp)
                                Text(formatNotificationDate(item.receivedAt), color = TextGray, fontSize = 11.sp)
                                if (!item.alertId.isNullOrBlank()) {
                                    Text("Tap to view alert", color = XpertPurple, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                            if (!item.isRead) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 8.dp, top = 5.dp)
                                        .size(8.dp)
                                        .background(XpertOrange, RoundedCornerShape(50))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatNotificationDate(timestamp: Long): String = runCatching {
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(timestamp))
}.getOrDefault("")

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationsScreenPreview() {
    XpertAlertsTheme {
        // Preview uses the app's normal screen shell; live history is supplied at runtime.
    }
}
