
package com.aquila.pocxpertalerts.data.repository

import com.aquila.pocxpertalerts.data.remote.ApiService

class ChangePasswordRepository(
    private val apiService: ApiService
) {

    suspend fun changePassword(
        loginId: String,
        oldPassword: String,
        newPassword: String,
        userId: String
    ): ChangePasswordResult {

        val response = apiService.saveChangePassword(
            username = loginId,
            oldPassword = oldPassword,
            newPassword = newPassword,
            userId = userId
        )

        val responseObject = when {
            response.isJsonArray &&
                    response.asJsonArray.size() > 0 -> {
                response.asJsonArray[0]
                    .takeIf { it.isJsonObject }
                    ?.asJsonObject
            }

            response.isJsonObject -> response.asJsonObject

            else -> null
        } ?: throw IllegalStateException(
            "Unexpected password-change response from server."
        )

        val returnStatus = responseObject
            .get("returnStatus")
            ?.takeUnless { it.isJsonNull }
            ?.asString
            ?.toIntOrNull()
            ?: throw IllegalStateException(
                "The server response did not contain returnStatus."
            )

        return when (returnStatus) {
            1 -> ChangePasswordResult(
                success = true,
                message = "Password changed successfully."
            )

            5 -> ChangePasswordResult(
                success = false,
                message = "Password must be at least 6 characters."
            )

            6 -> ChangePasswordResult(
                success = false,
                message = "Password should not exceed 16 characters."
            )

            8 -> ChangePasswordResult(
                success = false,
                message = "Password must contain at least one special character."
            )

            9 -> ChangePasswordResult(
                success = false,
                message = "Password must contain at least one digit."
            )

            0 -> ChangePasswordResult(
                success = false,
                message = "New password matches one of your previous passwords."
            )

            -1 -> ChangePasswordResult(
                success = false,
                message = "Incorrect old password."
            )

            else -> ChangePasswordResult(
                success = false,
                message = "Password could not be changed. Please try again."
            )
        }
    }
}

data class ChangePasswordResult(
    val success: Boolean,
    val message: String
)
