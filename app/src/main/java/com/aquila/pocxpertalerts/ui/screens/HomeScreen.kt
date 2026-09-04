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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme


// ---------------------------------------------------------
// COLORS
// ---------------------------------------------------------

private val XpertOrange = Color(0xFFED741C)
private val XpertPurple = Color(0xFF3F237D)
private val ScreenBackground = Color(0xFFF7F7F7)
private val DarkText = Color(0xFF222222)
private val GrayText = Color(0xFF777777)


// ---------------------------------------------------------
// HOME SCREEN
// ---------------------------------------------------------

@Composable
fun HomeScreen() {

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var showMenu by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ScreenBackground
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // -------------------------------------------------
            // TOP HEADER
            // -------------------------------------------------

            HomeHeader()

            // -------------------------------------------------
            // MAIN CONTENT
            // -------------------------------------------------

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {

                when (selectedTab) {

                    0 -> AlertsContent()

                    1 -> SubscriptionsContent()

                    2 -> NotificationsContent()

                    3 -> AlertsContent()
                }
            }

            // -------------------------------------------------
            // MENU
            // -------------------------------------------------

            if (showMenu) {

                HomeMenu(
                    onArchivedClick = {
                        // Implement later
                    },

                    onProfileClick = {
                        // Implement later
                    },

                    onPasswordClick = {
                        // Implement later
                    },

                    onLogoutClick = {
                        // Implement later
                    }
                )
            }

            // -------------------------------------------------
            // BOTTOM NAVIGATION
            // -------------------------------------------------

            BottomNavigationBar(
                selectedTab = selectedTab,
                menuSelected = showMenu,

                onTabSelected = { tab ->

                    selectedTab = tab
                    showMenu = false
                },

                onMenuClick = {

                    showMenu = !showMenu
                }
            )
        }
    }
}


// ---------------------------------------------------------
// TOP HEADER
// ---------------------------------------------------------

@Composable
private fun HomeHeader() {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 3.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 20.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Xpert Alerts",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = XpertPurple
                )

                Text(
                    text = "Stay informed. Stay ahead.",
                    fontSize = 12.sp,
                    color = GrayText
                )
            }

            Surface(
                shape = RoundedCornerShape(50.dp),
                color = XpertOrange.copy(alpha = 0.12f)
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.ic_alert_list_bell
                    ),
                    contentDescription = "Alerts",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(26.dp)
                )
            }
        }
    }
}


// ---------------------------------------------------------
// ALERTS CONTENT
// ---------------------------------------------------------

@Composable
private fun AlertsContent() {

    val alerts = listOf(
        AlertItem(
            title = "System Alert",
            description = "New alert received from Xpert Alerts.",
            time = "10 min ago"
        ),

        AlertItem(
            title = "Important Update",
            description = "Please check your latest notifications.",
            time = "30 min ago"
        ),

        AlertItem(
            title = "New Information",
            description = "You have a new alert waiting for you.",
            time = "1 hour ago"
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),

        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            top = 18.dp,
            bottom = 20.dp
        ),

        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {

            Text(
                text = "Alerts",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Your latest alerts",
                fontSize = 14.sp,
                color = GrayText
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        items(alerts) { alert ->

            AlertCard(
                alert = alert
            )
        }
    }
}


// ---------------------------------------------------------
// ALERT CARD
// ---------------------------------------------------------

data class AlertItem(
    val title: String,
    val description: String,
    val time: String
)


@Composable
private fun AlertCard(
    alert: AlertItem
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment = Alignment.Top
        ) {

            Surface(
                modifier = Modifier.size(46.dp),

                shape = RoundedCornerShape(12.dp),

                color = XpertOrange.copy(alpha = 0.12f)
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.ic_alert_list_bell
                    ),

                    contentDescription = "Alert",

                    modifier = Modifier
                        .padding(9.dp)
                        .size(28.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = alert.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = alert.description,
                    fontSize = 14.sp,
                    color = GrayText
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = alert.time,
                    fontSize = 12.sp,
                    color = XpertOrange,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


// ---------------------------------------------------------
// SUBSCRIPTIONS
// ---------------------------------------------------------

@Composable
private fun SubscriptionsContent() {

    EmptyContent(
        icon = R.drawable.ic_alert_list_subscription,
        title = "Subscriptions",
        description = "Your subscribed alerts will appear here."
    )
}


// ---------------------------------------------------------
// NOTIFICATIONS
// ---------------------------------------------------------

@Composable
private fun NotificationsContent() {

    EmptyContent(
        icon = R.drawable.ic_alert_list_bel,
        title = "Notifications",
        description = "Your notifications will appear here."
    )
}


// ---------------------------------------------------------
// EMPTY CONTENT
// ---------------------------------------------------------

@Composable
private fun EmptyContent(
    icon: Int,
    title: String,
    description: String
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface(
                modifier = Modifier.size(80.dp),

                shape = RoundedCornerShape(24.dp),

                color = XpertOrange.copy(alpha = 0.10f)
            ) {

                Image(
                    painter = painterResource(id = icon),

                    contentDescription = title,

                    modifier = Modifier
                        .padding(20.dp)
                        .size(40.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = description,
                fontSize = 14.sp,
                color = GrayText
            )
        }
    }
}


// ---------------------------------------------------------
// HOME MENU
// ---------------------------------------------------------

@Composable
private fun HomeMenu(
    onArchivedClick: () -> Unit,
    onProfileClick: () -> Unit,
    onPasswordClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {

        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),

            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            MenuItem(
                icon = R.drawable.archivedalerts,
                text = "Archived",
                onClick = onArchivedClick
            )

            MenuItem(
                icon = R.drawable.profile,
                text = "Profile",
                onClick = onProfileClick
            )

            MenuItem(
                icon = R.drawable.changepassword,
                text = "Password",
                onClick = onPasswordClick
            )

            MenuItem(
                icon = R.drawable.new_logout,
                text = "Logout",
                onClick = onLogoutClick
            )
        }

        HorizontalDivider()
    }
}


// ---------------------------------------------------------
// MENU ITEM
// ---------------------------------------------------------

@Composable
private fun MenuItem(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = icon),

            contentDescription = text,

            modifier = Modifier.size(27.dp)
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = text,
            fontSize = 11.sp,
            color = DarkText
        )
    }
}


// ---------------------------------------------------------
// BOTTOM NAVIGATION
// ---------------------------------------------------------

@Composable
private fun BottomNavigationBar(
    selectedTab: Int,
    menuSelected: Boolean,
    onTabSelected: (Int) -> Unit,
    onMenuClick: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(62.dp)
        ) {

            BottomTab(
                icon = R.drawable.ic_alert_list_bell,
                selected = selectedTab == 0 && !menuSelected,
                onClick = {
                    onTabSelected(0)
                },
                modifier = Modifier.weight(1f)
            )

            BottomTab(
                icon = R.drawable.ic_alert_list_subscription,
                selected = selectedTab == 1 && !menuSelected,
                onClick = {
                    onTabSelected(1)
                },
                modifier = Modifier.weight(1f)
            )

            BottomTab(
                icon = R.drawable.ic_alert_list_bel,
                selected = selectedTab == 2 && !menuSelected,
                onClick = {
                    onTabSelected(2)
                },
                modifier = Modifier.weight(1f)
            )

            BottomTab(
                icon = R.drawable.ic_alert_list_menu,
                selected = menuSelected,
                onClick = onMenuClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


// ---------------------------------------------------------
// BOTTOM TAB
// ---------------------------------------------------------

@Composable
private fun BottomTab(
    icon: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                onClick()
            },

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Image(
            painter = painterResource(id = icon),

            contentDescription = null,

            modifier = Modifier.size(28.dp)
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(3.dp)
                .background(
                    if (selected) {
                        XpertOrange
                    } else {
                        Color.Transparent
                    }
                )
        )
    }
}


// ---------------------------------------------------------
// PREVIEW
// ---------------------------------------------------------

@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 360,
    heightDp = 760
)
@Composable
fun HomeScreenPreview() {

    XpertAlertsTheme {
        HomeScreen()
    }
}