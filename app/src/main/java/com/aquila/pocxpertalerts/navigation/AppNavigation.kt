package com.aquila.pocxpertalerts.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aquila.pocxpertalerts.ui.screens.AlertDetailScreen
import com.aquila.pocxpertalerts.ui.screens.ArchivedScreen
import com.aquila.pocxpertalerts.ui.screens.ChangePasswordScreen
import com.aquila.pocxpertalerts.ui.screens.ForwardAlertDetailsScreen
import com.aquila.pocxpertalerts.ui.screens.HomeScreen
import com.aquila.pocxpertalerts.ui.screens.LoginScreen
import com.aquila.pocxpertalerts.ui.screens.ProfileScreen
import com.aquila.pocxpertalerts.ui.screens.SettingsScreen
import com.aquila.pocxpertalerts.ui.screens.SearchAlertsScreen
import com.aquila.pocxpertalerts.ui.screens.SearchArchivedAlertsScreen
import com.aquila.pocxpertalerts.ui.screens.SubscriptionsScreen
import com.aquila.pocxpertalerts.ui.screens.SubscriptionDetailsScreen
import com.aquila.pocxpertalerts.ui.screens.ForwardAlertsScreen
import com.aquila.pocxpertalerts.ui.screens.SearchForwardAlertsScreen

// ============================================================
// SCREEN ROUTES
// ============================================================

sealed class Screen(val route: String) {

    data object Login : Screen("login")

    data object Home : Screen("home")

    data object Settings : Screen("settings")

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

    data object SubscriptionDetails : Screen("subscription_details")

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

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

// LOGIN
// ====================================================

        composable(Screen.Login.route) {

            LoginScreen(

                // TEMPORARY:
                // Authentication is bypassed for now.
                // Login directly opens Home.

                onLoginClick = { _, _ ->

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
                        Screen.AlertDetail.createRoute(alertId)
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

                onLogoutClick = {
                    navController.navigate(
                        Screen.Login.route
                    ) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
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
                    navController.navigate(Screen.SearchArchivedAlerts.route)
                },
                onAlertClick = { alertId ->
                    navController.navigate(
                        Screen.AlertDetail.createRoute(alertId)
                    )
                }
            )
        }


        // ====================================================
        // PROFILE
        // ====================================================

        composable(
            route = Screen.Profile.route
        ) {

            ProfileScreen()
        }


        // ====================================================
        // CHANGE PASSWORD
        // ====================================================

        composable(
            route = Screen.ChangePassword.route
        ) {

            ChangePasswordScreen()
        }


        // ====================================================
        // SETTINGS
        // ====================================================

        composable(
            route = Screen.Settings.route
        ) {

            SettingsScreen(

                onBackClick = {

                    navController.popBackStack()
                }
            )
        }

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

        composable(Screen.Subscriptions.route) {
            SubscriptionsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCreateClick = {
                    navController.navigate(Screen.SubscriptionDetails.route)
                },
                onEditClick = { subscriptionId ->
                    // Edit Subscription screen will be connected next
                }
            )
        }


        composable(Screen.SubscriptionDetails.route) {
            SubscriptionDetailsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.ForwardAlerts.route) {
            ForwardAlertsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCreateClick = {
                    navController.navigate(Screen.ForwardAlertDetails.route)
                },
                onEditClick = { forwardAlertId ->
                    navController.navigate(Screen.ForwardAlertDetails.route)
                },
                onSearchClick = {
                    navController.navigate(Screen.SearchForwardAlerts.route)
                }
            )
        }

        composable(Screen.ForwardAlertDetails.route) {

            ForwardAlertDetailsScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onSaveClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.SearchForwardAlerts.route) {
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