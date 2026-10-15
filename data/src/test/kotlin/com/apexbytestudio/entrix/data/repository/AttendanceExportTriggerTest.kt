package com.apexbytestudio.entrix.data.repository

import com.apexbytestudio.entrix.core.database.dao.AttendanceDao
import com.apexbytestudio.entrix.data.export.CsvExportScheduler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class AttendanceExportTriggerTest {
    private val dao = mockk<AttendanceDao>(relaxed = true)
    private val scheduler = mockk<CsvExportScheduler>(relaxed = true)
    private val repository = AttendanceRepositoryImpl(dao, scheduler)

    @Test fun `arrival departure and deletion each schedule a snapshot after committing`() = runTest {
        val entered = Instant.parse("2026-10-16T06:00:00Z")
        val exited = Instant.parse("2026-10-16T14:00:00Z")
        coEvery { dao.insert(any()) } returns 42L
        assertEquals(42L, repository.openVisit(1L, entered, entered))
        repository.closeVisit(42L, exited)
        repository.deleteAll()
        coVerify(exactly = 3) { scheduler.dataChanged() }
        coVerify { dao.closeVisit(42L, exited.toEpochMilli()) }
    }

    @Test fun `failed database insert does not export an uncommitted visit`() = runTest {
        coEvery { dao.insert(any()) } throws IllegalStateException("database failure")
        runCatching { repository.openVisit(1L, Instant.EPOCH, Instant.EPOCH) }
        coVerify(exactly = 0) { scheduler.dataChanged() }
    }
}
