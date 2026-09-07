package com.aquila.pocxpertalerts.data.model

data class User(
    val id: Long = 0L,
    val fname: String? = null,
    val lname: String? = null,
    val email: String? = null,
    val alertuserid: String? = null,
    val userId: Int = 0,
    val errorMsg: String? = null,
    val sessionId: String? = null,
    val userTypeId: Int = 0,
    val applicationIds: String? = null,
    val userImportCount: Int = 0,
    val loginId: String? = null,
    val emailNotifFlag: Int = 0,
    val pwdExpiryFlag: Int = 0
)