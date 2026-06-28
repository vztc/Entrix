package com.apexbytestudio.entrix.domain.usecase

import com.apexbytestudio.entrix.core.model.Attendance
import com.apexbytestudio.entrix.core.model.GeofenceTransition
import com.apexbytestudio.entrix.domain.fake.FakeAttendanceRepository
import com.apexbytestudio.entrix.domain.fake.FakeTrackingStateRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class RecordGeofenceTransitionUseCaseTest {

    private val officeId = 42L
    private val attendance = FakeAttendanceRepository()
    private val tracking = FakeTrackingStateRepository(enabled = true)
    private val useCase = RecordGeofenceTransitionUseCase(attendance, tracking)

    private val t0 = Instant.parse("2026-06-28T08:00:00Z") // ENTER
    private val t1 = Instant.parse("2026-06-28T08:02:00Z") // DWELL
    private val t2 = Instant.parse("2026-06-28T17:00:00Z") // EXIT

    @Test
    fun `enter caches pending time without opening a visit`() = runTest {
        val outcome = useCase(officeId, GeofenceTransition.Enter, t0)

        assertEquals(TransitionOutcome.EntryCached, outcome)
        assertEquals(t0, tracking.getPendingEnterTime())
        assertTrue(attendance.current().isEmpty())
    }

    @Test
    fun `dwell opens a visit back-dated to the cached enter time`() = runTest {
        useCase(officeId, GeofenceTransition.Enter, t0)

        val outcome = useCase(officeId, GeofenceTransition.Dwell, t1)

        assertEquals(TransitionOutcome.VisitOpened, outcome)
        val open = attendance.getOpenVisit(officeId)!!
        assertEquals(t0, open.enterTime) // back-dated, not t1
        assertEquals(t1, open.createdTime)
        assertNull(tracking.getPendingEnterTime()) // cache cleared
    }

    @Test
    fun `dwell without a cached enter falls back to the dwell time`() = runTest {
        val outcome = useCase(officeId, GeofenceTransition.Dwell, t1)

        assertEquals(TransitionOutcome.VisitOpened, outcome)
        assertEquals(t1, attendance.getOpenVisit(officeId)!!.enterTime)
    }

    @Test
    fun `exit closes the open visit and clears any pending enter`() = runTest {
        useCase(officeId, GeofenceTransition.Enter, t0)
        useCase(officeId, GeofenceTransition.Dwell, t1)

        val outcome = useCase(officeId, GeofenceTransition.Exit, t2)

        assertEquals(TransitionOutcome.VisitClosed, outcome)
        assertNull(attendance.getOpenVisit(officeId))
        val record = attendance.current().single()
        assertEquals(t0, record.enterTime)
        assertEquals(t2, record.exitTime)
    }

    @Test
    fun `drive-by enter then exit creates no record`() = runTest {
        useCase(officeId, GeofenceTransition.Enter, t0)
        val outcome = useCase(officeId, GeofenceTransition.Exit, t1)

        assertEquals(TransitionOutcome.Ignored, outcome)
        assertTrue(attendance.current().isEmpty())
        assertNull(tracking.getPendingEnterTime())
    }

    @Test
    fun `duplicate dwell does not open a second visit`() = runTest {
        useCase(officeId, GeofenceTransition.Dwell, t1)
        val outcome = useCase(officeId, GeofenceTransition.Dwell, t1.plusSeconds(30))

        assertEquals(TransitionOutcome.Ignored, outcome)
        assertEquals(1, attendance.current().size)
    }

    @Test
    fun `second enter while one is pending keeps the earliest time`() = runTest {
        useCase(officeId, GeofenceTransition.Enter, t0)
        val outcome = useCase(officeId, GeofenceTransition.Enter, t0.plusSeconds(60))

        assertEquals(TransitionOutcome.Ignored, outcome)
        assertEquals(t0, tracking.getPendingEnterTime())
    }

    @Test
    fun `enter while already inside is ignored`() = runTest {
        attendance.seed(
            Attendance(id = 1, officeId = officeId, enterTime = t0, exitTime = null, createdTime = t0),
        )
        val outcome = useCase(officeId, GeofenceTransition.Enter, t1)

        assertEquals(TransitionOutcome.Ignored, outcome)
        assertNull(tracking.getPendingEnterTime())
    }

    @Test
    fun `exit without an open visit is ignored`() = runTest {
        val outcome = useCase(officeId, GeofenceTransition.Exit, t2)

        assertEquals(TransitionOutcome.Ignored, outcome)
        assertTrue(attendance.current().isEmpty())
    }

    @Test
    fun `transitions are ignored when tracking is disabled`() = runTest {
        tracking.setTrackingEnabled(false)

        assertEquals(TransitionOutcome.Ignored, useCase(officeId, GeofenceTransition.Enter, t0))
        assertEquals(TransitionOutcome.Ignored, useCase(officeId, GeofenceTransition.Dwell, t1))
        assertTrue(attendance.current().isEmpty())
        assertNull(tracking.getPendingEnterTime())
    }
}
