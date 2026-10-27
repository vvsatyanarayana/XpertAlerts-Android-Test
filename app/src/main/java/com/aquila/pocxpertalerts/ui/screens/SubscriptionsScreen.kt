
package com.aquila.pocxpertalerts.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.aquila.pocxpertalerts.data.model.SubscriptionDto
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.components.AppListSearchBar
import com.aquila.pocxpertalerts.ui.components.AppTopBar
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import com.aquila.pocxpertalerts.viewmodel.SubscriptionsViewModel
import androidx.compose.foundation.layout.WindowInsets
@Composable
fun SubscriptionsScreen(
    onBackClick: () -> Unit,
    onCreateClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: SubscriptionsViewModel,
    userId: String,
    sessionId: String
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current

    // Keep the status bar orange.
    SideEffect {
        val activity = view.context as? Activity

        activity?.window?.let { window ->
            window.statusBarColor = XpertOrange.toArgb()

            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = false
        }
    }

    // Load subscriptions when the user/session changes.
    LaunchedEffect(userId, sessionId) {
        viewModel.loadInitialSubscriptions(
            userId = userId,
            sessionId = sessionId
        )
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(White)
        ) {
            AppTopBar(
                title = "My Subscriptions",
                onBackClick = onBackClick,
                trailingActionText = "Create",
                onTrailingActionClick = onCreateClick
            )

            AppListSearchBar(
                placeholder = "Search Subscriptions",
                onClick = onSearchClick,
                modifier = Modifier.padding(
                    start = 15.dp,
                    end = 15.dp,
                    top = 14.dp,
                    bottom = 8.dp
                )
            )

            // Loading, error, empty state, or subscription list.
            when {
                uiState.isLoading &&
                        uiState.subscriptions.isEmpty() -> {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = XpertOrange
                        )
                    }
                }

                uiState.errorMessage != null -> {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.errorMessage
                                ?: "Unable to load subscriptions",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Retry",
                            color = XpertOrange,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                viewModel.loadSubscriptions(
                                    userId = userId,
                                    sessionId = sessionId,
                                    applicationId = "0",
                                    searchName = "",
                                    searchActive = 2
                                )
                            }
                        )
                    }
                }

                uiState.subscriptions.isEmpty() -> {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "No subscriptions found",
                            color = TextGray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(
                                top = 24.dp
                            )
                        )
                    }
                }

                else -> {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(
                            bottom = 12.dp
                        )
                    ) {
                        items(
                            items = uiState.subscriptions,
                            key = { subscription ->
                                subscription.userSubId.toString()
                            }
                        ) { subscription ->

                            SubscriptionRow(
                                subscription = subscription,
                                onClick = {
                                    onEditClick(
                                        subscription.userSubId.toString()
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------
// SUBSCRIPTION ROW
// ------------------------------------------------------------

@Composable
private fun SubscriptionRow(
    subscription: SubscriptionDto,
    onClick: () -> Unit
) {
    val alertName = subscription.alertName
        ?.takeIf { it.isNotBlank() }
        ?: subscription.alertDefName
            ?.takeIf { it.isNotBlank() }
        ?: "Subscription"

    val applicationName = subscription.applicationName
        ?.takeIf { it.isNotBlank() }
        ?: "Application"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                start = 26.dp,
                end = 12.dp
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 66.dp)
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = alertName,
                    color = TextGray,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    maxLines = 2
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = applicationName,
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 1
                )
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "›",
                color = TextGray,
                fontSize = 28.sp,
                lineHeight = 28.sp
            )
        }

        HorizontalDivider(
            color = TextGray.copy(alpha = 0.20f),
            thickness = 1.dp
        )
    }
}

// ------------------------------------------------------------
// PREVIEW
// ------------------------------------------------------------

@Preview(
    name = "XpertAlerts - My Subscriptions",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=873dp,dpi=440"
)
@Composable
private fun SubscriptionsScreenPreview() {
    XpertAlertsTheme {
        SubscriptionLayoutPreview()
    }
}

@Composable
private fun SubscriptionLayoutPreview() {
    val previewRows = listOf(
        "Caterxpert - alcohol_change_request_0_30" to "CaterXpert",
        "CaterXpert - Alcohol_Change_Request_0_30" to "CaterXpert",
        "Caterxpert - broadcast email" to "CaterXpert",
        "CaterXpert - Database Alert" to "CaterXpert",
        "CaterXpert - Server Alert" to "CaterXpert",
        "CaterXpert - Network Alert" to "CaterXpert",
        "CaterXpert - Application Alert" to "CaterXpert",
        "CaterXpert - System Monitoring" to "CaterXpert"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        AppTopBar(
            title = "My Subscriptions",
            onBackClick = {},
            trailingActionText = "Create",
            onTrailingActionClick = {}
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
        ) {
            AppListSearchBar(
                placeholder = "Search Subscriptions",
                onClick = {},
                modifier = Modifier.padding(
                    start = 15.dp,
                    end = 15.dp,
                    top = 14.dp,
                    bottom = 8.dp
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(White),
                contentPadding = PaddingValues(
                    bottom = 12.dp
                )
            ) {
                items(previewRows) { row ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 26.dp,
                                end = 12.dp
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 66.dp)
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = row.first,
                                    color = TextGray,
                                    fontSize = 14.sp,
                                    lineHeight = 19.sp,
                                    maxLines = 2
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = row.second,
                                    color = TextGray,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    maxLines = 1
                                )
                            }

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = "›",
                                color = TextGray,
                                fontSize = 28.sp,
                                lineHeight = 28.sp
                            )
                        }

                        HorizontalDivider(
                            color = TextGray.copy(alpha = 0.20f),
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}
