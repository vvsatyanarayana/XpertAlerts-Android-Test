package com.aquila.pocxpertalerts.data.model

import com.aquila.pocxpertalerts.domain.model.User

data class UserDto(
    val id: Int? = null,
    val fname: String? = null,
    val lname: String? = null,
    val email: String? = null,
    val alertuserid: String? = null,
    val userId: Int? = null,
    val errorMsg: String? = null,
    val sessionId: String? = null,
    val userTypeId: Int? = null,
    val applicationIds: String? = null,
    val userImportCount: Int? = null,
    val loginId: String? = null,
    val emailNotifFlag: Int? = null,
    val pwdExpiryFlag: Int? = null
)

fun UserDto.toDomain(): User {
    return User(
        id = id ?: 0,
        fname = fname,
        lname = lname,
        email = email,
        alertuserid = alertuserid,
        userId = userId ?: 0,
        errorMsg = errorMsg,
        sessionId = sessionId,
        userTypeId = userTypeId ?: 0,
        applicationIds = applicationIds,
        userImportCount = userImportCount ?: 0,
        loginId = loginId,
        emailNotifFlag = emailNotifFlag ?: 0,
        pwdExpiryFlag = pwdExpiryFlag ?: 0
    )
}