package com.apexbytestudio.entrix.data.repository

import com.apexbytestudio.entrix.core.datastore.EntrixPreferencesDataSource
import com.apexbytestudio.entrix.domain.repository.TrackingStateRepository
import kotlinx.coroutines.flow.Flow
import java.time.Instant

class TrackingStateRepositoryImpl(
    private val preferences: EntrixPreferencesDataSource,
) : TrackingStateRepository {

    override val trackingEnabled: Flow<Boolean> = preferences.trackingEnabled

    override suspend fun isTrackingEnabled(): Boolean = preferences.isTrackingEnabled()

    override suspend fun setTrackingEnabled(enabled: Boolean) =
        preferences.setTrackingEnabled(enabled)

    override suspend fun getPendingEnterTime(): Instant? =
        preferences.getPendingEnterMillis()?.let(Instant::ofEpochMilli)

    override suspend fun setPendingEnterTime(time: Instant?) =
        preferences.setPendingEnterMillis(time?.toEpochMilli())
}
