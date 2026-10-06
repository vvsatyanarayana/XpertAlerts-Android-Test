package com.aquila.pocxpertalerts.domain.usecase

import com.aquila.pocxpertalerts.domain.model.User
import com.aquila.pocxpertalerts.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        username: String,
        password: String,
        deviceId: String
    ): Result<User> {

        return authRepository.login(
            username = username,
            password = password,
            deviceId = deviceId
        )
    }
}