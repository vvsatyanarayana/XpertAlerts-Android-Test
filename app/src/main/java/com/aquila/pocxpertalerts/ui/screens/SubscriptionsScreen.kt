
package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.data.model.SubscriptionDto
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import com.aquila.pocxpertalerts.ui.theme.XpertPurple
import com.aquila.pocxpertalerts.viewmodel.SubscriptionsViewModel
import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
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

    SideEffect {
        val activity = view.context as? Activity
        activity?.window?.let { window ->
            window.statusBarColor = XpertOrange.toArgb()

            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = false
        }
    }

    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(userId, sessionId) {
        viewModel.loadSubscriptions(
            userId = userId,
            sessionId = sessionId,
            applicationId = "0",
            searchName = "",
            searchActive = 2
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackground
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // Toolbar: same height and orange theme as HomeScreen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(XpertOrange)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "My Subscriptions",
                        color = White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }

                Box(
                    modifier = Modifier
                        .width(68.dp)
                        .fillMaxHeight()
                        .clickable(onClick = onCreateClick),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "Create",
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(White)
            ) {

                // Search bar styled consistently with HomeScreen
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 18.dp,
                            end = 18.dp,
                            top = 18.dp,
                            bottom = 14.dp
                        )
                        .height(54.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = White,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.5.dp,
                        color = XpertPurple
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchText.isEmpty()) {
                                Text(
                                    text = "Search Subscriptions",
                                    color = TextGray,
                                    fontSize = 16.sp
                                )
                            }

                            BasicTextField(
                                value = searchText,
                                onValueChange = {
                                    searchText = it
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = Color.DarkGray,
                                    fontSize = 16.sp
                                ),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        viewModel.loadSubscriptions(
                                            userId = userId,
                                            sessionId = sessionId,
                                            applicationId = "0",
                                            searchName = searchText.trim(),
                                            searchActive = 2
                                        )
                                    }
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search subscriptions",
                            tint = TextGray,
                            modifier = Modifier
                                .size(25.dp)
                                .clickable {
                                    viewModel.loadSubscriptions(
                                        userId = userId,
                                        sessionId = sessionId,
                                        applicationId = "0",
                                        searchName = searchText.trim(),
                                        searchActive = 2
                                    )
                                }
                        )
                    }
                }

                // Subscription list, loading, and error states
                when {
                    uiState.isLoading -> {
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
                                modifier = Modifier.clickable {
                                    viewModel.loadSubscriptions(
                                        userId = userId,
                                        sessionId = sessionId,
                                        applicationId = "0",
                                        searchName = searchText.trim(),
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
                                modifier = Modifier.padding(top = 24.dp)
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
                                key = { it.userSubId.toString() }
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
}

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
                .defaultMinSize(minHeight = 58.dp)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = alertName,
                    color = Color(0xFF555555),
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    maxLines = 2
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
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

// Preview using the project's existing theme

@Preview(
    name = "XpertAlerts - My Subscriptions",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=873dp,dpi=440"
)
@Composable
private fun SubscriptionsScreenPreview() {
    XpertAlertsTheme {
        SubscriptionLayoutPreview(
            onSearchClick = {}
        )
    }
}

@Composable
private fun SubscriptionLayoutPreview(
    onSearchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {

        // ---------------------------------------------
        // TOP TOOLBAR - SAME HEIGHT AS HOME SCREEN
        // ---------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(XpertOrange)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { }
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "My Subscriptions",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier
                    .width(64.dp)
                    .fillMaxHeight()
                    .clickable { },
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Create",
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // ---------------------------------------------
        // SEARCH BAR - SAME STYLE AS HOME SCREEN
        // ---------------------------------------------

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 18.dp,
                    bottom = 18.dp
                )
                .height(62.dp)
                .clickable {
                    onSearchClick()
                },
            shape = RoundedCornerShape(28.dp),
            color = White,
            border = BorderStroke(
                width = 1.5.dp,
                color = XpertPurple
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Search Subscriptions",
                    modifier = Modifier.weight(1f),
                    color = TextGray,
                    fontSize = 18.sp
                )

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Subscriptions",
                    tint = TextGray,
                    modifier = Modifier.size(27.dp)
                )
            }
        }

        // ---------------------------------------------
        // SUBSCRIPTION LIST - PREVIEW ONLY
        // ---------------------------------------------

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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(White),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(previewRows) { row ->

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(start = 26.dp, end = 12.dp)
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
                                color = Color(0xFF555555),
                                fontSize = 14.sp,
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = row.second,
                                color = TextGray,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }

                        Text(
                            text = "›",
                            color = TextGray,
                            fontSize = 30.sp,
                            lineHeight = 30.sp,
                            modifier = Modifier.padding(start = 8.dp)
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
