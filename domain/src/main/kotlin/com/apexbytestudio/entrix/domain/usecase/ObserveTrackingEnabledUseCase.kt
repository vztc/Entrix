package com.apexbytestudio.entrix.domain.usecase

import com.apexbytestudio.entrix.domain.repository.TrackingStateRepository
import kotlinx.coroutines.flow.Flow

class ObserveTrackingEnabledUseCase(
    private val trackingStateRepository: TrackingStateRepository,
) {
    operator fun invoke(): Flow<Boolean> = trackingStateRepository.trackingEnabled
}
