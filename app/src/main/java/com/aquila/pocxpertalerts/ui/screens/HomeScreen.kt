
package com.aquila.pocxpertalerts.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
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
    var showMenu by remember { mutableStateOf(false) }

    BackHandler(enabled = showMenu) {
        showMenu = false
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

            // Orange toolbar extends behind the status bar.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(XpertOrange)
            ) {
                HomeHeader(
                    onMenuClick = {
                        showMenu = !showMenu
                    },
                    modifier = Modifier.statusBarsPadding()
                )
            }

            // Main alerts area.
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

            // Six menu items appear when Menu is selected.
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

            // Bottom navigation remains visible.
            BottomNavigationBar(
                menuSelected = showMenu,
                onSubscriptionsClick = onSubscriptionsClick,
                onNotificationsClick = onNotificationsClick,
                onMenuClick = {
                    showMenu = !showMenu
                }
            )
        }
    }
}

// ============================================================
// TOP TOOLBAR
// ============================================================

@Composable
private fun HomeHeader(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(XpertOrange)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Alerts logo.
        Image(
            painter = painterResource(
                id = R.drawable.ic_alert_list_bel
            ),
            contentDescription = "Alerts logo",
            modifier = Modifier
                .size(40.dp)
                .background(White, CircleShape)
                .padding(4.dp)
        )

        // Center title.
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Alerts List",
                color = White,
                fontSize = 18.sp
            )
        }

        // Three-dot menu.
        Box(
            modifier = Modifier
                .size(36.dp)
                .clickable(onClick = onMenuClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⋮",
                color = White,
                fontSize = 25.sp
            )
        }
    }
}

// ============================================================
// ALERTS CONTENT AND SEARCH BAR
// ============================================================

@Composable
fun AlertsContent(
    onAlertClick: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {

        // Entire search bar is clickable.
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 18.dp
                )
                .height(62.dp)
                .clickable(onClick = onSearchClick),
            shape = RoundedCornerShape(32.dp),
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
                    text = "Search Alerts",
                    modifier = Modifier.weight(1f),
                    color = TextGray,
                    fontSize = 18.sp
                )

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Alerts",
                    tint = TextGray,
                    modifier = Modifier.size(27.dp)
                )
            }
        }

        // Blank area until alert data is available.
        Spacer(
            modifier = Modifier.weight(1f)
        )
    }
}

// ============================================================
// EXPANDED SIX-ICON MENU
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

        HorizontalDivider(
            color = TextGray.copy(alpha = 0.25f)
        )

        // First row: Archived, Forward, Profile.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MenuItem(
                icon = R.drawable.archivedalerts,
                text = "Archived",
                onClick = onArchivedClick,
                modifier = Modifier.weight(1f)
            )

            MenuItem(
                icon = R.drawable.ic_alert_list_bel,
                text = "Forward",
                onClick = onForwardAlertsClick,
                modifier = Modifier.weight(1f)
            )

            MenuItem(
                icon = R.drawable.profile,
                text = "Profile",
                onClick = onProfileClick,
                modifier = Modifier.weight(1f)
            )
        }

        // Second row: Password, About, Logout.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MenuItem(
                icon = R.drawable.changepassword,
                text = "Password",
                onClick = onPasswordClick,
                modifier = Modifier.weight(1f)
            )

            MenuItem(
                icon = null,
                text = "About",
                onClick = onAboutClick,
                vectorIcon = Icons.Default.Info,
                modifier = Modifier.weight(1f)
            )

            MenuItem(
                icon = R.drawable.new_logout,
                text = "Logout",
                onClick = onLogoutClick,
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(
            color = TextGray.copy(alpha = 0.25f)
        )
    }
}

// ============================================================
// INDIVIDUAL MENU ITEM
// ============================================================

@Composable
private fun MenuItem(
    icon: Int?,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    vectorIcon: ImageVector? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (vectorIcon != null) {
            Icon(
                imageVector = vectorIcon,
                contentDescription = text,
                tint = XpertPurple,
                modifier = Modifier.size(24.dp)
            )
        } else if (icon != null) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = text,
            fontSize = 11.sp,
            color = TextDark
        )
    }
}

// ============================================================
// BOTTOM NAVIGATION BAR
// ============================================================

@Composable
private fun BottomNavigationBar(
    menuSelected: Boolean,
    onSubscriptionsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(44.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Alerts tab.
            BottomTab(
                icon = R.drawable.ic_alert_list_bell,
                selected = !menuSelected,
                onClick = {},
                modifier = Modifier.weight(1f)
            )

            // Subscriptions tab.
            BottomTab(
                icon = R.drawable.ic_alert_list_subscription,
                selected = false,
                onClick = onSubscriptionsClick,
                modifier = Modifier.weight(1f)
            )

            // Notifications tab.
            BottomTab(
                icon = R.drawable.ic_alert_list_bel,
                selected = false,
                onClick = onNotificationsClick,
                modifier = Modifier.weight(1f)
            )

            // Menu tab.
            BottomTab(
                icon = R.drawable.ic_alert_list_menu,
                selected = menuSelected,
                onClick = onMenuClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ============================================================
// BOTTOM NAVIGATION TAB
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
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        // Selected tab indicator.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    if (selected) XpertOrange else White
                )
        )
    }
}

// ============================================================
// PREVIEW
// ============================================================

@Preview(
    showBackground = true,
    showSystemUi = true
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
            onLogoutClick = {},
            onAboutClick = {}
        )
    }
}
