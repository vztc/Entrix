package com.apexbytestudio.entrix.domain.usecase

import com.apexbytestudio.entrix.core.model.Coordinates
import com.apexbytestudio.entrix.domain.location.LocationProvider

class GetCurrentLocationUseCase(
    private val locationProvider: LocationProvider,
) {
    suspend operator fun invoke(): Result<Coordinates> = locationProvider.currentLocation()
}
