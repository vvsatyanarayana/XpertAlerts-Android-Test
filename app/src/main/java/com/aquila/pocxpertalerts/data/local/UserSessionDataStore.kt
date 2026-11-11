package com.aquila.pocxpertalerts.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aquila.pocxpertalerts.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.userSessionPreferences by preferencesDataStore(name = "user_session")

/** Persists the same user/session fields the legacy app stored locally for automatic login. */
class UserSessionDataStore(private val context: Context) {
    private object Keys {
        val id = intPreferencesKey("id")
        val userId = intPreferencesKey("user_id")
        val userTypeId = intPreferencesKey("user_type_id")
        val userImportCount = intPreferencesKey("user_import_count")
        val emailNotifFlag = intPreferencesKey("email_notif_flag")
        val pwdExpiryFlag = intPreferencesKey("pwd_expiry_flag")
        val fname = stringPreferencesKey("fname")
        val lname = stringPreferencesKey("lname")
        val email = stringPreferencesKey("email")
        val alertuserid = stringPreferencesKey("alertuserid")
        val errorMsg = stringPreferencesKey("error_msg")
        val sessionId = stringPreferencesKey("session_id")
        val applicationIds = stringPreferencesKey("application_ids")
        val loginId = stringPreferencesKey("login_id")
    }

    suspend fun saveUser(user: User) {
        context.userSessionPreferences.edit { p ->
            p[Keys.id] = user.id
            p[Keys.userId] = user.userId
            p[Keys.userTypeId] = user.userTypeId
            p[Keys.userImportCount] = user.userImportCount
            p[Keys.emailNotifFlag] = user.emailNotifFlag
            p[Keys.pwdExpiryFlag] = user.pwdExpiryFlag
            p[Keys.fname] = user.fname.orEmpty()
            p[Keys.lname] = user.lname.orEmpty()
            p[Keys.email] = user.email.orEmpty()
            p[Keys.alertuserid] = user.alertuserid.orEmpty()
            p[Keys.errorMsg] = user.errorMsg.orEmpty()
            p[Keys.sessionId] = user.sessionId.orEmpty()
            p[Keys.applicationIds] = user.applicationIds.orEmpty()
            p[Keys.loginId] = user.loginId.orEmpty()
        }
    }

    suspend fun readUser(): User? = context.userSessionPreferences.data.map { p ->
        val id = p[Keys.id] ?: 0
        val sessionId = p[Keys.sessionId].orEmpty()
        if (id <= 0 || sessionId.isBlank()) null else User(
            id = id,
            userId = p[Keys.userId] ?: 0,
            userTypeId = p[Keys.userTypeId] ?: 0,
            userImportCount = p[Keys.userImportCount] ?: 0,
            emailNotifFlag = p[Keys.emailNotifFlag] ?: 0,
            pwdExpiryFlag = p[Keys.pwdExpiryFlag] ?: 0,
            fname = p[Keys.fname].orEmpty().ifBlank { null },
            lname = p[Keys.lname].orEmpty().ifBlank { null },
            email = p[Keys.email].orEmpty().ifBlank { null },
            alertuserid = p[Keys.alertuserid].orEmpty().ifBlank { null },
            errorMsg = p[Keys.errorMsg].orEmpty().ifBlank { null },
            sessionId = sessionId,
            applicationIds = p[Keys.applicationIds].orEmpty().ifBlank { null },
            loginId = p[Keys.loginId].orEmpty().ifBlank { null }
        )
    }.first()

    suspend fun clear() {
        context.userSessionPreferences.edit { it.clear() }
    }
}
