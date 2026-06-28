package com.apexbytestudio.entrix.domain.usecase

import app.cash.turbine.test
import com.apexbytestudio.entrix.core.model.Attendance
import com.apexbytestudio.entrix.core.model.Office
import com.apexbytestudio.entrix.domain.fake.FakeAttendanceRepository
import com.apexbytestudio.entrix.domain.fake.FakeOfficeRepository
import com.apexbytestudio.entrix.domain.fake.FakeTrackingStateRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class ObserveDashboardUseCaseTest {

    private val zone = ZoneId.of("UTC")
    private val clock = Clock.fixed(Instant.parse("2026-06-28T12:00:00Z"), zone)

    private val officeRepo = FakeOfficeRepository()
    private val attendanceRepo = FakeAttendanceRepository()
    private val trackingRepo = FakeTrackingStateRepository(enabled = true)

    private val useCase = ObserveDashboardUseCase(officeRepo, attendanceRepo, trackingRepo, clock)

    @Test
    fun `snapshot keeps only today's visits and reflects an open visit`() = runTest {
        officeRepo.saveOffice(
            Office(name = "HQ", latitude = 52.52, longitude = 13.405, radiusMeters = 150f),
        )
        attendanceRepo.seed(
            Attendance(
                id = 1, officeId = 1,
                enterTime = Instant.parse("2026-06-28T11:00:00Z"),
                exitTime = null,
                createdTime = Instant.parse("2026-06-28T11:00:00Z"),
            ),
            Attendance(
                id = 2, officeId = 1,
                enterTime = Instant.parse("2026-06-27T09:00:00Z"),
                exitTime = Instant.parse("2026-06-27T17:00:00Z"),
                createdTime = Instant.parse("2026-06-27T09:00:00Z"),
            ),
        )

        useCase().test {
            val snapshot = awaitItem()

            assertTrue(snapshot.isOfficeConfigured)
            assertTrue(snapshot.isInsideOffice)
            assertEquals(1, snapshot.todayVisits.size)
            assertEquals(Instant.parse("2026-06-28T11:00:00Z"), snapshot.todayFirstEnter)
            assertTrue(snapshot.trackingEnabled)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
