
package com.aquila.pocxpertalerts.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.data.model.AlertDto
import com.aquila.pocxpertalerts.domain.model.User
import com.aquila.pocxpertalerts.ui.screens.AboutScreen
import com.aquila.pocxpertalerts.ui.screens.AlertDetailScreen
import com.aquila.pocxpertalerts.ui.screens.ArchivedScreen
import com.aquila.pocxpertalerts.ui.screens.ChangePasswordScreen
import com.aquila.pocxpertalerts.ui.screens.ForwardAlertDetailsScreen
import com.aquila.pocxpertalerts.ui.screens.ForwardAlertsScreen
import com.aquila.pocxpertalerts.ui.screens.HomeScreen
import com.aquila.pocxpertalerts.ui.screens.LoginScreen
import com.aquila.pocxpertalerts.ui.screens.NotificationsScreen
import com.aquila.pocxpertalerts.ui.screens.ProfileScreen
import com.aquila.pocxpertalerts.ui.screens.SearchAlertsScreen
import com.aquila.pocxpertalerts.ui.screens.SearchArchivedAlertsScreen
import com.aquila.pocxpertalerts.ui.screens.SearchForwardAlertsScreen
import com.aquila.pocxpertalerts.ui.screens.SearchSubscriptionScreen
import com.aquila.pocxpertalerts.ui.screens.SettingsScreen
import com.aquila.pocxpertalerts.ui.screens.SubscriptionDetailsScreen
import com.aquila.pocxpertalerts.ui.screens.SubscriptionsScreen
import com.aquila.pocxpertalerts.viewmodel.AlertsViewModel
import com.aquila.pocxpertalerts.viewmodel.AlertsViewModelFactory

// ============================================================
// SCREEN ROUTES
// ============================================================

sealed class Screen(val route: String) {

    data object Login : Screen("login")
    data object Home : Screen("home")
    data object Settings : Screen("settings")
    data object Notifications : Screen("notifications")
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

    data object SubscriptionDetails :
        Screen("subscription_details/{subscriptionId}") {
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

    var loggedInUser by remember {
        mutableStateOf<User?>(null)
    }

    // Stores the actual alerts returned by the API.
    var searchedAlerts by remember {
        mutableStateOf<List<AlertDto>>(emptyList())
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route
        ) {

            // ==================================================
            // LOGIN
            // ==================================================

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { user ->
                        loggedInUser = user

                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            // ==================================================
            // HOME
            // ==================================================

            composable(Screen.Home.route) {

                Log.d(
                    "AlertsNavigation",
                    "Opening Home with ${searchedAlerts.size} alerts"
                )

                HomeScreen(
                    // IMPORTANT: pass search results to HomeScreen.
                    alerts = searchedAlerts,

                    onAlertClick = { alertId ->
                        Log.d(
                            "AlertsNavigation",
                            "Selected alert ID: $alertId"
                        )

                        navController.navigate(
                            Screen.AlertDetail.createRoute(alertId)
                        )
                    },

                    onSearchClick = {
                        navController.navigate(Screen.SearchAlerts.route)
                    },

                    onSubscriptionsClick = {
                        navController.navigate(Screen.Subscriptions.route)
                    },

                    onNotificationsClick = {
                        navController.navigate(Screen.Notifications.route)
                    },

                    onArchivedClick = {
                        navController.navigate(Screen.Archived.route)
                    },

                    onForwardAlertsClick = {
                        navController.navigate(Screen.ForwardAlerts.route)
                    },

                    onProfileClick = {
                        navController.navigate(Screen.Profile.route)
                    },

                    onPasswordClick = {
                        navController.navigate(Screen.ChangePassword.route)
                    },

                    onAboutClick = {
                        navController.navigate(Screen.About.route)
                    },

                    onLogoutClick = {
                        loggedInUser = null
                        searchedAlerts = emptyList()

                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // ==================================================
            // ALERT DETAILS
            // ==================================================

            composable(Screen.AlertDetail.route) { backStackEntry ->

                val alertId = backStackEntry.arguments
                    ?.getString("alertId")
                    .orEmpty()

                AlertDetailScreen(
                    alertId = alertId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // ==================================================
            // NOTIFICATIONS
            // ==================================================

            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // ==================================================
            // ARCHIVED ALERTS
            // ==================================================

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
                            Screen.AlertDetail.createRoute(alertId)
                        )
                    }
                )
            }

            // ==================================================
            // PROFILE
            // ==================================================

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // ==================================================
            // CHANGE PASSWORD
            // ==================================================

            composable(Screen.ChangePassword.route) {
                ChangePasswordScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // ==================================================
            // SETTINGS
            // ==================================================

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // ==================================================
            // ABOUT
            // ==================================================

            composable(Screen.About.route) {
                AboutScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // ==================================================
            // SEARCH ALERTS
            // ==================================================

            composable(Screen.SearchAlerts.route) {

                val user = loggedInUser
                val context = LocalContext.current.applicationContext

                val settingsDataStore = remember(context) {
                    SettingsDataStore(context)
                }

                val alertsFactory = remember(settingsDataStore) {
                    AlertsViewModelFactory(settingsDataStore)
                }

                val alertsViewModel: AlertsViewModel = viewModel(
                    factory = alertsFactory
                )

                SearchAlertsScreen(
                    alertsViewModel = alertsViewModel,

                    onBackClick = {
                        navController.popBackStack()
                    },

                    onSearchClick = {
                            startDate,
                            endDate,
                            keyword,
                            viewBy,
                            application,
                            alertDefinition ->

                        if (user == null) {
                            Log.e(
                                "AlertsAPI",
                                "No logged-in user is available"
                            )
                        } else {
                            Log.d(
                                "AlertsAPI",
                                "Starting alert search for user ${user.id}"
                            )

                            alertsViewModel.searchAlerts(
                                userId = user.id.toString(),
                                startDate = startDate,
                                endDate = endDate,
                                keyword = keyword,
                                viewByFlag = viewBy,
                                applicationId = application,
                                alertDefinitionId = alertDefinition,
                                sessionId = user.sessionId.orEmpty(),

                                onSuccess = { results ->
                                    // Save the actual API results.
                                    Log.d(
                                        "AlertsNavigation",
                                        "Saving ${results.size} alerts"
                                    )
                                    searchedAlerts = results

                                    Log.d(
                                        "AlertsAPI",
                                        "Search succeeded. " +
                                                "Received ${results.size} alerts"
                                    )

                                    results.forEach { alert ->
                                        Log.d(
                                            "AlertsAPI",
                                            "Alert ID=${alert.id}, " +
                                                    "subject=${alert.subject}"
                                        )
                                    }

                                    // Return to the existing Home screen.
                                    val returnedToHome =
                                        navController.popBackStack(
                                            Screen.Home.route,
                                            inclusive = false
                                        )

                                    Log.d(
                                        "AlertsNavigation",
                                        "Returned to Home: $returnedToHome"
                                    )
                                }
                            )
                        }
                    }
                )
            }

            // ==================================================
            // SEARCH ARCHIVED ALERTS
            // ==================================================

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

            // ==================================================
            // SUBSCRIPTIONS
            // ==================================================

            composable(Screen.Subscriptions.route) {
                SubscriptionsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onCreateClick = {
                        navController.navigate(
                            Screen.SubscriptionDetails.createRoute("new")
                        )
                    },
                    onEditClick = { subscriptionId ->
                        navController.navigate(
                            Screen.SubscriptionDetails.createRoute(
                                subscriptionId
                            )
                        )
                    },
                    onSearchClick = {
                        navController.navigate(
                            Screen.SearchSubscription.route
                        )
                    }
                )
            }

            // ==================================================
            // SUBSCRIPTION DETAILS
            // ==================================================

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

            // ==================================================
            // SEARCH SUBSCRIPTIONS
            // ==================================================

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

            // ==================================================
            // FORWARD ALERTS
            // ==================================================

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
                    onEditClick = {
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

            // ==================================================
            // FORWARD ALERT DETAILS
            // ==================================================

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

            // ==================================================
            // SEARCH FORWARD ALERTS
            // ==================================================

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
}
