package com.aquila.pocxpertalerts.notifications

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.data.local.UserSessionDataStore
import com.aquila.pocxpertalerts.data.remote.RetrofitClient
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/** Queues token registration and retries transient failures using WorkManager. */
object FcmTokenRegistrar {
    private const val UNIQUE_WORK_NAME = "xpertalerts_fcm_token_registration"
    private const val INPUT_TOKEN = "fcm_token"
    private const val INPUT_DEVICE_ID = "device_id"

    fun enqueueRegistration(context: Context, token: String, deviceId: String) {
        if (token.isBlank() || deviceId.isBlank()) return
        val request = OneTimeWorkRequestBuilder<FcmTokenRegistrationWorker>()
            .setInputData(
                Data.Builder()
                    .putString(INPUT_TOKEN, token)
                    .putString(INPUT_DEVICE_ID, deviceId)
                    .build()
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    suspend fun registerIfSignedIn(context: Context, token: String, deviceId: String): Unit =
        withContext(Dispatchers.IO) {
            require(token.isNotBlank()) { "Firebase token is empty." }
            require(deviceId.isNotBlank()) { "Device ID is empty." }

            val appContext = context.applicationContext
            val user = UserSessionDataStore(appContext).readUser() ?: return@withContext
            if (user.sessionId.isNullOrBlank()) return@withContext

            val configuredUrl = SettingsDataStore(appContext).webServiceUrl.first()
            val apiService = RetrofitClient.createApiService(configuredUrl)
            apiService.saveTokenDetails(
                deviceTypeId = 1,
                deviceId = deviceId,
                tokenId = token
            )
            Unit
        }

    /** Stops pending registration and invalidates the current FCM token on logout. */
    fun onLogout(context: Context) {
        val appContext = context.applicationContext
        WorkManager.getInstance(appContext).cancelUniqueWork(UNIQUE_WORK_NAME)
        runCatching { FirebaseMessaging.getInstance().deleteToken() }
        androidx.core.app.NotificationManagerCompat.from(appContext).cancelAll()
    }
}

class FcmTokenRegistrationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val token = inputData.getString("fcm_token").orEmpty()
        val deviceId = inputData.getString("device_id").orEmpty()
        if (token.isBlank() || deviceId.isBlank()) return Result.failure()

        // A queued job must never register a device after the user has signed out.
        val user = try {
            UserSessionDataStore(applicationContext).readUser()
        } catch (_: Exception) {
            return Result.retry()
        }
        if (user?.sessionId.isNullOrBlank()) return Result.success()

        return try {
            FcmTokenRegistrar.registerIfSignedIn(applicationContext, token, deviceId)
            Result.success()
        } catch (cancelled: kotlinx.coroutines.CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            if (runAttemptCount >= 8) Result.failure() else Result.retry()
        }
    }
}
