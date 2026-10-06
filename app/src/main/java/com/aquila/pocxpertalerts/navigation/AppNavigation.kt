package com.aquila.pocxpertalerts.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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


// ============================================================
// SCREEN ROUTES
// ============================================================

sealed class Screen(
    val route: String
) {

    data object Login :
        Screen("login")


    data object Home :
        Screen("home")


    data object Settings :
        Screen("settings")


    data object Notifications :
        Screen("notifications")


    data object About :
        Screen("about")


    data object Archived :
        Screen("archived")


    data object Profile :
        Screen("profile")


    data object ChangePassword :
        Screen("change_password")


    data object AlertDetail :
        Screen("alert_detail/{alertId}") {

        fun createRoute(
            alertId: String
        ): String {

            return "alert_detail/$alertId"
        }
    }


    data object SearchAlerts :
        Screen("search_alerts")


    data object SearchArchivedAlerts :
        Screen("search_archived_alerts")


    data object Subscriptions :
        Screen("subscriptions")


    data object SubscriptionDetails :
        Screen("subscription_details/{subscriptionId}") {

        fun createRoute(
            subscriptionId: String
        ): String {

            return "subscription_details/$subscriptionId"
        }
    }


    data object SearchSubscription :
        Screen("search_subscription")


    data object ForwardAlerts :
        Screen("forward_alerts")


    data object ForwardAlertDetails :
        Screen("forward_alert_details")


    data object SearchForwardAlerts :
        Screen("search_forward_alerts")
}


// ============================================================
// APP NAVIGATION
// ============================================================

@Composable
fun AppNavigation() {

    // ========================================================
    // NAVIGATION CONTROLLER
    // ========================================================

    val navController = rememberNavController()


    // ========================================================
    // NAVIGATION HOST
    // ========================================================

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

            composable(
                route = Screen.Login.route
            ) {

                LoginScreen(

                    onLoginSuccess = {

                        navController.navigate(
                            Screen.Home.route
                        ) {

                            // Remove Login from back stack
                            popUpTo(
                                Screen.Login.route
                            ) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    },

                    onSettingsClick = {

                        navController.navigate(
                            Screen.Settings.route
                        )
                    }
                )
            }


            // ==================================================
            // HOME
            // ==================================================

            composable(
                route = Screen.Home.route
            ) {

                HomeScreen(

                    // ------------------------------------------
                    // ALERT DETAIL
                    // ------------------------------------------

                    onAlertClick = { alertId ->

                        navController.navigate(
                            Screen.AlertDetail.createRoute(
                                alertId
                            )
                        )
                    },


                    // ------------------------------------------
                    // SEARCH ALERTS
                    // ------------------------------------------

                    onSearchClick = {

                        navController.navigate(
                            Screen.SearchAlerts.route
                        )
                    },


                    // ------------------------------------------
                    // SUBSCRIPTIONS
                    // ------------------------------------------

                    onSubscriptionsClick = {

                        navController.navigate(
                            Screen.Subscriptions.route
                        )
                    },


                    // ------------------------------------------
                    // NOTIFICATIONS
                    // ------------------------------------------

                    onNotificationsClick = {

                        navController.navigate(
                            Screen.Notifications.route
                        )
                    },


                    // ------------------------------------------
                    // ARCHIVED
                    // ------------------------------------------

                    onArchivedClick = {

                        navController.navigate(
                            Screen.Archived.route
                        )
                    },


                    // ------------------------------------------
                    // FORWARD ALERTS
                    // ------------------------------------------

                    onForwardAlertsClick = {

                        navController.navigate(
                            Screen.ForwardAlerts.route
                        )
                    },


                    // ------------------------------------------
                    // PROFILE
                    // ------------------------------------------

                    onProfileClick = {

                        navController.navigate(
                            Screen.Profile.route
                        )
                    },


                    // ------------------------------------------
                    // CHANGE PASSWORD
                    // ------------------------------------------

                    onPasswordClick = {

                        navController.navigate(
                            Screen.ChangePassword.route
                        )
                    },


                    // ------------------------------------------
                    // ABOUT
                    // ------------------------------------------

                    onAboutClick = {

                        navController.navigate(
                            Screen.About.route
                        )
                    },


                    // ------------------------------------------
                    // LOGOUT
                    // ------------------------------------------

                    onLogoutClick = {

                        navController.navigate(
                            Screen.Login.route
                        ) {

                            // Remove all previous screens
                            popUpTo(0) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                )
            }


            // ==================================================
            // ALERT DETAIL
            // ==================================================

            composable(
                route = Screen.AlertDetail.route
            ) { backStackEntry ->

                val alertId =
                    backStackEntry.arguments
                        ?.getString("alertId")
                        ?: ""

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

            composable(
                route = Screen.Notifications.route
            ) {

                NotificationsScreen(

                    onBackClick = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // ARCHIVED ALERTS
            // ==================================================

            composable(
                route = Screen.Archived.route
            ) {

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


            // ==================================================
            // PROFILE
            // ==================================================

            composable(
                route = Screen.Profile.route
            ) {

                ProfileScreen(

                    onBackClick = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // CHANGE PASSWORD
            // ==================================================

            composable(
                route = Screen.ChangePassword.route
            ) {

                ChangePasswordScreen(

                    onBackClick = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // SETTINGS
            // ==================================================

            composable(
                route = Screen.Settings.route
            ) {

                SettingsScreen(

                    onBackClick = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // ABOUT
            // ==================================================

            composable(
                route = Screen.About.route
            ) {

                AboutScreen(

                    onBackClick = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // SEARCH ALERTS
            // ==================================================

            composable(
                route = Screen.SearchAlerts.route
            ) {

                SearchAlertsScreen(

                    onBackClick = {

                        navController.popBackStack()
                    },

                    onSearchClick = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // SEARCH ARCHIVED ALERTS
            // ==================================================

            composable(
                route = Screen.SearchArchivedAlerts.route
            ) {

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

            composable(
                route = Screen.Subscriptions.route
            ) {

                SubscriptionsScreen(

                    onBackClick = {

                        navController.popBackStack()
                    },

                    onCreateClick = {

                        navController.navigate(
                            Screen.SubscriptionDetails
                                .createRoute("new")
                        )
                    },

                    onEditClick = { subscriptionId ->

                        navController.navigate(
                            Screen.SubscriptionDetails
                                .createRoute(subscriptionId)
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

            composable(
                route = Screen.SubscriptionDetails.route
            ) {

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
            // SEARCH SUBSCRIPTION
            // ==================================================

            composable(
                route = Screen.SearchSubscription.route
            ) {

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

            composable(
                route = Screen.ForwardAlerts.route
            ) {

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

            composable(
                route = Screen.ForwardAlertDetails.route
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


            // ==================================================
            // SEARCH FORWARD ALERTS
            // ==================================================

            composable(
                route = Screen.SearchForwardAlerts.route
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