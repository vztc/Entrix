package com.apexbytestudio.entrix.domain.fake

import com.apexbytestudio.entrix.core.model.Office
import com.apexbytestudio.entrix.domain.geofence.GeofenceScheduler

class FakeGeofenceScheduler(
    private val registerResult: Result<Unit> = Result.success(Unit),
) : GeofenceScheduler {
    var registeredOffice: Office? = null
        private set
    var clearCount = 0
        private set
    var registerCount = 0
        private set

    override suspend fun register(office: Office): Result<Unit> {
        registerCount++
        registeredOffice = office
        return registerResult
    }

    override suspend fun clear(): Result<Unit> {
        clearCount++
        registeredOffice = null
        return Result.success(Unit)
    }
}
