
package com.aquila.pocxpertalerts.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.data.model.AlertDto
import com.aquila.pocxpertalerts.data.repository.AlertDynamicAction
import com.aquila.pocxpertalerts.data.repository.AlertDynamicActionRepository
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.components.AppTopBar
import com.aquila.pocxpertalerts.ui.theme.AlertAccentColor
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlertDetailScreen(
    alert: AlertDto,
    onBackClick: () -> Unit,
    userId: String,
    sessionId: String,
    onActionSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val repository = remember {
        AlertDynamicActionRepository()
    }

    val displayedId = if (alert.alertId != 0) {
        alert.alertId.toString()
    } else {
        alert.id.toString()
    }

    val subject = alert.subject
        ?.takeIf { it.isNotBlank() }
        ?: "System Alert"

    // Keep the original HTML from the API. The legacy Android app renders this
    // content in a WebView, so converting it to plain text loses tables, links,
    // formatting, and other HTML layout.
    val messageHtml = remember(alert.message) {
        alert.message?.takeIf { it.isNotBlank() }
            ?: "<html><body><p>No message is available for this alert.</p></body></html>"
    }

    val receivedDate = remember(alert.alertSendDate) {
        formatAlertDate(alert.alertSendDate)
    }

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    var dynamicActions by remember {
        mutableStateOf<List<AlertDynamicAction>>(emptyList())
    }

    var isLoadingActions by remember {
        mutableStateOf(false)
    }

    var selectedAction by remember {
        mutableStateOf<AlertDynamicAction?>(null)
    }

    var confirmationMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isExecutingAction by remember {
        mutableStateOf(false)
    }

    // Execute the exact dynamic action returned by the server.
    // The legacy app skips confirmation when confirmPrompt is "null".
    val executeAction: (AlertDynamicAction) -> Unit = { action ->
        coroutineScope.launch {
            isExecutingAction = true

            try {
                val result = repository.executeAction(
                    userId = userId,
                    sessionId = sessionId,
                    applicationId = alert.applicationId,
                    dispatchId = alert.id,
                    alertId = alert.alertId,
                    aggregateAlertId = alert.aggregateAlertId.orEmpty(),
                    action = action
                )

                result.fold(
                    onSuccess = { responseMessage ->
                        Toast.makeText(
                            context,
                            responseMessage,
                            Toast.LENGTH_LONG
                        ).show()

                        selectedAction = null
                        confirmationMessage = null
                        onActionSuccess()
                        onBackClick()
                    },
                    onFailure = { error ->
                        Log.e(
                            "XPERT_API",
                            "processDynamicActions failed",
                            error
                        )
                        Toast.makeText(
                            context,
                            error.message ?: "Unable to complete action.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            } catch (error: Exception) {
                Log.e(
                    "XPERT_API",
                    "Unexpected action execution error",
                    error
                )
                Toast.makeText(
                    context,
                    error.message ?: "Unable to complete action.",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isExecutingAction = false
            }
        }
    }

    // Load every action configured by the server, as the old app did.
    LaunchedEffect(
        alert.id,
        alert.alertDefId,
        alert.applicationId,
        userId,
        sessionId
    ) {
        Log.d(
            "XPERT_API",
            "Opening alert details: " +
                    "applicationId=${alert.applicationId}, " +
                    "alertDefId=${alert.alertDefId}, " +
                    "alertId=${alert.alertId}"
        )

        isLoadingActions = true

        try {
            val result = repository.getActions(
                userId = userId,
                sessionId = sessionId,
                applicationId = alert.applicationId,
                alertDefId = alert.alertDefId
            )

            result.fold(
                onSuccess = { actions ->
                    dynamicActions = actions

                    Log.d(
                        "XPERT_API",
                        "Actions displayed: " +
                                dynamicActions.map { it.caption }
                    )
                },
                onFailure = { error ->
                    dynamicActions = emptyList()

                    Log.e(
                        "XPERT_API",
                        "Failed to load alert actions",
                        error
                    )

                    Toast.makeText(
                        context,
                        error.message ?: "Unable to load alert actions.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        } catch (error: Exception) {
            dynamicActions = emptyList()

            Log.e(
                "XPERT_API",
                "Unexpected error loading alert actions",
                error
            )
        } finally {
            isLoadingActions = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            // The small Box at the top-right anchors the dropdown
            // beside the three-dot icon instead of at screen-left.
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                AppTopBar(
                    title = "Alert Details",
                    onBackClick = onBackClick,
                    onMoreClick = {
                        Log.d("XPERT_API", "Three-dot menu tapped")
                        menuExpanded = true
                    }
                )

                // IMPORTANT:
                // Keep the dropdown anchored to this small,
                // top-right Box, not the full-width parent Box.
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 4.dp)
                        .size(width = 48.dp, height = 56.dp)
                ) {
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = {
                            menuExpanded = false
                        },
                        modifier = Modifier.widthIn(min = 180.dp)
                    ) {
                        when {
                            isLoadingActions -> {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Loading actions...",
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.Black
                                        )
                                    },
                                    enabled = false,
                                    onClick = {}
                                )
                            }

                            dynamicActions.isEmpty() -> {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "No actions available",
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.Black
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                    }
                                )
                            }

                            else -> {
                                dynamicActions.forEach { action ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = action.caption,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.Black
                                            )
                                        },
                                        onClick = {
                                            menuExpanded = false

                                            val prompt = action.confirmationPrompt.trim()
                                            if (prompt.isBlank() || prompt.equals("null", ignoreCase = true)) {
                                                // Match legacy behavior: no prompt means execute immediately.
                                                executeAction(action)
                                            } else {
                                                selectedAction = action
                                                confirmationMessage = prompt
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
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

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = subject,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

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

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 480.dp),
                        factory = { viewContext ->
                            WebView(viewContext).apply {
                                settings.javaScriptEnabled = true
                                webViewClient = WebViewClient()
                                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                tag = messageHtml
                                loadDataWithBaseURL(
                                    null,
                                    messageHtml,
                                    "text/html",
                                    "UTF-8",
                                    null
                                )
                            }
                        },
                        update = { webView ->
                            if (webView.tag != messageHtml) {
                                webView.tag = messageHtml
                                webView.loadDataWithBaseURL(
                                    null,
                                    messageHtml,
                                    "text/html",
                                    "UTF-8",
                                    null
                                )
                            }
                        }
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

            Spacer(
                modifier = Modifier.height(4.dp)
            )
        }
    }

    // Confirmation dialog for a selected action
    val actionToExecute = selectedAction
    val dialogMessage = confirmationMessage

    if (actionToExecute != null && dialogMessage != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isExecutingAction) {
                    selectedAction = null
                    confirmationMessage = null
                }
            },
            title = {
                Text(actionToExecute.caption)
            },
            text = {
                Text(dialogMessage)
            },
            confirmButton = {
                Button(
                    enabled = !isExecutingAction,
                    onClick = {
                        executeAction(actionToExecute)
                    }
                ) {
                    Text(
                        if (isExecutingAction) {
                            "Please wait..."
                        } else {
                            "Confirm"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isExecutingAction,
                    onClick = {
                        selectedAction = null
                        confirmationMessage = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
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

            val parsedDate: Date =
                parser.parse(rawDate) ?: continue

            return SimpleDateFormat(
                "dd MMM yyyy",
                Locale.US
            ).format(parsedDate)

        } catch (_: ParseException) {
            // Try the next supported date format.
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

        Spacer(
            modifier = Modifier.width(12.dp)
        )

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
                applicationId = 45L,
                alertDefId = 1,
                userId = "1"
            ),
            onBackClick = {},
            onActionSuccess = {},
            userId = "1",
            sessionId = "1"
        )
    }
}
