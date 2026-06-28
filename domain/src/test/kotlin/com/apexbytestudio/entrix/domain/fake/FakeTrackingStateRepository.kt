package com.apexbytestudio.entrix.domain.fake

import com.apexbytestudio.entrix.domain.repository.TrackingStateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant

class FakeTrackingStateRepository(
    enabled: Boolean = true,
) : TrackingStateRepository {
    private val enabledFlow = MutableStateFlow(enabled)
    private var pending: Instant? = null

    override val trackingEnabled: Flow<Boolean> = enabledFlow

    override suspend fun isTrackingEnabled(): Boolean = enabledFlow.value

    override suspend fun setTrackingEnabled(enabled: Boolean) {
        enabledFlow.value = enabled
    }

    override suspend fun getPendingEnterTime(): Instant? = pending

    override suspend fun setPendingEnterTime(time: Instant?) {
        pending = time
    }
}
