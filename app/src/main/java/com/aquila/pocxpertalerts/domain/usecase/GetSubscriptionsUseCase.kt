package com.aquila.pocxpertalerts.domain.usecase

import com.aquila.pocxpertalerts.data.model.SubscriptionDto
import com.aquila.pocxpertalerts.data.repository.SubscriptionRepository

class GetSubscriptionsUseCase(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(
        userId: String,
        sessionId: String,
        applicationId: String = "0",
        searchName: String = "",
        searchActive: Int = 1
    ): Result<List<SubscriptionDto>> {
        return repository.getSubscriptions(
            userId = userId,
            sessionId = sessionId,
            applicationId = applicationId,
            searchName = searchName,
            searchActive = searchActive
        )
    }
}