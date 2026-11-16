
package com.aquila.pocxpertalerts.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.aquila.pocxpertalerts.data.local.UserSessionDataStore
import com.aquila.pocxpertalerts.data.model.AlertDto
import com.aquila.pocxpertalerts.data.remote.RetrofitClient
import com.aquila.pocxpertalerts.data.remote.SessionExpirationManager
import com.aquila.pocxpertalerts.data.repository.ArchivedAlertsCache
import com.aquila.pocxpertalerts.data.repository.ArchivedAlertsRepository
import com.aquila.pocxpertalerts.data.repository.ArchivedDynamicAction
import com.aquila.pocxpertalerts.data.repository.ArchivedSearchPreferences
import com.aquila.pocxpertalerts.data.repository.ChangePasswordRepository
import com.aquila.pocxpertalerts.data.repository.ForwardAlertsRepository
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
import com.aquila.pocxpertalerts.viewmodel.ForwardAlertsViewModelFactory
import com.aquila.pocxpertalerts.viewmodel.SubscriptionsViewModel
import com.aquila.pocxpertalerts.viewmodel.SubscriptionsViewModelFactory

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    data object ArchivedAlertDetails : Screen("archived_alert_details")
    data object Profile : Screen("profile")
    data object ChangePassword : Screen("change_password")

    data object AlertDetail : Screen("alert_detail/{alertId}") {
        fun createRoute(alertId: String) = "alert_detail/$alertId"
    }

    data object SearchAlerts : Screen("search_alerts")
    data object SearchArchivedAlerts : Screen("search_archived_alerts")
    data object Subscriptions : Screen("subscriptions")

    data object SubscriptionDetails :
        Screen("subscription_details/{subscriptionId}") {

        fun createRoute(subscriptionId: String) =
            "subscription_details/$subscriptionId"
    }

    data object SearchSubscription : Screen("search_subscription")
    data object ForwardAlerts : Screen("forward_alerts")
    data object ForwardAlertDetails : Screen("forward_alert_details")
    data object SearchForwardAlerts : Screen("search_forward_alerts")
}

// ============================================================
// SEARCH MODELS
// ============================================================

data class ForwardSearchCriteria(
    val applicationId: String = "0",
    val userName: String = "",
    val email: String = "",
    val active: Int = 1
)

private data class AlertRefreshCriteria(
    val startDate: String,
    val endDate: String,
    val keyword: String = "",
    val viewByFlag: String = "0",
    val applicationId: String = "0",
    val alertDefinitionId: String = "0"
)

private fun defaultAlertApiDate(daysFromToday: Int): String {
    return SimpleDateFormat("MM-dd-yyyy", Locale.US).format(
        Calendar.getInstance()
            .apply { add(Calendar.DAY_OF_YEAR, daysFromToday) }
            .time
    )
}

// ============================================================
// APP NAVIGATION
// ============================================================

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val appContext = LocalContext.current.applicationContext

    // --------------------------------------------------------
    // DATA STORES
    // Initialize these before creating repositories.
    // --------------------------------------------------------

    val settingsDataStore = remember(appContext) {
        SettingsDataStore(appContext)
    }

    val userSessionDataStore = remember(appContext) {
        UserSessionDataStore(appContext)
    }

    // --------------------------------------------------------
    // SHARED API SERVICE
    //
    // Every API repository in this navigation file uses this
    // service. Its base URL comes from the saved Settings URL.
    // --------------------------------------------------------

    val apiService = remember(settingsDataStore) {
        val configuredUrl = runBlocking {
            settingsDataStore.webServiceUrl.first()
        }

        Log.d(
            "ApiConfiguration",
            "Creating shared API service using configured URL: $configuredUrl"
        )

        RetrofitClient.createApiService(configuredUrl)
    }

    // --------------------------------------------------------
    // ARCHIVED ALERTS
    // --------------------------------------------------------

    val archivedSearchPreferences = remember(appContext) {
        ArchivedSearchPreferences(appContext)
    }

    val archivedCache = remember(appContext) {
        ArchivedAlertsCache(appContext)
    }

    val archivedRepository = remember(apiService) {
        ArchivedAlertsRepository(apiService)
    }

    val archivedFactory = remember(
        archivedRepository,
        archivedCache
    ) {
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
    // ALERTS
    // --------------------------------------------------------

    val searchPersistence = remember(appContext) {
        SearchAlertsPersistence(appContext)
    }

    val alertsFactory = remember(settingsDataStore) {
        AlertsViewModelFactory(settingsDataStore)
    }

    val alertsViewModel: AlertsViewModel =
        viewModel(factory = alertsFactory)

    val alertsLoading by alertsViewModel.isLoading.collectAsState()

    var lastAlertRefreshCriteria by remember {
        mutableStateOf(
            AlertRefreshCriteria(
                startDate = defaultAlertApiDate(-5),
                endDate = defaultAlertApiDate(0)
            )
        )
    }

    // --------------------------------------------------------
    // FORWARD ALERTS
    // --------------------------------------------------------

    val forwardRepository = remember(apiService) {
        ForwardAlertsRepository(apiService)
    }

    val forwardFactory = remember(forwardRepository) {
        ForwardAlertsViewModelFactory(forwardRepository)
    }

    val forwardViewModel: ForwardAlertsViewModel =
        viewModel(factory = forwardFactory)

    val forwardListState by forwardViewModel.listState.collectAsState()
    val forwardFormState by forwardViewModel.formState.collectAsState()
    val forwardCreateState by forwardViewModel.createState.collectAsState()

    // --------------------------------------------------------
    // USER AND SCREEN STATES
    // --------------------------------------------------------

    var loggedInUser by remember {
        mutableStateOf<User?>(null)
    }

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

    // --------------------------------------------------------
    // RESTORE SAVED SESSION
    // --------------------------------------------------------

    LaunchedEffect(Unit) {

        if (loggedInUser == null) {

            val savedUser = try {
                userSessionDataStore.readUser()
            } catch (_: Exception) {
                null
            }

            if (savedUser != null && savedUser.pwdExpiryFlag == 0) {

                loggedInUser = savedUser

                searchedAlerts = searchPersistence.loadAlerts(
                    savedUser.id.toString()
                )

                SessionExpirationManager.resetForNewSession()

                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    // --------------------------------------------------------
    // SESSION EXPIRATION OBSERVER
    // --------------------------------------------------------

    LaunchedEffect(navController) {

        SessionExpirationManager.events.collect {

            if (loggedInUser != null) {

                loggedInUser = null

                scope.launch {
                    try {
                        userSessionDataStore.clear()
                    } catch (_: Exception) {
                    }
                }

                selectedAlert = null
                selectedArchivedAlert = null
                selectedForwardAlert = null
                pendingDeleteAction = null
                showDeleteConfirmation = false

                Toast.makeText(
                    appContext,
                    "Your session has expired. Please sign in again.",
                    Toast.LENGTH_LONG
                ).show()

                navController.navigate(Screen.Login.route) {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    // ========================================================
    // NAVIGATION HOST
    // ========================================================

    Box(modifier = Modifier.fillMaxSize()) {

        NavHost(
            navController = navController,
            startDestination = Screen.Login.route
        ) {

            // =================================================
            // LOGIN
            // =================================================

            composable(Screen.Login.route) {

                LoginScreen(

                    onLoginSuccess = { user ->

                        SessionExpirationManager.resetForNewSession()

                        loggedInUser = user

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

                    onPasswordExpired = { user ->

                        loggedInUser = user

                        navController.navigate(Screen.ChangePassword.route) {
                            launchSingleTop = true
                        }
                    },

                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            // =================================================
            // HOME
            // =================================================

            composable(Screen.Home.route) {

                val homeUser = loggedInUser

                LaunchedEffect(
                    homeUser?.id,
                    homeUser?.sessionId
                ) {

                    val currentUser = homeUser

                    if (
                        currentUser != null &&
                        currentUser.id.toString().isNotBlank() &&
                        !currentUser.sessionId.isNullOrBlank()
                    ) {

                        val criteria = lastAlertRefreshCriteria

                        alertsViewModel.searchAlerts(
                            userId = currentUser.id.toString(),
                            startDate = criteria.startDate,
                            endDate = criteria.endDate,
                            keyword = criteria.keyword,
                            viewByFlag = criteria.viewByFlag,
                            applicationId = criteria.applicationId,
                            alertDefinitionId = criteria.alertDefinitionId,
                            sessionId = currentUser.sessionId.orEmpty(),

                            onSuccess = { freshAlerts ->

                                searchedAlerts = freshAlerts

                                searchPersistence.saveAlerts(
                                    userId = currentUser.id.toString(),
                                    alerts = freshAlerts
                                )
                            }
                        )
                    }
                }

                HomeScreen(
                    alerts = searchedAlerts,
                    userId = loggedInUser?.id?.toString().orEmpty(),
                    sessionId = loggedInUser?.sessionId.orEmpty(),
                    isRefreshing = alertsLoading,

                    onRefreshClick = {

                        val currentUser = loggedInUser

                        if (currentUser != null) {

                            val criteria = lastAlertRefreshCriteria

                            alertsViewModel.searchAlerts(
                                userId = currentUser.id.toString(),
                                startDate = criteria.startDate,
                                endDate = criteria.endDate,
                                keyword = criteria.keyword,
                                viewByFlag = criteria.viewByFlag,
                                applicationId = criteria.applicationId,
                                alertDefinitionId = criteria.alertDefinitionId,
                                sessionId = currentUser.sessionId.orEmpty(),

                                onSuccess = { refreshedAlerts ->

                                    searchedAlerts = refreshedAlerts

                                    searchPersistence.saveAlerts(
                                        userId = currentUser.id.toString(),
                                        alerts = refreshedAlerts
                                    )
                                }
                            )
                        }
                    },

                    onAlertsChanged = { updatedAlerts ->

                        searchedAlerts = updatedAlerts

                        loggedInUser?.id?.toString()?.let { currentUserId ->

                            searchPersistence.saveAlerts(
                                userId = currentUserId,
                                alerts = updatedAlerts
                            )
                        }
                    },

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

                        scope.launch {
                            try {
                                userSessionDataStore.clear()
                            } catch (_: Exception) {
                            }
                        }

                        selectedAlert = null

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
            // ALERT DETAILS
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
                        userId = loggedInUser?.id?.toString().orEmpty(),
                        sessionId = loggedInUser?.sessionId.orEmpty(),

                        onBackClick = {
                            navController.popBackStack()
                        },

                        onActionSuccess = {

                            searchedAlerts = searchedAlerts.filterNot { item ->
                                item.id == alert.id ||
                                        (
                                                item.alertId == alert.alertId &&
                                                        item.id == alert.id
                                                )
                            }

                            loggedInUser?.id?.toString()?.let { currentUserId ->

                                searchPersistence.saveAlerts(
                                    userId = currentUserId,
                                    alerts = searchedAlerts
                                )
                            }
                        }
                    )

                } else {

                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("Alert details are unavailable.")
                    }
                }
            }

            // =================================================
            // NOTIFICATIONS
            // =================================================

            composable(Screen.Notifications.route) {

                NotificationsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // =================================================
            // ARCHIVED ALERTS
            // =================================================

            composable(Screen.Archived.route) {

                val user = loggedInUser

                val archivedUiItems = archivedAlerts.map { item ->

                    val displayMessage = item.message
                        .replace("<br/><br/>", "\n")
                        .replace("<br/>", "\n")

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

                LaunchedEffect(
                    user?.id,
                    user?.sessionId
                ) {

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
                        navController.navigate(Screen.SearchArchivedAlerts.route)
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
            // ARCHIVED ALERT DETAILS
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
            // PROFILE
            // =================================================

            composable(Screen.Profile.route) {

                ProfileScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // =================================================
            // CHANGE PASSWORD
            // =================================================

            composable(Screen.ChangePassword.route) {

                val user = loggedInUser

                // Use the shared configured API service.
                val changePasswordRepository = remember(apiService) {
                    ChangePasswordRepository(apiService)
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

                LaunchedEffect(changePasswordState.isSuccess) {

                    if (changePasswordState.isSuccess && user != null) {

                        try {
                            userSessionDataStore.saveUser(user)
                        } catch (_: Exception) {
                        }

                        SessionExpirationManager.resetForNewSession()

                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.ChangePassword.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }

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
            // SETTINGS
            // =================================================

            composable(Screen.Settings.route) {

                SettingsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // =================================================
            // ABOUT
            // =================================================

            composable(Screen.About.route) {

                AboutScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // =================================================
            // SEARCH ALERTS
            // =================================================

            composable(Screen.SearchAlerts.route) {

                val user = loggedInUser

                SearchAlertsScreen(
                    alertsViewModel = alertsViewModel,
                    userId = user?.id?.toString().orEmpty(),
                    userTypeId = user?.userTypeId?.toString().orEmpty(),
                    sessionId = user?.sessionId.orEmpty(),

                    onBackClick = {
                        navController.popBackStack()
                    },

                    onAlertsTabClick = {
                        navController.popBackStack(
                            route = Screen.Home.route,
                            inclusive = false
                        )
                    },

                    onSubscriptionsTabClick = {
                        navController.navigate(Screen.Subscriptions.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },

                    onForwardAlertsTabClick = {
                        navController.navigate(Screen.ForwardAlerts.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },

                    onMenuTabClick = {
                        navController.popBackStack(
                            route = Screen.Home.route,
                            inclusive = false
                        )
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

                            lastAlertRefreshCriteria = AlertRefreshCriteria(
                                startDate = startDate,
                                endDate = endDate,
                                keyword = keyword,
                                viewByFlag = viewBy,
                                applicationId = application,
                                alertDefinitionId = alertDefinition
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

                                    searchedAlerts = results

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

                                    navController.popBackStack(
                                        route = Screen.Home.route,
                                        inclusive = false,
                                        saveState = true
                                    )
                                }
                            )
                        }
                    }
                )
            }

            // =================================================
            // SEARCH ARCHIVED ALERTS
            // =================================================

            composable(Screen.SearchArchivedAlerts.route) {

                val user = loggedInUser

                SearchArchivedAlertsScreen(

                    onBackClick = {
                        navController.popBackStack()
                    },

                    onSearchClick = { criteria ->

                        if (user != null) {

                            archivedSearchPreferences.save(criteria)

                            archivedViewModel.load(
                                userId = user.id.toString(),
                                sessionId = user.sessionId.orEmpty(),
                                criteria = criteria
                            )

                            navController.popBackStack()
                        }
                    },

                    onResetClick = {
                        // Reset is handled inside SearchArchivedAlertsScreen.
                    }
                )
            }

            // =================================================
            // SUBSCRIPTIONS
            // =================================================

            composable(Screen.Subscriptions.route) {

                val user = loggedInUser

                // Use the shared configured API service.
                val subscriptionRepository = remember(apiService) {
                    SubscriptionRepositoryImpl(apiService)
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
            // SUBSCRIPTION DETAILS
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

                        subscriptionViewModel.clearMessages()

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
            // SEARCH SUBSCRIPTIONS
            // =================================================

            composable(Screen.SearchSubscription.route) {

                val subscriptionsEntry = remember(navController) {
                    navController.getBackStackEntry(
                        Screen.Subscriptions.route
                    )
                }

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
                val userTypeId = loggedInUser?.userTypeId?.toString().orEmpty()

                // Load applications using the shared API service.
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

                        val response = apiService.getApplicationList(
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
            // FORWARD ALERTS LIST
            // =================================================

            composable(Screen.ForwardAlerts.route) {

                val user = loggedInUser

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
                        forwardViewModel.updateAllForwardAlertsStatus(active)
                    },

                    onDeleteClick = { alert ->
                        forwardViewModel.deleteForwardAlert(alert)
                    }
                )
            }

            // =================================================
            // FORWARD ALERT DETAILS
            // =================================================

            composable(Screen.ForwardAlertDetails.route) {

                val user = loggedInUser

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

                    users = forwardFormState.users.map { user ->

                        com.aquila.pocxpertalerts.ui.screens.ForwardUserOption(
                            id = user.id,
                            name = user.name,
                            email = user.email
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
            // SEARCH FORWARD ALERTS
            // =================================================

            composable(Screen.SearchForwardAlerts.route) {

                val user = loggedInUser

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
        }
    }
}
