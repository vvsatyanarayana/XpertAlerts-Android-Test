package com.aquila.pocxpertalerts.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.ui.XpertAlertsTheme
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import com.aquila.pocxpertalerts.ui.theme.XpertPurple
import kotlinx.coroutines.delay


// ============================================================
// HOME SCREEN
// ============================================================

@Composable
fun HomeScreen(
    onAlertClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSubscriptionsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onArchivedClick: () -> Unit,
    onForwardAlertsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onPasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAboutClick: () -> Unit
) {

    // ========================================================
    // MENU STATE
    // ========================================================

    var showMenu by remember {
        mutableStateOf(false)
    }


    // ========================================================
    // ANDROID BACK BUTTON
    // ========================================================
    //
    // Notifications is now a separate navigation screen.
    //
    // Therefore Home only needs to handle the Menu here.
    //
    // If Menu is open:
    //
    // Home → Menu → Back → Home
    //
    // If Menu is not open:
    //
    // Android Navigation handles Back normally.
    // ========================================================

    BackHandler(
        enabled = showMenu
    ) {
        showMenu = false
    }


    // ========================================================
    // SCREEN
    // ========================================================

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppBackground
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {


            // ==================================================
            // HEADER
            // ==================================================

            HomeHeader()


            // ==================================================
            // MAIN ALERT CONTENT
            // ==================================================

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {

                AlertsContent(
                    onAlertClick = onAlertClick,
                    onSearchClick = onSearchClick
                )
            }


            // ==================================================
            // MENU
            // ==================================================

            if (showMenu) {

                HomeMenu(
                    onArchivedClick = {
                        showMenu = false
                        onArchivedClick()
                    },

                    onForwardAlertsClick = {
                        showMenu = false
                        onForwardAlertsClick()
                    },

                    onProfileClick = {
                        showMenu = false
                        onProfileClick()
                    },

                    onPasswordClick = {
                        showMenu = false
                        onPasswordClick()
                    },

                    onAboutClick = {
                        showMenu = false
                        onAboutClick()
                    },

                    onLogoutClick = {
                        showMenu = false
                        onLogoutClick()
                    }
                )
            }


            // ==================================================
            // BOTTOM NAVIGATION
            // ==================================================

            BottomNavigationBar(
                menuSelected = showMenu,

                onSubscriptionsClick = {
                    onSubscriptionsClick()
                },

                onNotificationsClick = {
                    onNotificationsClick()
                },

                onMenuClick = {
                    showMenu = !showMenu
                }
            )
        }
    }
}


// ============================================================
// HOME HEADER
// ============================================================

@Composable
private fun HomeHeader() {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = White,
        shadowElevation = 3.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 20.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {


            // ==================================================
            // TITLE
            // ==================================================

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
                    color = TextGray
                )
            }


            // ==================================================
            // HEADER ALERT ICON
            // ==================================================

            Surface(
                shape = RoundedCornerShape(50.dp),
                color = XpertOrange.copy(alpha = 0.12f)
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.ic_alert_list_bel
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


// ============================================================
// ALERTS CONTENT
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsContent(
    onAlertClick: (String) -> Unit,
    onSearchClick: () -> Unit
) {

    // ========================================================
    // MULTI SELECT STATE
    // ========================================================

    var selectedAlertIds by remember {
        mutableStateOf(setOf<String>())
    }

    val selectionMode =
        selectedAlertIds.isNotEmpty()


    // ========================================================
    // REFRESH STATE
    // ========================================================

    var isRefreshing by remember {
        mutableStateOf(false)
    }

    val refreshState =
        rememberPullToRefreshState()


    // ========================================================
    // SIMULATED REFRESH
    //
    // UI ONLY
    // ========================================================

    LaunchedEffect(isRefreshing) {

        if (isRefreshing) {

            delay(1500)

            isRefreshing = false
        }
    }


    // ========================================================
    // MOCK ALERT DATA
    //
    // UI ONLY FOR NOW
    // API WILL BE ADDED LATER.
    // ========================================================

    val alerts = listOf(

        AlertItem(
            id = "1",
            subject = "System Alert",
            message = "New alert received from Xpert Alerts.",
            time = "10 min ago",
            isUnread = true
        ),

        AlertItem(
            id = "2",
            subject = "System Alert",
            message = "System maintenance notification received.",
            time = "25 min ago",
            isUnread = false
        ),

        AlertItem(
            id = "3",
            subject = "Important Update",
            message = "Please check your latest notifications.",
            time = "30 min ago",
            isUnread = true
        ),

        AlertItem(
            id = "4",
            subject = "New Information",
            message = "You have a new alert waiting for you.",
            time = "1 hour ago",
            isUnread = false
        ),

        AlertItem(
            id = "5",
            subject = "New Information",
            message = "A new information update is available.",
            time = "2 hours ago",
            isUnread = true
        )
    )


    // ========================================================
    // GROUP ALERTS
    // ========================================================

    val groupedAlerts =
        alerts.groupBy {
            it.subject
        }


    // ========================================================
    // PULL TO REFRESH
    // ========================================================

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        state = refreshState,

        onRefresh = {
            isRefreshing = true
        }
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),

            contentPadding = PaddingValues(
                top = 18.dp,
                bottom = 20.dp
            ),

            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {


            // ==================================================
            // ALERT HEADER
            // ==================================================

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "Alerts",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        Text(
                            text = "Your latest alerts",
                            fontSize = 14.sp,
                            color = TextGray
                        )
                    }


                    // ==================================================
                    // SEARCH BUTTON
                    // ==================================================

                    IconButton(
                        onClick = onSearchClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Search,

                            contentDescription =
                                "Search Alerts",

                            tint =
                                XpertPurple
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }


            // ==================================================
            // GROUPED ALERTS
            // ==================================================

            groupedAlerts.forEach { (subject, subjectAlerts) ->


                // ==================================================
                // SUBJECT HEADER
                // ==================================================

                item(
                    key = "subject_$subject"
                ) {

                    Text(
                        text =
                            subject.uppercase(),

                        fontSize =
                            13.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            XpertPurple,

                        modifier =
                            Modifier.padding(
                                top = 8.dp,
                                bottom = 2.dp
                            )
                    )
                }


                // ==================================================
                // ALERT ITEMS
                // ==================================================

                items(
                    items = subjectAlerts,

                    key = { alert ->
                        alert.id
                    }
                ) { alert ->

                    AlertCard(
                        alert = alert,

                        selected =
                            selectedAlertIds.contains(
                                alert.id
                            ),

                        selectionMode =
                            selectionMode,

                        onClick = {

                            if (selectionMode) {

                                selectedAlertIds =
                                    if (
                                        selectedAlertIds.contains(
                                            alert.id
                                        )
                                    ) {

                                        selectedAlertIds -
                                                alert.id

                                    } else {

                                        selectedAlertIds +
                                                alert.id
                                    }

                            } else {

                                onAlertClick(
                                    alert.id
                                )
                            }
                        },

                        onLongClick = {

                            selectedAlertIds =
                                selectedAlertIds +
                                        alert.id
                        }
                    )
                }
            }
        }
    }
}


// ============================================================
// ALERT MODEL
// ============================================================

data class AlertItem(
    val id: String,
    val subject: String,
    val message: String,
    val time: String,
    val isUnread: Boolean
)


// ============================================================
// ALERT CARD
// ============================================================

@OptIn(
    androidx.compose.foundation.ExperimentalFoundationApi::class
)
@Composable
private fun AlertCard(
    alert: AlertItem,
    selected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (selected) {

                        XpertOrange.copy(
                            alpha = 0.08f
                        )

                    } else {

                        White
                    }
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment =
                Alignment.Top
        ) {


            // ==================================================
            // SELECTION CHECK
            // ==================================================

            if (selectionMode) {

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            color =
                                if (selected) {

                                    XpertPurple

                                } else {

                                    White.copy(
                                        alpha = 0f
                                    )
                                },

                            shape =
                                CircleShape
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    if (selected) {

                        Icon(
                            imageVector =
                                Icons.Default.Done,

                            contentDescription =
                                "Selected",

                            tint =
                                White,

                            modifier =
                                Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )
            }


            // ==================================================
            // ALERT ICON
            // ==================================================

            Surface(
                modifier =
                    Modifier.size(46.dp),

                shape =
                    RoundedCornerShape(12.dp),

                color =
                    XpertOrange.copy(
                        alpha = 0.12f
                    )
            ) {

                Image(
                    painter =
                        painterResource(
                            id =
                                R.drawable
                                    .ic_alert_list_bell
                        ),

                    contentDescription =
                        "Alert",

                    modifier =
                        Modifier
                            .padding(9.dp)
                            .size(28.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.width(14.dp)
            )


            // ==================================================
            // ALERT INFORMATION
            // ==================================================

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            alert.message,

                        modifier =
                            Modifier.weight(1f),

                        fontSize =
                            14.sp,

                        fontWeight =
                            if (alert.isUnread) {

                                FontWeight.Bold

                            } else {

                                FontWeight.Normal
                            },

                        color =
                            TextDark
                    )


                    // ==================================================
                    // UNREAD INDICATOR
                    // ==================================================

                    if (alert.isUnread) {

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Box(
                            modifier =
                                Modifier
                                    .size(9.dp)
                                    .background(
                                        XpertOrange,
                                        CircleShape
                                    )
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                Text(
                    text =
                        alert.time,

                    fontSize =
                        12.sp,

                    color =
                        XpertOrange,

                    fontWeight =
                        FontWeight.Medium
                )
            }
        }
    }
}


// ============================================================
// HOME MENU
// ============================================================

@Composable
private fun HomeMenu(
    onArchivedClick: () -> Unit,
    onForwardAlertsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onPasswordClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
    ) {

        HorizontalDivider()


        // ==================================================
        // FIRST ROW
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp
                ),

            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {

            MenuItem(
                icon =
                    R.drawable.archivedalerts,

                text =
                    "Archived",

                onClick =
                    onArchivedClick,

                modifier =
                    Modifier.weight(1f)
            )


            MenuItem(
                icon =
                    R.drawable.ic_alert_list_bel,

                text =
                    "Forward",

                onClick =
                    onForwardAlertsClick,

                modifier =
                    Modifier.weight(1f)
            )


            MenuItem(
                icon =
                    R.drawable.profile,

                text =
                    "Profile",

                onClick =
                    onProfileClick,

                modifier =
                    Modifier.weight(1f)
            )
        }


        // ==================================================
        // SECOND ROW
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp
                ),

            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {

            MenuItem(
                icon =
                    R.drawable.changepassword,

                text =
                    "Password",

                onClick =
                    onPasswordClick,

                modifier =
                    Modifier.weight(1f)
            )


            MenuItem(
                icon = null,

                text =
                    "About",

                onClick =
                    onAboutClick,

                vectorIcon =
                    Icons.Default.Info,

                modifier =
                    Modifier.weight(1f)
            )


            MenuItem(
                icon =
                    R.drawable.new_logout,

                text =
                    "Logout",

                onClick =
                    onLogoutClick,

                modifier =
                    Modifier.weight(1f)
            )
        }


        HorizontalDivider()
    }
}


// ============================================================
// MENU ITEM
// ============================================================

@Composable
private fun MenuItem(
    icon: Int?,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    vectorIcon:
    androidx.compose.ui.graphics.vector.ImageVector? = null
) {

    Column(
        modifier = modifier
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 4.dp,
                vertical = 6.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {


        // ==================================================
        // ICON
        // ==================================================

        if (vectorIcon != null) {

            Icon(
                imageVector =
                    vectorIcon,

                contentDescription =
                    text,

                tint =
                    XpertPurple,

                modifier =
                    Modifier.size(27.dp)
            )

        } else if (icon != null) {

            Image(
                painter =
                    painterResource(
                        id = icon
                    ),

                contentDescription =
                    text,

                modifier =
                    Modifier.size(27.dp)
            )
        }


        Spacer(
            modifier =
                Modifier.height(4.dp)
        )


        // ==================================================
        // TEXT
        // ==================================================

        Text(
            text =
                text,

            fontSize =
                11.sp,

            color =
                TextDark
        )
    }
}


// ============================================================
// BOTTOM NAVIGATION
// ============================================================

@Composable
private fun BottomNavigationBar(
    menuSelected: Boolean,
    onSubscriptionsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onMenuClick: () -> Unit
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),

        color =
            White,

        shadowElevation =
            8.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(62.dp)
        ) {


            // ==================================================
            // ALERTS
            // ==================================================

            BottomTab(
                icon =
                    R.drawable.ic_alert_list_bell,

                selected =
                    !menuSelected,

                onClick = {
                    // Already on Home / Alerts.
                },

                modifier =
                    Modifier.weight(1f)
            )


            // ==================================================
            // SUBSCRIPTIONS
            // ==================================================

            BottomTab(
                icon =
                    R.drawable.ic_alert_list_subscription,

                selected = false,

                onClick =
                    onSubscriptionsClick,

                modifier =
                    Modifier.weight(1f)
            )


            // ==================================================
            // NOTIFICATIONS
            // ==================================================

            BottomTab(
                icon =
                    R.drawable.ic_alert_list_bel,

                selected = false,

                onClick =
                    onNotificationsClick,

                modifier =
                    Modifier.weight(1f)
            )


            // ==================================================
            // MENU
            // ==================================================

            BottomTab(
                icon =
                    R.drawable.ic_alert_list_menu,

                selected =
                    menuSelected,

                onClick =
                    onMenuClick,

                modifier =
                    Modifier.weight(1f)
            )
        }
    }
}


// ============================================================
// BOTTOM TAB
// ============================================================

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

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        Image(
            painter =
                painterResource(
                    id = icon
                ),

            contentDescription =
                null,

            modifier =
                Modifier.size(28.dp)
        )


        Spacer(
            modifier =
                Modifier.weight(1f)
        )


        // ==================================================
        // SELECTED INDICATOR
        // ==================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp
                )
                .height(3.dp)
                .background(
                    if (selected) {
                        XpertOrange
                    } else {
                        White.copy(alpha = 0f)
                    }
                )
        )
    }
}


// ============================================================
// PREVIEW
// ============================================================

@Preview(
    showBackground = true,
    showSystemUi = true,

)
@Composable
fun HomeScreenPreview() {

    XpertAlertsTheme {

        HomeScreen(

            onAlertClick = {},

            onSearchClick = {},

            onSubscriptionsClick = {},

            onNotificationsClick = {},

            onArchivedClick = {},

            onForwardAlertsClick = {},

            onProfileClick = {},

            onPasswordClick = {},

            onAboutClick = {},

            onLogoutClick = {}
        )
    }
}