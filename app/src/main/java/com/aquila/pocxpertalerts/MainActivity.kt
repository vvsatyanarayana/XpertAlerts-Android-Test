
package com.aquila.pocxpertalerts

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.aquila.pocxpertalerts.navigation.AppNavigation
import com.aquila.pocxpertalerts.notifications.NotificationIntentKeys
import com.aquila.pocxpertalerts.notifications.NotificationOpenRequest
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private var notificationOpenRequest by mutableStateOf<NotificationOpenRequest?>(null)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationOpenRequest = intent.toNotificationOpenRequest()
    }

    private fun clearNotificationIntentExtras() {
        intent?.let { current ->
            listOf(
                NotificationIntentKeys.OPEN_NOTIFICATION,
                NotificationIntentKeys.NOTIFICATION_ID,
                NotificationIntentKeys.ALERT_ID,
                NotificationIntentKeys.TITLE,
                NotificationIntentKeys.BODY,
                "google.message_id",
                "gcm.n.e",
                "gcm.n.title",
                "gcm.n.body",
                "alertId",
                "alertID",
                "alert_id",
                "dispatchId",
                "title",
                "subject",
                "body",
                "message"
            ).forEach(current::removeExtra)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationOpenRequest = intent.toNotificationOpenRequest()

        enableEdgeToEdge()

        // Keep status bar icons white.
        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = false

        setContent {
            XpertAlertsTheme {
                XpertAlertsApp(
                    notificationOpenRequest = notificationOpenRequest,
                    onNotificationOpenRequestConsumed = {
                        notificationOpenRequest = null
                        clearNotificationIntentExtras()
                    }
                )
            }
        }
    }
}

@Composable
fun XpertAlertsApp(
    notificationOpenRequest: NotificationOpenRequest? = null,
    onNotificationOpenRequestConsumed: () -> Unit = {}
) {

    var showSplash by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        delay(2000)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else {
        AppNavigation(
            notificationOpenRequest = notificationOpenRequest,
            onNotificationOpenRequestConsumed = onNotificationOpenRequestConsumed
        )
    }
}

@Composable
fun SplashScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.xpertalerts_splash_logo
            ),
            contentDescription = "Xpert Alerts Logo",
            modifier = Modifier.size(400.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,

    )
@Composable
private fun SplashScreenPreview() {
    XpertAlertsTheme {
        SplashScreen()
    }
}


private fun Intent?.toNotificationOpenRequest(): NotificationOpenRequest? {
    val source = this ?: return null
    val extras = source.extras
    val explicitOpen = source.getBooleanExtra(NotificationIntentKeys.OPEN_NOTIFICATION, false)
    val alertId = source.getStringExtra(NotificationIntentKeys.ALERT_ID)
        ?: source.getStringExtra("alertId")
        ?: source.getStringExtra("alertID")
        ?: source.getStringExtra("alert_id")
        ?: source.getStringExtra("dispatchId")
    val title = source.getStringExtra(NotificationIntentKeys.TITLE)
        ?: source.getStringExtra("gcm.n.title")
        ?: source.getStringExtra("title")
        ?: source.getStringExtra("subject")
    val body = source.getStringExtra(NotificationIntentKeys.BODY)
        ?: source.getStringExtra("gcm.n.body")
        ?: source.getStringExtra("body")
        ?: source.getStringExtra("message")
    val looksLikeFcmTap = extras?.containsKey("google.message_id") == true ||
            extras?.containsKey("gcm.n.e") == true || alertId != null || title != null || body != null
    if (!explicitOpen && !looksLikeFcmTap) return null

    return NotificationOpenRequest(
        notificationId = source.getStringExtra(NotificationIntentKeys.NOTIFICATION_ID)
            ?: source.getStringExtra("google.message_id"),
        alertId = alertId,
        title = title,
        body = body
    )
}
