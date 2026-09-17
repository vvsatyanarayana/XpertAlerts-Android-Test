package com.aquila.pocxpertalerts.navigation

import android.os.SystemClock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

import com.aquila.pocxpertalerts.ui.screens.AboutScreen
import com.aquila.pocxpertalerts.ui.screens.AlertDetailScreen
import com.aquila.pocxpertalerts.ui.screens.ArchivedScreen
import com.aquila.pocxpertalerts.ui.screens.ChangePasswordScreen
import com.aquila.pocxpertalerts.ui.screens.ForwardAlertDetailsScreen
import com.aquila.pocxpertalerts.ui.screens.ForwardAlertsScreen
import com.aquila.pocxpertalerts.ui.screens.HomeScreen
import com.aquila.pocxpertalerts.ui.screens.LoginScreen
import com.aquila.pocxpertalerts.ui.screens.ProfileScreen
import com.aquila.pocxpertalerts.ui.screens.SearchAlertsScreen
import com.aquila.pocxpertalerts.ui.screens.SearchArchivedAlertsScreen
import com.aquila.pocxpertalerts.ui.screens.SearchForwardAlertsScreen
import com.aquila.pocxpertalerts.ui.screens.SettingsScreen
import com.aquila.pocxpertalerts.ui.screens.SubscriptionDetailsScreen
import com.aquila.pocxpertalerts.ui.screens.SubscriptionsScreen
import com.aquila.pocxpertalerts.ui.screens.SearchSubscriptionScreen

// ============================================================
// SESSION TIMEOUT
// ============================================================

// Temporary session timeout: 15 minutes
private const val SESSION_TIMEOUT_MILLIS = 15 * 6 * 1000L


// ============================================================
// SCREEN ROUTES
// ============================================================

sealed class Screen(val route: String) {

    data object Login : Screen("login")

    data object Home : Screen("home")

    data object Settings : Screen("settings")

    data object About : Screen("about")

    data object Archived : Screen("archived")

    data object Profile : Screen("profile")

    data object ChangePassword : Screen("change_password")

    data object AlertDetail : Screen("alert_detail/{alertId}") {

        fun createRoute(alertId: String): String {
            return "alert_detail/$alertId"
        }
    }

    data object SearchAlerts : Screen("search_alerts")

    data object SearchArchivedAlerts : Screen("search_archived_alerts")

    data object Subscriptions : Screen("subscriptions")

    data object SubscriptionDetails : Screen("subscription_details/{subscriptionId}") {

        fun createRoute(subscriptionId: String): String {
            return "subscription_details/$subscriptionId"
        }
    }

    data object SearchSubscription : Screen("search_subscription")

    data object ForwardAlerts : Screen("forward_alerts")

    data object ForwardAlertDetails : Screen("forward_alert_details")

    data object SearchForwardAlerts : Screen("search_forward_alerts")


}


// ============================================================
// APP NAVIGATION
// ============================================================

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current

    // Current navigation route
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route

    // Stores the last time the user interacted with the app
    var lastActivityTime by remember {
        mutableLongStateOf(SystemClock.elapsedRealtime())
    }


    // ========================================================
    // SESSION TIMEOUT MONITOR
    // ========================================================

    LaunchedEffect(
        currentRoute,
        lastActivityTime
    ) {

        // Do not run timeout while user is on Login
        if (currentRoute == Screen.Login.route) {
            return@LaunchedEffect
        }

        // Wait until the session timeout period
        delay(SESSION_TIMEOUT_MILLIS)

        // Check actual inactivity duration
        val inactiveTime =
            SystemClock.elapsedRealtime() - lastActivityTime

        if (inactiveTime >= SESSION_TIMEOUT_MILLIS) {

            // Cancel all Xpert Alerts notifications
            NotificationManagerCompat
                .from(context)
                .cancelAll()

            // Return to Login
            navController.navigate(Screen.Login.route) {

                popUpTo(0) {
                    inclusive = true
                }

                launchSingleTop = true
            }
        }
    }


    // ========================================================
    // NAVIGATION CONTENT
    // ========================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {

                awaitPointerEventScope {

                    while (true) {

                        val event = awaitPointerEvent(
                            pass = PointerEventPass.Initial
                        )

                        // Reset session timeout when user touches
                        // the screen
                        if (
                            event.changes.any {
                                it.changedToDown()
                            }
                        ) {
                            lastActivityTime =
                                SystemClock.elapsedRealtime()
                        }
                    }
                }
            }
    ) {

        NavHost(
            navController = navController,
            startDestination = Screen.Login.route
        ) {


            // ====================================================
            // LOGIN
            // ====================================================

            composable(Screen.Login.route) {

                LoginScreen(

                    // TEMPORARY:
                    // Authentication is bypassed for now.
                    // Login directly opens Home.

                    onLoginClick = { _, _ ->

                        // Start a fresh session
                        lastActivityTime =
                            SystemClock.elapsedRealtime()

                        navController.navigate(
                            Screen.Home.route
                        ) {

                            popUpTo(
                                Screen.Login.route
                            ) {
                                inclusive = true
                            }
                        }
                    },

                    // Open Settings from Login
                    onSettingsClick = {

                        navController.navigate(
                            Screen.Settings.route
                        )
                    }
                )
            }


            // ====================================================
            // HOME
            // ====================================================

            composable(Screen.Home.route) {

                HomeScreen(

                    onAlertClick = { alertId ->

                        navController.navigate(
                            Screen.AlertDetail.createRoute(
                                alertId
                            )
                        )
                    },

                    onSearchClick = {

                        navController.navigate(
                            Screen.SearchAlerts.route
                        )
                    },

                    onSubscriptionsClick = {

                        navController.navigate(
                            Screen.Subscriptions.route
                        )
                    },

                    onArchivedClick = {

                        navController.navigate(
                            Screen.Archived.route
                        )
                    },

                    onForwardAlertsClick = {

                        navController.navigate(
                            Screen.ForwardAlerts.route
                        )
                    },

                    onProfileClick = {

                        navController.navigate(
                            Screen.Profile.route
                        )
                    },

                    onPasswordClick = {

                        navController.navigate(
                            Screen.ChangePassword.route
                        )
                    },

                    onAboutClick = {

                        navController.navigate(
                            Screen.About.route
                        )
                    },

                    onLogoutClick = {

                        // Cancel all Xpert Alerts notifications
                        NotificationManagerCompat
                            .from(context)
                            .cancelAll()

                        // Return to Login
                        navController.navigate(
                            Screen.Login.route
                        ) {

                            popUpTo(0) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                )
            }


            // ====================================================
            // ALERT DETAIL
            // ====================================================

            composable(
                route = Screen.AlertDetail.route
            ) { backStackEntry ->

                val alertId =
                    backStackEntry.arguments?.getString(
                        "alertId"
                    ) ?: return@composable

                AlertDetailScreen(

                    alertId = alertId,

                    onBackClick = {

                        navController.popBackStack()
                    }
                )
            }


            // ====================================================
            // ARCHIVED ALERTS
            // ====================================================

            composable(Screen.Archived.route) {

                ArchivedScreen(

                    onBackClick = {

                        navController.popBackStack()
                    },

                    onSearchClick = {

                        navController.navigate(
                            Screen.SearchArchivedAlerts.route
                        )
                    },

                    onAlertClick = { alertId ->

                        navController.navigate(
                            Screen.AlertDetail.createRoute(
                                alertId
                            )
                        )
                    }
                )
            }


            // ====================================================
            // PROFILE
            // ====================================================

            composable(Screen.Profile.route) {

                ProfileScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // ====================================================
            // CHANGE PASSWORD
            // ====================================================

            composable(Screen.ChangePassword.route) {

                ChangePasswordScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }


            // ====================================================
            // SETTINGS
            // ====================================================

            composable(Screen.Settings.route) {

                SettingsScreen(

                    onBackClick = {

                        navController.popBackStack()
                    }
                )
            }


            // ====================================================
            // ABOUT
            // ====================================================

            composable(Screen.About.route) {

                AboutScreen(

                    onBackClick = {

                        navController.popBackStack()
                    }
                )
            }


            // ====================================================
            // SEARCH ALERTS
            // ====================================================

            composable(Screen.SearchAlerts.route) {

                SearchAlertsScreen(

                    onBackClick = {

                        navController.popBackStack()
                    },

                    onSearchClick = {

                        navController.popBackStack()
                    }
                )
            }


            // ====================================================
            // SEARCH ARCHIVED ALERTS
            // ====================================================

            composable(Screen.SearchArchivedAlerts.route) {

                SearchArchivedAlertsScreen(

                    onBackClick = {

                        navController.popBackStack()
                    },

                    onSearchClick = {

                        navController.popBackStack()
                    }
                )
            }


            // ====================================================
            // SUBSCRIPTIONS
            // ====================================================

            composable(Screen.Subscriptions.route) {

                SubscriptionsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },

                    onCreateClick = {
                        navController.navigate(
                            Screen.SubscriptionDetails.route
                        )
                    },

                    onEditClick = { subscriptionId ->

                        // Keep current UI behavior for now.
                        // Edit will be connected to API data later.
                    },

                    onSearchClick = {
                        navController.navigate(
                            Screen.SearchSubscription.route
                        )
                    }
                )
            }


            // ====================================================
            // SUBSCRIPTION DETAILS
            // ====================================================

            composable(
                route = Screen.SubscriptionDetails.route
            ) { backStackEntry ->

                val subscriptionId =
                    backStackEntry.arguments?.getString("subscriptionId")

                SubscriptionDetailsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveClick = {
                        navController.popBackStack()
                    }
                )
            }

            // ====================================================
            // SEARCH SUBSCRIPTION
            // ====================================================

            composable(Screen.SearchSubscription.route) {
                SearchSubscriptionScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSearchClick = {
                        navController.popBackStack()
                    }
                )
            }


            // ====================================================
            // FORWARD ALERTS
            // ====================================================

            composable(Screen.ForwardAlerts.route) {

                ForwardAlertsScreen(

                    onBackClick = {

                        navController.popBackStack()
                    },

                    onCreateClick = {

                        navController.navigate(
                            Screen.ForwardAlertDetails.route
                        )
                    },

                    onEditClick = { forwardAlertId ->

                        navController.navigate(
                            Screen.ForwardAlertDetails.route
                        )
                    },

                    onSearchClick = {

                        navController.navigate(
                            Screen.SearchForwardAlerts.route
                        )
                    }
                )
            }


            // ====================================================
            // FORWARD ALERT DETAILS
            // ====================================================

            composable(
                Screen.ForwardAlertDetails.route
            ) {

                ForwardAlertDetailsScreen(

                    onBackClick = {

                        navController.popBackStack()
                    },

                    onSaveClick = {

                        navController.popBackStack()
                    }
                )
            }


            // ====================================================
            // SEARCH FORWARD ALERTS
            // ====================================================

            composable(
                Screen.SearchForwardAlerts.route
            ) {

                SearchForwardAlertsScreen(

                    onBackClick = {

                        navController.popBackStack()
                    },

                    onSearchClick = {

                        navController.popBackStack()
                    }
                )
            }
        }
    }
}