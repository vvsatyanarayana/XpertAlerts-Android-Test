package com.aquila.pocxpertalerts.data.remote

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Response

/** Detects both HTTP 401 and the legacy API's JSON errorCode=412 session-expired response. */
class SessionExpirationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val isLoginRequest = chain.request().url.encodedPath.endsWith("/authenticateUserForDevices")
        if (!isLoginRequest && (response.code == 401 || responseIndicatesExpiredSession(response))) {
            SessionExpirationManager.notifySessionExpired()
        }

        return response
    }

    private fun responseIndicatesExpiredSession(response: Response): Boolean {
        return try {
            // peekBody does not consume the body, so Retrofit can still parse it normally.
            val body = response.peekBody(MAX_BODY_BYTES).string()
            if (body.isBlank()) return false
            hasExpiredErrorCode(JsonParser.parseString(body))
        } catch (_: Exception) {
            // Non-JSON and malformed responses are not treated as session expiration.
            false
        }
    }

    private fun hasExpiredErrorCode(element: JsonElement): Boolean {
        if (element.isJsonObject) {
            val obj = element.asJsonObject
            val code = obj.get("errorCode")
                ?.takeUnless { it.isJsonNull }
                ?.let { runCatching { it.asString }.getOrNull() }
            if (code == "412") return true

            return obj.entrySet().any { (_, value) -> hasExpiredErrorCode(value) }
        }

        if (element.isJsonArray) {
            return element.asJsonArray.any { hasExpiredErrorCode(it) }
        }

        return false
    }

    private companion object {
        const val MAX_BODY_BYTES = 1024L * 1024L
    }
}
