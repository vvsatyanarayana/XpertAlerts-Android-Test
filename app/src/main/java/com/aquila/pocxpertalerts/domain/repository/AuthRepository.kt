package com.aquila.pocxpertalerts.domain.repository

import com.aquila.pocxpertalerts.domain.model.User

interface AuthRepository {

    suspend fun login(
        username: String,
        password: String,
        deviceId: String
    ): Result<User>
}