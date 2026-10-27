package com.aquila.pocxpertalerts.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.repository.ForwardAlertsRepository
import com.aquila.pocxpertalerts.ui.screens.ForwardAlertItem
import com.google.gson.JsonElement
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ForwardAlertsListState(
    val alerts: List<ForwardAlertItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class ForwardApplicationOption(
    val id: String,
    val name: String
)

data class ForwardUserOption(
    val id: String,
    val name: String,
    val email: String = ""
)

data class ForwardAlertsFormState(
    val applications: List<ForwardApplicationOption> = emptyList(),
    val users: List<ForwardUserOption> = emptyList(),
    val isLoadingApplications: Boolean = false,
    val isLoadingUsers: Boolean = false,
    val errorMessage: String? = null,
    val isDuplicate: Boolean = false,
    val duplicateChecked: Boolean = false
)

data class CreateForwardAlertState(
    val email: String = "",
    val isLoadingEmail: Boolean = false,
    val isSaving: Boolean = false,
    val message: String? = null,
    val isSuccess: Boolean = false,
    val isDuplicate: Boolean = false,
    val duplicateChecked: Boolean = false
)

class ForwardAlertsViewModel(
    private val repository: ForwardAlertsRepository =
        ForwardAlertsRepository()
) : ViewModel() {

    private val _listState =
        MutableStateFlow(ForwardAlertsListState())

    val listState: StateFlow<ForwardAlertsListState> =
        _listState.asStateFlow()
    private val _formState = MutableStateFlow(ForwardAlertsFormState())

    val formState: StateFlow<ForwardAlertsFormState> =
        _formState.asStateFlow()

    private val _createState =
        MutableStateFlow(CreateForwardAlertState())

    val createState: StateFlow<CreateForwardAlertState> =
        _createState.asStateFlow()

    private var currentUserId = ""
    private var currentUserTypeId = ""
    private var currentSessionId = ""
    private var currentApplicationIds = ""

    private var currentSearchName = ""
    private var currentSearchEmail = ""
    private var currentSearchActive = 2
    private var currentSearchAppId = "0"

    fun loadForwardAlerts(
        userId: String,
        userTypeId: String,
        sessionId: String,
        applicationIds: String,
        searchName: String = "",
        searchEmail: String = "",
        searchActive: Int = 2,
        searchAppId: String = "0"
    ) {
        currentUserId = userId
        currentUserTypeId = userTypeId
        currentSessionId = sessionId
        currentApplicationIds = applicationIds

        currentSearchName = searchName
        currentSearchEmail = searchEmail
        currentSearchActive = searchActive
        currentSearchAppId = searchAppId

        if (userId.isBlank() || sessionId.isBlank()) {
            _listState.value = ForwardAlertsListState(
                errorMessage = "User ID or session ID is missing."
            )
            return
        }

        viewModelScope.launch {
            _listState.value = _listState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val response = repository.getForwardAlerts(
                    baseUserId = userId,
                    applicationIds = applicationIds,
                    sessionId = sessionId,
                    searchName = searchName,
                    searchEmail = searchEmail,
                    searchActive = searchActive,
                    searchAppId = searchAppId
                )

                Log.d("ForwardAlertsAPI", "List response: $response")

                val (errorCode, errorMessage) = getApiError(response)

                if (errorCode == 412) {
                    _listState.value = ForwardAlertsListState(
                        errorMessage = errorMessage
                            ?: "Your session has expired. Please log in again."
                    )
                    return@launch
                }

                if (errorCode != null && errorCode != 0) {
                    _listState.value = ForwardAlertsListState(
                        errorMessage = errorMessage
                            ?: "The server rejected the request: $errorCode"
                    )
                    return@launch
                }

                val records = response.asRecordList()

                val alerts = records.mapNotNull { item ->
                    if (!item.isJsonObject) return@mapNotNull null

                    val obj = item.asJsonObject

                    fun value(vararg keys: String): String {
                        for (key in keys) {
                            val element = obj.get(key)
                            if (element != null && !element.isJsonNull) {
                                return runCatching {
                                    element.asString
                                }.getOrDefault("")
                            }
                        }
                        return ""
                    }

                    val id = value("forwardAlertId", "id")
                    if (id.isBlank()) return@mapNotNull null

                    val activeValue = value("active", "status")
                    ForwardAlertItem(
                        id = value("forwardAlertId"),
                        userName = value("userName"),
                        email = value("email"),
                        applicationName = value("applicationName"),
                        active = value("active") == "1",
                        mappedUserId = value("mapUserId"),
                        userId = value("baseUserId"),
                        applicationId = value("applicationId"),
                        sortOrder = value("sortOrder").ifBlank { "0" }
                    )
                }

                _listState.value = ForwardAlertsListState(
                    alerts = alerts,
                    errorMessage = if (alerts.isEmpty()) {
                        errorMessage ?: "No forward alerts found."
                    } else {
                        null
                    }
                )
            } catch (e: Exception) {
                Log.e("ForwardAlertsAPI", "Failed to load list", e)

                _listState.value = ForwardAlertsListState(
                    errorMessage = e.message
                        ?: "Unable to load Forward Alerts."
                )
            }
        }
    }

    fun loadApplications(
        userId: String,
        userTypeId: String,
        sessionId: String

    ) {
        println(
            "ForwardAlertsAPI loadApplications CALLED: " +
                    "userId=$userId, userTypeId=$userTypeId, " +
                    "sessionIdPresent=${sessionId.isNotBlank()}"
        )
        currentUserId = userId
        currentUserTypeId = userTypeId
        currentSessionId = sessionId

        viewModelScope.launch {
            _formState.value = _formState.value.copy(
                isLoadingApplications = true,
                errorMessage = null
            )

            try {
                val response = repository.getApplications(
                    userId = userId,
                    userTypeId = userTypeId,
                    sessionId = sessionId
                )

                println("ForwardAlertsAPI Applications response: $response")

                val records = response.toOptionRecords()
                val applications = records.mapNotNull { item ->
                    if (!item.isJsonObject) return@mapNotNull null

                    val obj = item.asJsonObject

                    fun read(vararg keys: String): String =
                        keys.firstNotNullOfOrNull { key ->
                            obj.get(key)
                                ?.takeIf { !it.isJsonNull }
                                ?.let { runCatching { it.asString }.getOrNull() }
                                ?.takeIf { it.isNotBlank() }
                        }.orEmpty()

                    val id = read("applicationId", "appId", "id")
                    val name = read("applicationName", "appName", "name")

                    if (id.isBlank() || name.isBlank()) null
                    else ForwardApplicationOption(id, name)
                }

                _formState.value = _formState.value.copy(
                    applications = applications,
                    isLoadingApplications = false
                )
            } catch (e: Exception) {
                _formState.value = _formState.value.copy(
                    isLoadingApplications = false,
                    errorMessage = e.message ?: "Unable to load applications."
                )
            }
        }
    }


    fun loadUsers(applicationId: String = "0") {
        if (
            currentUserId.isBlank() ||
            currentUserTypeId.isBlank() ||
            currentSessionId.isBlank()
        ) {
            _formState.value = _formState.value.copy(
                errorMessage = "User session is missing."
            )

            Log.e(
                "ForwardUsers",
                "Cannot load users: user ID, user type ID, or session ID is missing."
            )
            return
        }

        viewModelScope.launch {
            _formState.value = _formState.value.copy(
                isLoadingUsers = true,
                users = emptyList(),
                errorMessage = null
            )

            try {
                Log.d(
                    "ForwardUsers",
                    "Loading users for applicationId=$applicationId"
                )

                val response = repository.getUsers(
                    userId = currentUserId,
                    userTypeId = currentUserTypeId,
                    applicationId = applicationId,
                    sessionId = currentSessionId
                )

                Log.d("ForwardUsers", "Raw response: $response")

                val records = response.toOptionRecords()

                Log.d(
                    "ForwardUsers",
                    "Response records count: ${records.size}"
                )

                val parsedUsers = records.mapNotNull { item ->
                    if (!item.isJsonObject) {
                        Log.w("ForwardUsers", "Skipping non-object record: $item")
                        return@mapNotNull null
                    }

                    val obj = item.asJsonObject

                    fun read(vararg keys: String): String =
                        keys.firstNotNullOfOrNull { key ->
                            obj.get(key)
                                ?.takeIf { !it.isJsonNull }
                                ?.let {
                                    runCatching { it.asString }.getOrNull()
                                }
                                ?.takeIf { it.isNotBlank() }
                        }.orEmpty()

                    val id = read(
                        "userId",
                        "baseUserId",
                        "mapUserId",
                        "mappedUserId",
                        "id"
                    )

                    val firstName = read("firstName", "firstname")
                    val lastName = read("lastName", "lastname")

                    val name = read(
                        "userName",
                        "username",
                        "name",
                        "fullName",
                        "displayName"
                    ).ifBlank {
                        listOf(firstName, lastName)
                            .filter { it.isNotBlank() }
                            .joinToString(" ")
                    }

                    val email = read("email", "userEmail", "mailId")

                    Log.d(
                        "ForwardUsers",
                        "Parsed candidate: id=$id, name=$name, email=$email"
                    )

                    if (id.isBlank() || name.isBlank()) {
                        Log.w(
                            "ForwardUsers",
                            "Skipping record because ID or name is missing: $obj"
                        )
                        null
                    } else {
                        ForwardUserOption(
                            id = id,
                            name = name,
                            email = email
                        )
                    }
                }

                Log.d(
                    "ForwardUsers",
                    "Successfully parsed ${parsedUsers.size} users"
                )

                _formState.value = _formState.value.copy(
                    users = parsedUsers,
                    isLoadingUsers = false,
                    errorMessage = if (parsedUsers.isEmpty()) {
                        "No users were returned. Check the API response in Logcat."
                    } else {
                        null
                    }
                )
            } catch (e: Exception) {
                Log.e(
                    "ForwardUsers",
                    "Failed to load users for applicationId=$applicationId",
                    e
                )

                _formState.value = _formState.value.copy(
                    isLoadingUsers = false,
                    errorMessage = e.message ?: "Unable to load users."
                )
            }
        }
    }


    fun loadCreateUserEmail(userId: String) {
        if (userId.isBlank() || currentSessionId.isBlank()) {
            _createState.value = _createState.value.copy(
                email = "",
                isLoadingEmail = false,
                message = "User ID or session ID is missing."
            )
            return
        }

        viewModelScope.launch {
            _createState.value = _createState.value.copy(
                isLoadingEmail = true,
                email = "",
                message = null
            )

            try {
                val response = repository.getUserEmail(
                    userId = userId,
                    sessionId = currentSessionId
                )

                Log.d("ForwardAlertsAPI", "User email response: $response")

                val email = when {
                    response.isJsonPrimitive ->
                        response.asString

                    response.isJsonObject -> {
                        val obj = response.asJsonObject
                        listOf("email", "userEmail", "mailId")
                            .firstNotNullOfOrNull { key ->
                                obj.get(key)
                                    ?.takeIf { !it.isJsonNull }
                                    ?.let {
                                        runCatching { it.asString }.getOrNull()
                                    }
                                    ?.takeIf { it.isNotBlank() }
                            }.orEmpty()
                    }

                    response.isJsonArray -> {
                        val obj = response.asJsonArray
                            .firstOrNull()
                            ?.takeIf { it.isJsonObject }
                            ?.asJsonObject

                        listOf("email", "userEmail", "mailId")
                            .firstNotNullOfOrNull { key ->
                                obj?.get(key)
                                    ?.takeIf { !it.isJsonNull }
                                    ?.let {
                                        runCatching { it.asString }.getOrNull()
                                    }
                                    ?.takeIf { it.isNotBlank() }
                            }.orEmpty()
                    }

                    else -> ""
                }

                _createState.value = _createState.value.copy(
                    email = email,
                    isLoadingEmail = false,
                    message = if (email.isBlank()) {
                        "Email was not returned by the API."
                    } else {
                        null
                    }
                )
            } catch (e: Exception) {
                Log.e("ForwardAlertsAPI", "Failed to load user email", e)

                _createState.value = _createState.value.copy(
                    isLoadingEmail = false,
                    message = e.message ?: "Unable to load user email."
                )
            }
        }
    }

    fun createForwardAlert(
        applicationId: String,
        mappedUserId: String,
        email: String,
        sortOrder: String,
        active: Boolean,
        forwardAlertId: String = "0"
    ) {
        if (currentUserId.isBlank() || currentSessionId.isBlank()) {
            _createState.value = _createState.value.copy(
                isSaving = false,
                message = "User session is missing.",
                isSuccess = false
            )
            return
        }

        if (applicationId.isBlank() || applicationId == "0") {
            _createState.value = _createState.value.copy(
                message = "Please select an application.",
                isSuccess = false
            )
            return
        }

        if (mappedUserId.isBlank() || mappedUserId == "0") {
            _createState.value = _createState.value.copy(
                message = "Please select a user.",
                isSuccess = false
            )
            return
        }

        viewModelScope.launch {
            _createState.value = _createState.value.copy(
                isSaving = true,
                message = null,
                isSuccess = false,
                isDuplicate = false,
                duplicateChecked = false
            )

            try {
                // First check whether the mapping already exists.
                val duplicateResponse = repository.getDuplicateForwardAlert(
                    forwardAlertId = forwardAlertId,
                    baseUserId = currentUserId,
                    mappedUserId = mappedUserId,
                    applicationId = applicationId,
                    sessionId = currentSessionId
                )

                Log.d(
                    "ForwardAlertsAPI",
                    "Duplicate check response: $duplicateResponse"
                )

                val duplicateObject = when {
                    duplicateResponse.isJsonObject ->
                        duplicateResponse.asJsonObject

                    duplicateResponse.isJsonArray ->
                        duplicateResponse.asJsonArray
                            .firstOrNull()
                            ?.takeIf { it.isJsonObject }
                            ?.asJsonObject

                    else -> null
                }

                val errorCode = duplicateObject
                    ?.get("errorCode")
                    ?.takeIf { !it.isJsonNull }
                    ?.let { runCatching { it.asInt }.getOrNull() }

                if (errorCode == 412) {
                    _createState.value = _createState.value.copy(
                        isSaving = false,
                        message = "Your session has expired. Please log in again."
                    )
                    return@launch
                }

                val duplicateCount = duplicateObject
                    ?.get("duplicateCount")
                    ?.takeIf { !it.isJsonNull }
                    ?.let { runCatching { it.asInt }.getOrNull() }

                if (duplicateCount == null) {
                    _createState.value = _createState.value.copy(
                        isSaving = false,
                        message = "Could not verify duplicate mapping. Please check the API response."
                    )
                    return@launch
                }

                if (duplicateCount > 0) {
                    _createState.value = _createState.value.copy(
                        isSaving = false,
                        isDuplicate = true,
                        duplicateChecked = true,
                        message = "This user is already mapped to this application."
                    )
                    return@launch
                }

                // No duplicate: save the mapping.
                val saveResponse = repository.saveMappedUser(
                    forwardAlertId = forwardAlertId,
                    baseUserId = currentUserId,
                    mappedUserId = mappedUserId,
                    email = email,
                    sortOrder = sortOrder,
                    active = if (active) "1" else "0",
                    applicationId = applicationId,
                    sessionId = currentSessionId
                )

                Log.d(
                    "ForwardAlertsAPI",
                    "Save mapping response: $saveResponse"
                )

                val saveObject = when {
                    saveResponse.isJsonObject ->
                        saveResponse.asJsonObject

                    saveResponse.isJsonArray ->
                        saveResponse.asJsonArray
                            .firstOrNull()
                            ?.takeIf { it.isJsonObject }
                            ?.asJsonObject

                    else -> null
                }

                val saveErrorCode = saveObject
                    ?.get("errorCode")
                    ?.takeIf { !it.isJsonNull }
                    ?.let { runCatching { it.asInt }.getOrNull() }

                val saveMessage = saveObject
                    ?.get("message")
                    ?.takeIf { !it.isJsonNull }
                    ?.let { runCatching { it.asString }.getOrNull() }

                when (saveErrorCode) {
                    412 -> {
                        _createState.value = _createState.value.copy(
                            isSaving = false,
                            message = "Your session has expired. Please log in again."
                        )
                    }

                    1, 2 -> {
                        _createState.value = _createState.value.copy(
                            isSaving = false,
                            message = saveMessage
                                ?: "The mapping could not be saved."
                        )
                    }

                    else -> {
                        _createState.value = _createState.value.copy(
                            isSaving = false,
                            isSuccess = true,
                            message = saveMessage ?: "Forward alert mapped successfully."
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ForwardAlertsAPI", "Create forward alert failed", e)

                _createState.value = _createState.value.copy(
                    isSaving = false,
                    isSuccess = false,
                    message = e.message ?: "Unable to save forward alert."
                )
            }
        }
    }

    fun resetSearchForm() {
        _formState.value = _formState.value.copy(
            users = emptyList(),
            errorMessage = null
        )
        loadUsers("0")
    }

    private fun JsonElement.toOptionRecords(): List<JsonElement> {
        if (isJsonArray) return asJsonArray.toList()
        if (!isJsonObject) return emptyList()

        val obj = asJsonObject

        for (key in listOf("data", "results", "records", "applications", "users")) {
            val child = obj.get(key)
            if (child != null && child.isJsonArray) {
                return child.asJsonArray.toList()
            }
        }

        return emptyList()
    }

    fun updateForwardAlertStatus(
        alert: ForwardAlertItem,
        active: Boolean
    ) {
        if (alert.mappedUserId.isBlank() ||
            alert.applicationId.isBlank()
        ) {
            _listState.value = _listState.value.copy(
                errorMessage =
                    "The API response is missing the mapped user ID or application ID."
            )
            return
        }

        runListAction("Update status") {
            repository.updateUserStatus(
                userId = currentUserId,
                status = if (active) "1" else "0",
                mappedUserId = alert.mappedUserId,
                applicationId = alert.applicationId,
                sessionId = currentSessionId
            )
        }
    }

    fun updateAllForwardAlertsStatus(active: Boolean) {
        runListAction("Update all statuses") {
            repository.updateAllStatus(
                userId = currentUserId,
                status = if (active) "1" else "0",
                sessionId = currentSessionId
            )
        }
    }

    fun deleteForwardAlert(alert: ForwardAlertItem) {
        if (alert.id.isBlank()) {
            _listState.value = _listState.value.copy(
                errorMessage = "Forward Alert ID is missing."
            )
            return
        }

        runListAction("Delete mapping") {
            repository.deleteForwardAlert(
                forwardAlertId = alert.id,
                sessionId = currentSessionId
            )
        }
    }

    private fun runListAction(
        actionName: String,
        request: suspend () -> JsonElement
    ) {
        if (currentUserId.isBlank() || currentSessionId.isBlank()) {
            _listState.value = _listState.value.copy(
                errorMessage = "User session is missing."
            )
            return
        }

        viewModelScope.launch {
            _listState.value = _listState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val response = request()

                Log.d(
                    "ForwardAlertsAPI",
                    "$actionName response: $response"
                )

                val (errorCode, errorMessage) = getApiError(response)

                if (errorCode == 412) {
                    _listState.value = _listState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                            ?: "Your session has expired."
                    )
                    return@launch
                }

                if (errorCode != null && errorCode != 0) {
                    _listState.value = _listState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                            ?: "$actionName failed (errorCode $errorCode)."
                    )
                    return@launch
                }

                loadForwardAlerts(
                    userId = currentUserId,
                    userTypeId = currentUserTypeId,
                    sessionId = currentSessionId,
                    applicationIds = currentApplicationIds,
                    searchName = currentSearchName,
                    searchEmail = currentSearchEmail,
                    searchActive = currentSearchActive,
                    searchAppId = currentSearchAppId
                )
            } catch (e: Exception) {
                Log.e("ForwardAlertsAPI", "$actionName failed", e)

                _listState.value = _listState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "$actionName failed."
                )
            }
        }
    }

    private fun getApiError(
        response: JsonElement
    ): Pair<Int?, String?> {
        val obj = when {
            response.isJsonObject -> response.asJsonObject
            response.isJsonArray ->
                response.asJsonArray.firstOrNull()
                    ?.takeIf { it.isJsonObject }
                    ?.asJsonObject
            else -> null
        } ?: return null to null

        val code = obj.get("errorCode")
            ?.takeIf { !it.isJsonNull }
            ?.let { runCatching { it.asInt }.getOrNull() }

        val message = listOf(
            "errorMessage",
            "message",
            "error"
        ).firstNotNullOfOrNull { key ->
            obj.get(key)
                ?.takeIf { !it.isJsonNull }
                ?.let { runCatching { it.asString }.getOrNull() }
                ?.takeIf { it.isNotBlank() }
        }

        return code to message
    }

    private fun JsonElement.asRecordList(): List<JsonElement> {
        if (isJsonArray) return asJsonArray.toList()
        if (!isJsonObject) return emptyList()

        val obj = asJsonObject

        for (key in listOf("data", "results", "records", "forwardAlerts")) {
            val child = obj.get(key)
            if (child != null && child.isJsonArray) {
                return child.asJsonArray.toList()
            }
        }

        return if (
            obj.has("forwardAlertId") || obj.has("id")
        ) {
            listOf(this)
        } else {
            emptyList()
        }
    }
}