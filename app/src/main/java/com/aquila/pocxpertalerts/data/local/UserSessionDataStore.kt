package com.aquila.pocxpertalerts.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aquila.pocxpertalerts.domain.model.User
import kotlinx.coroutines.flow.first
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private val Context.userSessionPreferences by preferencesDataStore(name = "user_session")

/**
 * Stores the session token encrypted with an AES/GCM key protected by Android Keystore.
 * Other profile fields remain in Preferences DataStore for compatibility with the current app.
 */
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
        val encryptedSessionId = stringPreferencesKey("session_id_encrypted")
        // Legacy plaintext key, read only for one-time migration.
        val legacySessionId = stringPreferencesKey("session_id")
        val applicationIds = stringPreferencesKey("application_ids")
        val loginId = stringPreferencesKey("login_id")
    }

    suspend fun saveUser(user: User) {
        val encryptedSession = user.sessionId
            ?.takeIf { it.isNotBlank() }
            ?.let(SessionTokenCipher::encrypt)

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
            if (encryptedSession.isNullOrBlank()) {
                p.remove(Keys.encryptedSessionId)
            } else {
                p[Keys.encryptedSessionId] = encryptedSession
            }
            p.remove(Keys.legacySessionId)
            p[Keys.applicationIds] = user.applicationIds.orEmpty()
            p[Keys.loginId] = user.loginId.orEmpty()
        }
    }

    suspend fun readUser(): User? {
        val preferences = context.userSessionPreferences.data.first()
        val id = preferences[Keys.id] ?: 0
        if (id <= 0) return null

        val encryptedSession = preferences[Keys.encryptedSessionId]
        val sessionId = if (!encryptedSession.isNullOrBlank()) {
            runCatching { SessionTokenCipher.decrypt(encryptedSession) }
                .getOrElse {
                    // Do not silently fall back to an invalid or corrupted token.
                    SessionTokenCipher.resetKey()
                    clear()
                    return null
                }
        } else {
            // Migrate a session written by older app versions.
            val legacySession = preferences[Keys.legacySessionId].orEmpty()
            if (legacySession.isNotBlank()) {
                val encrypted = runCatching {
                    SessionTokenCipher.encrypt(legacySession)
                }.getOrNull() ?: run {
                    clear()
                    return null
                }

                context.userSessionPreferences.edit { p ->
                    p[Keys.encryptedSessionId] = encrypted
                    p.remove(Keys.legacySessionId)
                }
            }
            legacySession
        }

        if (sessionId.isBlank()) return null

        return User(
            id = id,
            userId = preferences[Keys.userId] ?: 0,
            userTypeId = preferences[Keys.userTypeId] ?: 0,
            userImportCount = preferences[Keys.userImportCount] ?: 0,
            emailNotifFlag = preferences[Keys.emailNotifFlag] ?: 0,
            pwdExpiryFlag = preferences[Keys.pwdExpiryFlag] ?: 0,
            fname = preferences[Keys.fname].orEmpty().ifBlank { null },
            lname = preferences[Keys.lname].orEmpty().ifBlank { null },
            email = preferences[Keys.email].orEmpty().ifBlank { null },
            alertuserid = preferences[Keys.alertuserid].orEmpty().ifBlank { null },
            errorMsg = preferences[Keys.errorMsg].orEmpty().ifBlank { null },
            sessionId = sessionId,
            applicationIds = preferences[Keys.applicationIds].orEmpty().ifBlank { null },
            loginId = preferences[Keys.loginId].orEmpty().ifBlank { null }
        )
    }

    suspend fun clear() {
        context.userSessionPreferences.edit { it.clear() }
    }
}

private object SessionTokenCipher {
    private const val KEY_ALIAS = "xpertalerts_session_token_key"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_LENGTH_BYTES = 12
    private const val TAG_LENGTH_BITS = 128

    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val payload = cipher.iv + encrypted
        return Base64.encodeToString(payload, Base64.NO_WRAP)
    }

    fun decrypt(encoded: String): String {
        val payload = Base64.decode(encoded, Base64.NO_WRAP)
        require(payload.size > IV_LENGTH_BYTES) { "Invalid encrypted session token." }

        val iv = payload.copyOfRange(0, IV_LENGTH_BYTES)
        val encrypted = payload.copyOfRange(IV_LENGTH_BYTES, payload.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateKey(),
            GCMParameterSpec(TAG_LENGTH_BITS, iv)
        )
        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }

    fun resetKey() {
        runCatching {
            KeyStore.getInstance("AndroidKeyStore").apply {
                load(null)
                if (containsAlias(KEY_ALIAS)) deleteEntry(KEY_ALIAS)
            }
        }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val existingKey = runCatching {
            keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        }.getOrNull()
        if (existingKey != null) return existingKey
        runCatching {
            if (keyStore.containsAlias(KEY_ALIAS)) keyStore.deleteEntry(KEY_ALIAS)
        }

        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }
}
