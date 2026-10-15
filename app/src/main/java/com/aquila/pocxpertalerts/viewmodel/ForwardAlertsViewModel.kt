package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.repository.ForwardAlertsRepository
import com.aquila.pocxpertalerts.ui.screens.ForwardApplicationOption
import com.aquila.pocxpertalerts.ui.screens.ForwardUserOption
import com.google.gson.JsonElement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ForwardAlertFormState(
    val applications: List<ForwardApplicationOption> = emptyList(),
    val users: List<ForwardUserOption> = emptyList(),
    val selectedEmail: String = "",
    val isLoadingApplications: Boolean = false,
    val isLoadingUsers: Boolean = false,
    val isSaving: Boolean = false,
    val message: String? = null,
    val saved: Boolean = false
)

class ForwardAlertsViewModel(
    private val repository: ForwardAlertsRepository =
        ForwardAlertsRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(ForwardAlertFormState())
    val state: StateFlow<ForwardAlertFormState> = _state.asStateFlow()

    private var currentUserId: String = ""
    private var currentUserTypeId: String = ""
    private var currentSessionId: String = ""

    // ---------------------------------------------------------
    // INITIALIZE LOGGED-IN USER AND LOAD APPLICATIONS
    // ---------------------------------------------------------

    fun loadApplications(
        userId: String,
        userTypeId: String,
        sessionId: String
    ) {
        currentUserId = userId
        currentUserTypeId = userTypeId
        currentSessionId = sessionId

        if (userId.isBlank() || sessionId.isBlank()) {
            _state.value = _state.value.copy(
                isLoadingApplications = false,
                message = "User ID or session ID is missing."
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoadingApplications = true,
                message = null
            )

            runCatching {
                repository.getApplications(
                    userId = currentUserId,
                    userTypeId = currentUserTypeId,
                    sessionId = currentSessionId
                )
            }.onSuccess { response ->
                _state.value = _state.value.copy(
                    applications = parseApplications(response),
                    isLoadingApplications = false,
                    message = null
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoadingApplications = false,
                    message = error.message
                        ?: "Unable to load applications."
                )
            }
        }
    }

    // ---------------------------------------------------------
    // LOAD USERS WHEN AN APPLICATION IS SELECTED
    // ---------------------------------------------------------

    fun loadUsers(applicationId: String) {
        if (applicationId.isBlank()) {
            _state.value = _state.value.copy(
                users = emptyList(),
                message = "Please select an application."
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                users = emptyList(),
                selectedEmail = "",
                isLoadingUsers = true,
                message = null
            )

            runCatching {
                repository.getUsers(
                    userId = currentUserId,
                    userTypeId = currentUserTypeId,
                    applicationId = applicationId,
                    sessionId = currentSessionId
                )
            }.onSuccess { response ->
                _state.value = _state.value.copy(
                    users = parseUsers(response),
                    isLoadingUsers = false,
                    message = null
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoadingUsers = false,
                    message = error.message
                        ?: "Unable to load users."
                )
            }
        }
    }

    // ---------------------------------------------------------
    // LOAD EMAIL FOR SELECTED USER
    // ---------------------------------------------------------

    fun loadUserEmail(userId: String) {
        if (userId.isBlank()) {
            _state.value = _state.value.copy(
                selectedEmail = "",
                message = "Please select a user."
            )
            return
        }

        viewModelScope.launch {
            runCatching {
                repository.getUserEmail(
                    userId = userId,
                    sessionId = currentSessionId
                )
            }.onSuccess { response ->
                val email = findString(
                    response,
                    setOf("email", "emailAddress", "mailId")
                )

                _state.value = _state.value.copy(
                    selectedEmail = email.orEmpty(),
                    message = if (email.isNullOrBlank()) {
                        "The API did not return an email address."
                    } else {
                        null
                    }
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    message = error.message
                        ?: "Unable to load the user's email."
                )
            }
        }
    }

    // ---------------------------------------------------------
    // CHECK DUPLICATE AND SAVE MAPPING
    // ---------------------------------------------------------

    fun saveMapping(
        forwardAlertId: String = "0",
        mappedUserId: String,
        applicationId: String,
        email: String,
        sortOrder: String,
        active: Boolean
    ) {
        if (
            currentUserId.isBlank() ||
            currentSessionId.isBlank() ||
            mappedUserId.isBlank() ||
            applicationId.isBlank() ||
            email.isBlank()
        ) {
            _state.value = _state.value.copy(
                message = "Required user, application, or email details are missing.",
                saved = false
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isSaving = true,
                message = null,
                saved = false
            )

            try {
                val duplicateResponse = repository.checkDuplicate(
                    forwardAlertId = forwardAlertId,
                    baseUserId = currentUserId,
                    mappedUserId = mappedUserId,
                    applicationId = applicationId,
                    sessionId = currentSessionId
                )

                if (indicatesDuplicate(duplicateResponse)) {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        saved = false,
                        message = "The selected user is already mapped."
                    )
                    return@launch
                }

                val saveResponse = repository.saveMapping(
                    forwardAlertId = forwardAlertId,
                    baseUserId = currentUserId,
                    mappedUserId = mappedUserId,
                    email = email,
                    sortOrder = sortOrder,
                    active = if (active) "1" else "0",
                    applicationId = applicationId,
                    sessionId = currentSessionId
                )

                if (indicatesFailure(saveResponse)) {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        saved = false,
                        message = "The server reported that saving failed."
                    )
                } else {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        saved = true,
                        message = "Save request completed. Verify the server response."
                    )
                }
            } catch (error: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    saved = false,
                    message = error.message
                        ?: "Unable to save the mapping."
                )
            }
        }
    }

    // ---------------------------------------------------------
    // RESET FORM FEEDBACK
    // ---------------------------------------------------------

    fun clearMessage() {
        _state.value = _state.value.copy(message = null)
    }

    fun clearSavedState() {
        _state.value = _state.value.copy(
            saved = false,
            message = null
        )
    }

    // ---------------------------------------------------------
    // PARSE APPLICATION RESPONSE
    // ---------------------------------------------------------

    private fun parseApplications(
        response: JsonElement
    ): List<ForwardApplicationOption> {
        return response.asArrayOrNested()
            .mapNotNull { item ->
                val id = findString(
                    item,
                    setOf("applicationId", "appId", "id")
                ) ?: return@mapNotNull null

                val name = findString(
                    item,
                    setOf("applicationName", "appName", "name")
                ) ?: return@mapNotNull null

                ForwardApplicationOption(
                    id = id,
                    name = name
                )
            }
    }

    // ---------------------------------------------------------
    // PARSE USER RESPONSE
    // ---------------------------------------------------------

    private fun parseUsers(
        response: JsonElement
    ): List<ForwardUserOption> {
        return response.asArrayOrNested()
            .mapNotNull { item ->
                val id = findString(
                    item,
                    setOf("userId", "baseUserId", "id")
                ) ?: return@mapNotNull null

                val name = findString(
                    item,
                    setOf("userName", "fullName", "name", "fname")
                ) ?: return@mapNotNull null

                val email = findString(
                    item,
                    setOf("email", "emailAddress", "mailId")
                ).orEmpty()

                ForwardUserOption(
                    id = id,
                    name = name,
                    email = email
                )
            }
    }

    // ---------------------------------------------------------
    // HANDLE ARRAY OR NESTED JSON RESPONSE
    // ---------------------------------------------------------

    private fun JsonElement.asArrayOrNested(): List<JsonElement> {
        if (isJsonArray) {
            return asJsonArray.toList()
        }

        if (!isJsonObject) {
            return emptyList()
        }

        val obj = asJsonObject

        for (key in listOf(
            "data",
            "result",
            "results",
            "applications",
            "users"
        )) {
            val value = obj.get(key) ?: continue

            if (value.isJsonArray) {
                return value.asJsonArray.toList()
            }
        }

        return emptyList()
    }

    // ---------------------------------------------------------
    // FIND A FIELD IN JSON
    // ---------------------------------------------------------

    private fun findString(
        element: JsonElement,
        keys: Set<String>
    ): String? {
        if (element.isJsonNull) {
            return null
        }

        if (element.isJsonPrimitive) {
            return element.asString
        }

        if (element.isJsonArray) {
            for (item in element.asJsonArray) {
                val result = findString(item, keys)
                if (result != null) return result
            }
            return null
        }

        if (!element.isJsonObject) {
            return null
        }

        val obj = element.asJsonObject
        val normalizedKeys = keys.map { it.lowercase() }.toSet()

        for ((key, value) in obj.entrySet()) {
            if (
                key.lowercase() in normalizedKeys &&
                value.isJsonPrimitive &&
                !value.isJsonNull
            ) {
                return value.asString
            }
        }

        for ((_, value) in obj.entrySet()) {
            val result = findString(value, keys)
            if (result != null) return result
        }

        return null
    }

    // ---------------------------------------------------------
    // DUPLICATE RESPONSE CHECK
    // Verify against the real backend response before production.
    // ---------------------------------------------------------

    private fun indicatesDuplicate(
        response: JsonElement
    ): Boolean {
        val text = response.toString().lowercase()

        return text.contains("already mapped") ||
                text.contains("\"duplicate\":true") ||
                text.contains("\"isduplicate\":true")
    }

    // ---------------------------------------------------------
    // SAVE RESPONSE CHECK
    // ---------------------------------------------------------

    private fun indicatesFailure(
        response: JsonElement
    ): Boolean {
        val text = response.toString().lowercase()

        return text.contains("\"success\":false") ||
                text.contains("\"status\":\"error\"") ||
                text.contains("\"error\":true")
    }
}
