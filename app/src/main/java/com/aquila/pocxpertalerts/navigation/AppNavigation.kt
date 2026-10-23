package com.aquila.pocxpertalerts.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.aquila.pocxpertalerts.data.local.SearchAlertsPersistence
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.data.model.AlertDto
import com.aquila.pocxpertalerts.data.remote.RetrofitClient
import com.aquila.pocxpertalerts.data.repository.ArchivedAlertsCache
import com.aquila.pocxpertalerts.data.repository.ArchivedAlertsRepository
import com.aquila.pocxpertalerts.data.repository.ArchivedDynamicAction
import com.aquila.pocxpertalerts.data.repository.ArchivedSearchPreferences
import com.aquila.pocxpertalerts.data.repository.ChangePasswordRepository
import com.aquila.pocxpertalerts.data.repository.SubscriptionRepositoryImpl
import com.aquila.pocxpertalerts.domain.model.User

import com.aquila.pocxpertalerts.ui.screens.AboutScreen
import com.aquila.pocxpertalerts.ui.screens.AlertDetailScreen
import com.aquila.pocxpertalerts.ui.screens.ApplicationOption
import com.aquila.pocxpertalerts.ui.screens.ArchivedAlertDetailsScreen
import com.aquila.pocxpertalerts.ui.screens.ArchivedAlertUi
import com.aquila.pocxpertalerts.ui.screens.ArchivedAlertsScreen
import com.aquila.pocxpertalerts.ui.screens.ChangePasswordScreen
import com.aquila.pocxpertalerts.ui.screens.ForwardAlertDetailsScreen
import com.aquila.pocxpertalerts.ui.screens.ForwardAlertEditDetails
import com.aquila.pocxpertalerts.ui.screens.ForwardAlertItem
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
import com.aquila.pocxpertalerts.viewmodel.ArchivedAlertsViewModel
import com.aquila.pocxpertalerts.viewmodel.ArchivedAlertsViewModelFactory
import com.aquila.pocxpertalerts.viewmodel.ChangePasswordViewModel
import com.aquila.pocxpertalerts.viewmodel.ChangePasswordViewModelFactory
import com.aquila.pocxpertalerts.viewmodel.ForwardAlertsViewModel
import com.aquila.pocxpertalerts.viewmodel.SubscriptionsViewModel
import com.aquila.pocxpertalerts.viewmodel.SubscriptionsViewModelFactory

// ============================================================
// SCREEN ROUTES
// ============================================================

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Settings : Screen("settings")
    data object Home : Screen("home")
    data object Subscriptions : Screen("subscriptions")
    data object ForwardAlerts : Screen("forward_alerts")
    data object Archived : Screen("archived")
    data object ChangePassword : Screen("change_password")
    data object About : Screen("about")

    data object Notifications : Screen("notifications")
    data object Profile : Screen("profile")
    data object ArchivedAlertDetails : Screen("archived_alert_details")

    data object AlertDetail : Screen("alert_detail/{alertId}") {
        fun createRoute(alertId: String) = "alert_detail/$alertId"
    }

    data object SearchAlerts : Screen("search_alerts")
    data object SearchArchivedAlerts : Screen("search_archived_alerts")

    data object SubscriptionDetails :
        Screen("subscription_details/{subscriptionId}") {
        fun createRoute(subscriptionId: String) =
            "subscription_details/$subscriptionId"
    }

    data object SearchSubscription : Screen("search_subscription")
    data object ForwardAlertDetails : Screen("forward_alert_details")
    data object SearchForwardAlerts : Screen("search_forward_alerts")
}

// ============================================================
// FORWARD ALERT SEARCH FILTERS
// ============================================================

data class ForwardSearchCriteria(
    val applicationId: String = "0",
    val userName: String = "",
    val email: String = "",
    val active: Int = 1
)

// ============================================================
// APP NAVIGATION
// ============================================================

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val appContext = LocalContext.current.applicationContext

    // --------------------------------------------------------
    // ARCHIVED ALERTS: SHARED REPOSITORY, CACHE AND VIEWMODEL
    // --------------------------------------------------------

    val archivedSearchPreferences = remember(appContext) {
        ArchivedSearchPreferences(appContext)
    }

    val archivedCache = remember(appContext) {
        ArchivedAlertsCache(appContext)
    }

    val archivedRepository = remember {
        ArchivedAlertsRepository(RetrofitClient.apiService)
    }

    val archivedFactory = remember(archivedRepository, archivedCache) {
        ArchivedAlertsViewModelFactory(
            repository = archivedRepository,
            cache = archivedCache
        )
    }

    val archivedViewModel: ArchivedAlertsViewModel =
        viewModel(factory = archivedFactory)

    val archivedAlerts by archivedViewModel.alerts.collectAsState()
    val archivedLoading by archivedViewModel.isLoading.collectAsState()
    val archivedError by archivedViewModel.errorMessage.collectAsState()

    // --------------------------------------------------------
    // SEARCHED ALERTS PERSISTENCE
    // --------------------------------------------------------

    val searchPersistence = remember(appContext) {
        SearchAlertsPersistence(appContext)
    }

    // --------------------------------------------------------
    // SHARED APPLICATION STATE
    // --------------------------------------------------------

    var loggedInUser by remember {
        mutableStateOf<User?>(null)
    }

    val forwardViewModel: ForwardAlertsViewModel = viewModel()
    val forwardListState by forwardViewModel.listState.collectAsState()
    val forwardFormState by forwardViewModel.formState.collectAsState()
    val forwardCreateState by forwardViewModel.createState.collectAsState()

    var searchedAlerts by remember {
        mutableStateOf<List<AlertDto>>(emptyList())
    }

    var selectedAlert by remember {
        mutableStateOf<AlertDto?>(null)
    }

    var selectedForwardAlert by remember {
        mutableStateOf<ForwardAlertItem?>(null)
    }

    var forwardSearchCriteria by remember {
        mutableStateOf(ForwardSearchCriteria())
    }

    var selectedArchivedAlert: ArchivedAlertUi? by remember {
        mutableStateOf(null)
    }

    val scope = rememberCoroutineScope()

    var pendingDeleteAction by remember {
        mutableStateOf<ArchivedDynamicAction?>(null)
    }

    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    var deleteMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isDeleting by remember {
        mutableStateOf(false)
    }

    // ========================================================
    // NAVIGATION HOST
    // Screen destinations are grouped in a readable order.
    // ========================================================

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route
        ) {

            // =================================================
            // 1. LOGIN
            // =================================================

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { user ->
                        loggedInUser = user

                        // Restore the current user's previous search.
                        searchedAlerts = searchPersistence.loadAlerts(
                            user.id.toString()
                        )

                        selectedAlert = null

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

            // =================================================
            // 2. SETTINGS
            // =================================================

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // =================================================
            // 3. HOME
            // =================================================

            composable(Screen.Home.route) {

                // Create the settings DataStore.
                val context = LocalContext.current.applicationContext

                val settingsDataStore = remember(context) {
                    SettingsDataStore(context)
                }

                // Create the Alerts ViewModel factory.
                val alertsFactory = remember(settingsDataStore) {
                    AlertsViewModelFactory(settingsDataStore)
                }

                // Use the Home navigation entry as the ViewModel owner.
                val homeBackStackEntry = remember(navController) {
                    navController.getBackStackEntry(Screen.Home.route)
                }

                // Observe the same ViewModel used by Search Alerts.
                val alertsViewModel: AlertsViewModel = viewModel(
                    viewModelStoreOwner = homeBackStackEntry,
                    factory = alertsFactory
                )

                // Observe whether the API request is running.
                val isAlertsLoading by alertsViewModel.isLoading.collectAsState()

                // Your existing HomeScreen starts here.
                HomeScreen(
                    alerts = searchedAlerts,
                    isLoading = isAlertsLoading,

                    // Keep all your existing callbacks below.
                    onAlertClick = { alertId ->
                        val alert = searchedAlerts.firstOrNull {
                            it.alertId.toString() == alertId ||
                                    it.id.toString() == alertId
                        }

                        if (alert != null) {
                            selectedAlert = alert

                            navController.navigate(
                                Screen.AlertDetail.createRoute(alertId)
                            )
                        } else {
                            Log.e(
                                "AlertsNavigation",
                                "Alert not found: $alertId"
                            )
                        }
                    },

                    onSearchClick = {
                        navController.navigate(Screen.SearchAlerts.route) {
                            launchSingleTop = true
                        }
                    },

                    onSubscriptionsClick = {
                        navController.navigate(Screen.Subscriptions.route) {
                            launchSingleTop = true
                        }
                    },

                    onNotificationsClick = {
                        navController.navigate(Screen.Notifications.route) {
                            launchSingleTop = true
                        }
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
                        selectedAlert = null

                        // Keep locally saved search results intact.
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // =================================================
            // 4. ALERT DETAILS
            // =================================================

            composable(
                route = Screen.AlertDetail.route,
                arguments = listOf(
                    navArgument("alertId") {
                        type = NavType.StringType
                    }
                )
            ) { entry ->

                val routeAlertId = entry.arguments
                    ?.getString("alertId")
                    .orEmpty()

                val alert = selectedAlert?.takeIf {
                    it.alertId.toString() == routeAlertId ||
                            it.id.toString() == routeAlertId
                } ?: searchedAlerts.firstOrNull {
                    it.alertId.toString() == routeAlertId ||
                            it.id.toString() == routeAlertId
                }

                if (alert != null) {
                    AlertDetailScreen(
                        alert = alert,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("Alert details are unavailable.")
                    }
                }
            }

            // =================================================
            // 5. NOTIFICATIONS
            // =================================================

            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // =================================================
            // 6. ARCHIVED ALERTS
            // =================================================

            composable(Screen.Archived.route) {

                val user = loggedInUser

                val archivedUiItems = archivedAlerts.map { item ->
                    val displayMessage = item.message
                        .replace("<br/><br/>", "\\n")
                        .replace("<br/>", "\\n")

                    ArchivedAlertUi(
                        id = (
                                item.archiveId.takeIf { it != 0L }
                                    ?: item.alertId.toLong()
                                ).toString(),
                        title = item.groupBy.ifBlank {
                            item.subject.ifBlank { "Archived Alert" }
                        },
                        date = item.alertSendDate,
                        subject = item.subject.ifBlank { item.groupBy },
                        description = displayMessage,
                        currentStatus = if (item.errorCode == 412) {
                            "Session expired"
                        } else {
                            "Archived"
                        },
                        archiveId = item.archiveId,
                        alertId = item.alertId,
                        aggregateAlertId = item.aggregateAlertId,
                        alertDefId = item.alertDefId,
                        applicationId = item.applicationId
                    )
                }

                LaunchedEffect(user?.id, user?.sessionId) {
                    if (user != null) {
                        val criteria = archivedSearchPreferences.load()

                        archivedViewModel.load(
                            userId = user.id.toString(),
                            sessionId = user.sessionId.orEmpty(),
                            criteria = criteria
                        )
                    }
                }

                ArchivedAlertsScreen(
                    alerts = archivedUiItems,
                    isLoading = archivedLoading,
                    errorMessage = archivedError,
                    onRetryClick = {
                        archivedViewModel.refresh()
                    },
                    onRefreshClick = {
                        archivedViewModel.refresh()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSearchClick = {
                        navController.navigate(
                            Screen.SearchArchivedAlerts.route
                        )
                    },
                    onAlertClick = { alert ->
                        selectedArchivedAlert = alert
                        navController.navigate(
                            Screen.ArchivedAlertDetails.route
                        )
                    }
                )
            }

            // =================================================
            // 7. ARCHIVED ALERT DETAILS
            // =================================================

            composable(Screen.ArchivedAlertDetails.route) {
                val alert = selectedArchivedAlert

                if (alert != null) {
                    ArchivedAlertDetailsScreen(
                        alert = alert,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("Archived alert details are unavailable.")
                    }
                }
            }
            // =================================================
            // 8. PROFILE
            // =================================================

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // =================================================
            // 9. CHANGE PASSWORD
            // =================================================

            composable(Screen.ChangePassword.route) {
                val user = loggedInUser

                val changePasswordRepository = remember {
                    ChangePasswordRepository(
                        RetrofitClient.apiService
                    )
                }

                val changePasswordFactory =
                    remember(changePasswordRepository) {
                        ChangePasswordViewModelFactory(
                            changePasswordRepository
                        )
                    }

                val changePasswordViewModel: ChangePasswordViewModel =
                    viewModel(factory = changePasswordFactory)

                val changePasswordState by
                changePasswordViewModel.uiState.collectAsState()

                ChangePasswordScreen(
                    loginId = user?.loginId.orEmpty(),
                    onBackClick = {
                        navController.popBackStack()
                    },
                    isLoading = changePasswordState.isLoading,
                    message = changePasswordState.message,
                    isSuccess = changePasswordState.isSuccess,
                    onSaveClick = { loginId, oldPassword, newPassword ->
                        if (user == null) {
                            return@ChangePasswordScreen
                        }

                        changePasswordViewModel.changePassword(
                            username = loginId,
                            userId = user.id.toString(),
                            oldPassword = oldPassword,
                            newPassword = newPassword,
                            confirmPassword = newPassword
                        )
                    }
                )
            }

            // =================================================
            // 10. ABOUT
            // =================================================

            composable(Screen.About.route) {
                AboutScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // =================================================
            // 11. SEARCH ALERTS
            //
            // The ViewModel is owned by Home's back-stack entry.
            // After Search is tapped, Home appears immediately,
            // while the API request continues in the shared
            // ViewModel. Results update Home when the API succeeds.
            // =================================================

            composable(Screen.SearchAlerts.route) {
                val user = loggedInUser
                val context = LocalContext.current.applicationContext

                val settingsDataStore = remember(context) {
                    SettingsDataStore(context)
                }

                val alertsFactory = remember(settingsDataStore) {
                    AlertsViewModelFactory(settingsDataStore)
                }

                val homeBackStackEntry = remember(navController) {
                    navController.getBackStackEntry(Screen.Home.route)
                }

                val alertsViewModel: AlertsViewModel = viewModel(
                    viewModelStoreOwner = homeBackStackEntry,
                    factory = alertsFactory
                )
                val isAlertsLoading by alertsViewModel.isLoading.collectAsState()
                SearchAlertsScreen(
                    alertsViewModel = alertsViewModel,
                    userId = user?.id?.toString().orEmpty(),
                    userTypeId = user?.userTypeId?.toString().orEmpty(),
                    sessionId = user?.sessionId.orEmpty(),

                    onBackClick = {
                        navController.popBackStack()
                    },

                    // Alerts tab returns to Home.
                    onAlertsTabClick = {
                        navController.popBackStack(
                            route = Screen.Home.route,
                            inclusive = false
                        )
                    },

                    // Subscriptions tab.
                    onSubscriptionsTabClick = {
                        navController.navigate(Screen.Subscriptions.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },

                    // Forward Alerts tab.
                    onForwardAlertsTabClick = {
                        navController.navigate(Screen.ForwardAlerts.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },

                    // Menu returns to Home.
                    onMenuTabClick = {
                        navController.popBackStack(
                            route = Screen.Home.route,
                            inclusive = false
                        )
                    },

                    // Search and return to Home immediately.
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
                            // Step 1: Return to Home.
                            navController.popBackStack(
                                route = Screen.Home.route,
                                inclusive = false
                            )

                            // Step 2: Request the filtered alerts.
                            alertsViewModel.searchAlerts(
                                userId = user.id.toString(),
                                startDate = startDate,
                                endDate = endDate,
                                keyword = keyword,
                                viewByFlag = viewBy,
                                applicationId = application,
                                alertDefinitionId = alertDefinition,
                                sessionId = user.sessionId.orEmpty(),

                                // Step 3: Update Home after success.
                                onSuccess = { results ->
                                    searchedAlerts = results

                                    // Save results for this user.
                                    searchPersistence.saveAlerts(
                                        userId = user.id.toString(),
                                        alerts = results
                                    )

                                    Log.d(
                                        "AlertsNavigation",
                                        "Search succeeded: ${results.size} alerts"
                                    )

                                    results.forEach { alert ->
                                        Log.d(
                                            "AlertsAPI",
                                            "id=${alert.id}, " +
                                                    "alertId=${alert.alertId}, " +
                                                    "subject=${alert.subject}"
                                        )
                                    }
                                }
                            )
                        }
                    }
                )
            }

            // =================================================
            // 12. SEARCH ARCHIVED ALERTS
            // =================================================

            composable(Screen.SearchArchivedAlerts.route) {
                val user = loggedInUser

                SearchArchivedAlertsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },

                    onSearchClick = { criteria ->
                        if (user != null) {
                            // Save the filters selected by the user.
                            archivedSearchPreferences.save(criteria)

                            // Load archived alerts using those filters.
                            archivedViewModel.load(
                                userId = user.id.toString(),
                                sessionId = user.sessionId.orEmpty(),
                                criteria = criteria
                            )

                            // Return to Archived Alerts.
                            navController.popBackStack()
                        }
                    },

                    onResetClick = {
                        // Reset is handled inside SearchArchivedAlertsScreen.
                    }
                )
            }

            // =================================================
            // 13. SUBSCRIPTIONS
            // =================================================

            composable(Screen.Subscriptions.route) {
                val user = loggedInUser

                val subscriptionRepository = remember {
                    SubscriptionRepositoryImpl(
                        RetrofitClient.apiService
                    )
                }

                val subscriptionFactory =
                    remember(subscriptionRepository) {
                        SubscriptionsViewModelFactory(
                            subscriptionRepository
                        )
                    }

                val subscriptionViewModel: SubscriptionsViewModel =
                    viewModel(factory = subscriptionFactory)

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
                        Log.d(
                            "SubscriptionNavigation",
                            "Opening subscription details for ID: $subscriptionId"
                        )

                        navController.navigate(
                            Screen.SubscriptionDetails.createRoute(
                                subscriptionId
                            )
                        ) {
                            launchSingleTop = true
                        }
                    },

                    onSearchClick = {
                        navController.navigate(
                            Screen.SearchSubscription.route
                        )
                    },

                    viewModel = subscriptionViewModel,
                    userId = user?.id?.toString().orEmpty(),
                    sessionId = user?.sessionId.orEmpty()
                )
            }

            // =================================================
            // 14. SUBSCRIPTION DETAILS
            // =================================================

            composable(
                route = Screen.SubscriptionDetails.route,
                arguments = listOf(
                    navArgument("subscriptionId") {
                        type = NavType.StringType
                    }
                )
            ) { entry ->

                val subscriptionId = entry.arguments
                    ?.getString("subscriptionId")
                    ?: "new"

                val user = loggedInUser

                // Reuse the ViewModel belonging to Subscriptions.
                val subscriptionsEntry = remember(navController) {
                    navController.getBackStackEntry(
                        Screen.Subscriptions.route
                    )
                }

                val subscriptionViewModel: SubscriptionsViewModel =
                    viewModel(
                        viewModelStoreOwner = subscriptionsEntry
                    )

                SubscriptionDetailsScreen(
                    subscriptionId = subscriptionId,
                    viewModel = subscriptionViewModel,
                    userId = user?.id?.toString().orEmpty(),
                    userTypeId = user?.userTypeId?.toString().orEmpty(),
                    sessionId = user?.sessionId.orEmpty(),

                    onBackClick = {
                        navController.popBackStack()
                    },

                    onSaveSuccess = {
                        // Clear any previous save message.
                        subscriptionViewModel.clearMessages()

                        // Refresh the list after a successful save.
                        subscriptionViewModel.loadSubscriptions(
                            userId = user?.id?.toString().orEmpty(),
                            sessionId = user?.sessionId.orEmpty(),
                            applicationId = "0",
                            searchName = "",
                            searchActive = 2
                        )

                        navController.popBackStack()
                    }
                )
            }

            // =================================================
            // 15. SEARCH SUBSCRIPTIONS
            // =================================================

            composable(Screen.SearchSubscription.route) {

                val subscriptionsEntry = remember(navController) {
                    navController.getBackStackEntry(
                        Screen.Subscriptions.route
                    )
                }

                // Share the Subscriptions ViewModel with search.
                val subscriptionViewModel: SubscriptionsViewModel =
                    viewModel(
                        viewModelStoreOwner = subscriptionsEntry
                    )

                var applicationOptions by remember {
                    mutableStateOf<List<ApplicationOption>>(emptyList())
                }

                var isApplicationsLoading by remember {
                    mutableStateOf(false)
                }

                var applicationsError by remember {
                    mutableStateOf<String?>(null)
                }

                var applicationRetryKey by remember {
                    mutableStateOf(0)
                }

                val userId = loggedInUser?.id?.toString().orEmpty()
                val sessionId = loggedInUser?.sessionId.orEmpty()
                val userTypeId =
                    loggedInUser?.userTypeId?.toString().orEmpty()

                // Load application choices from the API.
                LaunchedEffect(
                    userId,
                    sessionId,
                    userTypeId,
                    applicationRetryKey
                ) {
                    if (
                        userId.isBlank() ||
                        sessionId.isBlank() ||
                        userTypeId.isBlank()
                    ) {
                        applicationOptions = emptyList()
                        applicationsError =
                            "User ID, user type ID, or session ID is missing."
                        isApplicationsLoading = false
                        return@LaunchedEffect
                    }

                    isApplicationsLoading = true
                    applicationsError = null

                    try {
                        val response =
                            RetrofitClient.apiService.getApplicationList(
                                userId = userId,
                                userTypeId = userTypeId,
                                sessionId = sessionId
                            )

                        Log.d(
                            "SubscriptionSearch",
                            "getApplicationList response: $response"
                        )

                        val parsedApplications =
                            mutableListOf<ApplicationOption>()

                        if (response.isJsonArray) {
                            response.asJsonArray.forEach { item ->
                                if (item.isJsonObject) {
                                    val obj = item.asJsonObject
                                    val idElement = obj.get("id")
                                    val nameElement = obj.get("name")

                                    val id = if (
                                        idElement != null &&
                                        !idElement.isJsonNull
                                    ) {
                                        idElement.asString
                                    } else {
                                        null
                                    }

                                    val name = if (
                                        nameElement != null &&
                                        !nameElement.isJsonNull
                                    ) {
                                        nameElement.asString
                                    } else {
                                        null
                                    }

                                    if (
                                        !id.isNullOrBlank() &&
                                        !name.isNullOrBlank()
                                    ) {
                                        parsedApplications.add(
                                            ApplicationOption(
                                                id = id,
                                                name = name
                                            )
                                        )
                                    }
                                }
                            }

                            applicationOptions = parsedApplications

                            if (parsedApplications.isEmpty()) {
                                applicationsError =
                                    "The server returned no application records."
                            }

                            Log.d(
                                "SubscriptionSearch",
                                "Parsed applications: $applicationOptions"
                            )
                        } else {
                            applicationOptions = emptyList()
                            applicationsError =
                                "Unexpected response from the application API."
                        }
                    } catch (e: Exception) {
                        applicationOptions = emptyList()
                        applicationsError =
                            e.message ?: "Unable to load applications."

                        Log.e(
                            "SubscriptionSearch",
                            "Failed to load applications",
                            e
                        )
                    } finally {
                        isApplicationsLoading = false
                    }
                }

                SearchSubscriptionScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },

                    onSearchClick = {
                            alertName,
                            applicationId,
                            searchActive ->

                        Log.d(
                            "SubscriptionSearch",
                            "Search filters: name=$alertName, " +
                                    "applicationId=$applicationId, " +
                                    "searchActive=$searchActive"
                        )

                        subscriptionViewModel.loadSubscriptions(
                            userId = userId,
                            sessionId = sessionId,
                            applicationId = applicationId,
                            searchName = alertName.trim(),
                            searchActive = searchActive
                        )

                        navController.popBackStack()
                    },

                    applications = applicationOptions,
                    isApplicationsLoading = isApplicationsLoading,
                    applicationsError = applicationsError,

                    onRetryApplications = {
                        applicationRetryKey++
                    }
                )
            }
            // =================================================
            // 16. FORWARD ALERTS
            // =================================================

            composable(Screen.ForwardAlerts.route) {
                val user = loggedInUser

                // Load the forward-alert list whenever the user or
                // the selected search criteria change.
                LaunchedEffect(
                    user?.id,
                    user?.userTypeId,
                    user?.sessionId,
                    user?.applicationIds,
                    forwardSearchCriteria.applicationId,
                    forwardSearchCriteria.userName,
                    forwardSearchCriteria.email,
                    forwardSearchCriteria.active
                ) {
                    if (user != null) {
                        forwardViewModel.loadForwardAlerts(
                            userId = user.id.toString(),
                            userTypeId = user.userTypeId.toString(),
                            sessionId = user.sessionId.orEmpty(),
                            applicationIds = user.applicationIds.orEmpty(),
                            searchName = forwardSearchCriteria.userName,
                            searchEmail = forwardSearchCriteria.email,
                            searchActive = forwardSearchCriteria.active,
                            searchAppId = forwardSearchCriteria.applicationId
                        )
                    }
                }

                ForwardAlertsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },

                    onCreateClick = {
                        selectedForwardAlert = null
                        navController.navigate(
                            Screen.ForwardAlertDetails.route
                        )
                    },

                    onEditClick = { alert ->
                        selectedForwardAlert = alert
                        navController.navigate(
                            Screen.ForwardAlertDetails.route
                        )
                    },

                    onSearchClick = {
                        navController.navigate(
                            Screen.SearchForwardAlerts.route
                        )
                    },

                    alerts = forwardListState.alerts,
                    isLoading = forwardListState.isLoading,
                    errorMessage = forwardListState.errorMessage,

                    onToggleStatus = { alert, active ->
                        forwardViewModel.updateForwardAlertStatus(
                            alert = alert,
                            active = active
                        )
                    },

                    onSetAllStatus = { active ->
                        forwardViewModel.updateAllForwardAlertsStatus(
                            active
                        )
                    },

                    onDeleteClick = { alert ->
                        forwardViewModel.deleteForwardAlert(alert)
                    }
                )
            }

            // =================================================
            // 17. CREATE / EDIT FORWARD ALERT
            // =================================================

            composable(Screen.ForwardAlertDetails.route) {
                val user = loggedInUser

                // Load applications for the signed-in user.
                LaunchedEffect(
                    user?.id,
                    user?.userTypeId,
                    user?.sessionId
                ) {
                    if (user != null) {
                        forwardViewModel.loadApplications(
                            userId = user.id.toString(),
                            userTypeId = user.userTypeId.toString(),
                            sessionId = user.sessionId.orEmpty()
                        )
                    }
                }

                ForwardAlertDetailsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },

                    onSaveClick = {},

                    applications = forwardFormState.applications.map { application ->
                        com.aquila.pocxpertalerts.ui.screens.ForwardApplicationOption(
                            id = application.id,
                            name = application.name
                        )
                    },

                    users = forwardFormState.users.map { forwardUser ->
                        com.aquila.pocxpertalerts.ui.screens.ForwardUserOption(
                            id = forwardUser.id,
                            name = forwardUser.name,
                            email = forwardUser.email
                        )
                    },

                    emailFromApi = forwardCreateState.email,

                    onUserSelected = { selectedUser ->
                        forwardViewModel.loadCreateUserEmail(
                            selectedUser.id
                        )
                    },

                    isSaving = forwardCreateState.isSaving,
                    saveMessage = forwardCreateState.message,
                    saveSucceeded = forwardCreateState.isSuccess,

                    editDetails = selectedForwardAlert?.let { alert ->
                        ForwardAlertEditDetails(
                            forwardAlertId = alert.id,
                            mappedUserId = alert.mappedUserId,
                            applicationId = alert.applicationId,
                            email = alert.email,
                            sortOrder = alert.sortOrder,
                            active = alert.active
                        )
                    },

                    onApplicationSelected = { application ->
                        forwardViewModel.loadUsers(application.id)
                    },

                    onMapClick = {
                            applicationId,
                            mappedUserId,
                            email,
                            sortOrder,
                            active ->

                        forwardViewModel.createForwardAlert(
                            applicationId = applicationId,
                            mappedUserId = mappedUserId,
                            email = email,
                            sortOrder = sortOrder,
                            active = active,
                            forwardAlertId = selectedForwardAlert?.id ?: "0"
                        )
                    }
                )
            }

            // =================================================
            // 18. SEARCH FORWARD ALERTS
            // =================================================

            composable(Screen.SearchForwardAlerts.route) {
                val user = loggedInUser

                // Load applications and users for the search filters.
                LaunchedEffect(
                    user?.id,
                    user?.userTypeId,
                    user?.sessionId
                ) {
                    if (user != null) {
                        forwardViewModel.loadApplications(
                            userId = user.id.toString(),
                            userTypeId = user.userTypeId.toString(),
                            sessionId = user.sessionId.orEmpty()
                        )

                        forwardViewModel.loadUsers("0")
                    }
                }

                SearchForwardAlertsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },

                    onSearchClick = {
                            applicationId,
                            userName,
                            email,
                            active ->

                        val selectedAppId =
                            applicationId.ifBlank { "0" }

                        // Save the filters. Returning to the list
                        // triggers its LaunchedEffect and reloads data.
                        forwardSearchCriteria = ForwardSearchCriteria(
                            applicationId = selectedAppId,
                            userName = userName,
                            email = email,
                            active = active
                        )

                        navController.popBackStack()
                    },

                    applications = forwardFormState.applications,
                    users = forwardFormState.users,
                    isLoadingApplications =
                        forwardFormState.isLoadingApplications,
                    isLoadingUsers =
                        forwardFormState.isLoadingUsers,

                    onApplicationSelected = { application ->
                        forwardViewModel.loadUsers(
                            application?.id ?: "0"
                        )
                    },

                    onResetClick = {
                        forwardViewModel.resetSearchForm()
                    }
                )
            }
        } // End NavHost
    } // End Box
} // End AppNavigation