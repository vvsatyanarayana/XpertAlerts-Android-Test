package com.aquila.pocxpertalerts.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aquila.pocxpertalerts.ui.screens.AlertDetailScreen
import com.aquila.pocxpertalerts.ui.screens.ArchivedScreen
import com.aquila.pocxpertalerts.ui.screens.ChangePasswordScreen
import com.aquila.pocxpertalerts.ui.screens.HomeScreen
import com.aquila.pocxpertalerts.ui.screens.LoginScreen
import com.aquila.pocxpertalerts.ui.screens.ProfileScreen
import com.aquila.pocxpertalerts.ui.screens.SettingsScreen
import com.aquila.pocxpertalerts.ui.screens.SearchAlertsScreen
import com.aquila.pocxpertalerts.ui.screens.SearchArchivedAlertsScreen
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

                // --------------------------------------------
                // ALERT CLICK
                // --------------------------------------------

                onAlertClick = { alertId ->

                    navController.navigate(
                        Screen.AlertDetail.createRoute(
                            alertId
                        )
                    )
                },


                // --------------------------------------------
                // SEARCH ALERTS
                // --------------------------------------------

                onSearchClick = {

                    navController.navigate(
                        Screen.SearchAlerts.route
                    )
                },


                // --------------------------------------------
                // ARCHIVED
                // --------------------------------------------

                onArchivedClick = {

                    navController.navigate(
                        Screen.Archived.route
                    )
                },


                // --------------------------------------------
                // PROFILE
                // --------------------------------------------

                onProfileClick = {

                    navController.navigate(
                        Screen.Profile.route
                    )
                },


                // --------------------------------------------
                // CHANGE PASSWORD
                // --------------------------------------------

                onPasswordClick = {

                    navController.navigate(
                        Screen.ChangePassword.route
                    )
                },


                // --------------------------------------------
                // LOGOUT
                // --------------------------------------------

                onLogoutClick = {

                    navController.navigate(
                        Screen.Login.route
                    ) {

                        popUpTo(
                            Screen.Home.route
                        ) {
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
    }
}