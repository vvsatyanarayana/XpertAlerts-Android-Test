package com.aquila.pocxpertalerts.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import androidx.compose.foundation.layout.Box

private val SubscriptionPurple = Color(0xFF6A4BBC)
private val SubscriptionOrange = Color(0xFFFF8A00)
private val SubscriptionDarkText = Color(0xFF222222)
private val SubscriptionGrayText = Color(0xFF777777)

data class SubscriptionItem(
    val id: String,
    val alertName: String,
    val application: String,
    val email: String,
    val active: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    onBackClick: () -> Unit,
    onCreateClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onSearchClick: () -> Unit
) {

    // Temporary UI data.
    // This will later be replaced with API data.
    val subscriptions = listOf(
        SubscriptionItem(
            id = "subscription_1",
            alertName = "System Alert",
            application = "Xpert Application",
            email = "user@example.com",
            active = true
        ),
        SubscriptionItem(
            id = "subscription_2",
            alertName = "Important Update",
            application = "Xpert Application",
            email = "user@example.com",
            active = true
        ),
        SubscriptionItem(
            id = "subscription_3",
            alertName = "New Information",
            application = "Another Application",
            email = "admin@example.com",
            active = false
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Subscriptions",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                actions = {
                    IconButton(
                        onClick = onCreateClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Subscription",
                            tint = SubscriptionPurple
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        if (subscriptions.isEmpty()) {

            SubscriptionEmptyState(
                modifier = Modifier.padding(innerPadding)
            )

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                // =====================================================
                // SEARCH SUBSCRIPTIONS
                // =====================================================

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp
                        )
                        .clickable {
                            onSearchClick()
                        }
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        enabled = false,
                        singleLine = true,
                        placeholder = {
                            Text("Search Subscriptions")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Subscriptions"
                            )
                        }
                    )
                }
                // =====================================================
                // SUBSCRIPTION LIST
                // =====================================================

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),

                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 20.dp
                    ),

                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = subscriptions,
                        key = { it.id }
                    ) { subscription ->

                        SubscriptionCard(
                            subscription = subscription,
                            onEditClick = {
                                onEditClick(subscription.id)
                            }
                        )
                    }
                }
            }
        }
    }
}


// ================================================================
// SUBSCRIPTION CARD
// ================================================================

@Composable
private fun SubscriptionCard(
    subscription: SubscriptionItem,
    onEditClick: () -> Unit
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            // =====================================================
            // TITLE + EDIT
            // =====================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = subscription.alertName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SubscriptionDarkText
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = subscription.application,
                        fontSize = 13.sp,
                        color = SubscriptionGrayText
                    )
                }

                IconButton(
                    onClick = onEditClick
                ) {

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Subscription",
                        tint = SubscriptionPurple
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // =====================================================
            // EMAIL
            // =====================================================

            Text(
                text = "Email: ${subscription.email}",
                fontSize = 13.sp,
                color = SubscriptionGrayText
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // =====================================================
            // ACTIVE / INACTIVE
            // =====================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (subscription.active) {
                        "Active"
                    } else {
                        "Inactive"
                    },

                    modifier = Modifier.weight(1f),

                    fontSize = 13.sp,

                    fontWeight = FontWeight.Medium,

                    color = if (subscription.active) {
                        SubscriptionPurple
                    } else {
                        SubscriptionGrayText
                    }
                )

                Switch(
                    checked = subscription.active,

                    onCheckedChange = {
                        // UI only for now.
                        // API integration will persist this later.
                    }
                )
            }
        }
    }
}


// ================================================================
// EMPTY STATE
// ================================================================

@Composable
private fun SubscriptionEmptyState(
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxSize(),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "No records found",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SubscriptionDarkText
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "There are no subscriptions.",
            fontSize = 14.sp,
            color = SubscriptionGrayText
        )
    }
}


// ================================================================
// PREVIEW
// ================================================================

@Preview(
    showBackground = true,
    showSystemUi = true,

)
@Composable
fun SubscriptionsScreenPreview() {

    XpertAlertsTheme {

        SubscriptionsScreen(
            onBackClick = {},
            onCreateClick = {},
            onEditClick = {},
            onSearchClick = {}
        )
    }
}