package com.apexbytestudio.entrix.domain.usecase

import com.apexbytestudio.entrix.core.model.Office
import com.apexbytestudio.entrix.domain.fake.FakeGeofenceScheduler
import com.apexbytestudio.entrix.domain.fake.FakeOfficeRepository
import com.apexbytestudio.entrix.domain.fake.FakeTrackingStateRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SyncGeofencesUseCaseTest {

    private val officeRepo = FakeOfficeRepository()
    private val trackingRepo = FakeTrackingStateRepository(enabled = false)
    private val scheduler = FakeGeofenceScheduler()
    private val sync = SyncGeofencesUseCase(officeRepo, trackingRepo, scheduler)

    private suspend fun configureOffice() = officeRepo.saveOffice(
        Office(name = "HQ", latitude = 52.52, longitude = 13.405, radiusMeters = 150f),
    )

    @Test
    fun `registers when tracking is enabled and an office exists`() = runTest {
        val office = configureOffice()
        trackingRepo.setTrackingEnabled(true)

        val result = sync()

        assertEquals(Result.success(Unit), result)
        assertEquals(office, scheduler.registeredOffice)
        assertEquals(1, scheduler.registerCount)
    }

    @Test
    fun `clears when tracking is disabled`() = runTest {
        configureOffice()
        trackingRepo.setTrackingEnabled(false)

        sync()

        assertNull(scheduler.registeredOffice)
        assertEquals(1, scheduler.clearCount)
        assertEquals(0, scheduler.registerCount)
    }

    @Test
    fun `clears when enabled but no office is configured`() = runTest {
        trackingRepo.setTrackingEnabled(true)

        sync()

        assertEquals(0, scheduler.registerCount)
        assertEquals(1, scheduler.clearCount)
    }

    @Test
    fun `set tracking enabled persists the flag and triggers registration`() = runTest {
        val office = configureOffice()
        val setTracking = SetTrackingEnabledUseCase(trackingRepo, sync)

        val result = setTracking(true)

        assertEquals(Result.success(Unit), result)
        assertEquals(true, trackingRepo.isTrackingEnabled())
        assertEquals(office, scheduler.registeredOffice)
    }
}
