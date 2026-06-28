package com.apexbytestudio.entrix.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.apexbytestudio.entrix.core.database.dao.AttendanceDao
import com.apexbytestudio.entrix.core.database.dao.OfficeDao
import com.apexbytestudio.entrix.core.database.entity.AttendanceEntity
import com.apexbytestudio.entrix.core.database.entity.OfficeEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AttendanceDaoTest {

    private lateinit var db: EntrixDatabase
    private lateinit var officeDao: OfficeDao
    private lateinit var attendanceDao: AttendanceDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            EntrixDatabase::class.java,
        ).build()
        officeDao = db.officeDao()
        attendanceDao = db.attendanceDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun openVisitIsObservedThenClosed() = runTest {
        val officeId = officeDao.upsert(office())

        val id = attendanceDao.insert(
            AttendanceEntity(officeId = officeId, enterTime = 1_000, exitTime = null, createdTime = 1_000),
        )
        assertEquals(id, attendanceDao.observeOpenVisit().first()?.id)

        attendanceDao.closeVisit(id, exitTime = 2_000)
        assertNull(attendanceDao.observeOpenVisit().first())
        assertEquals(2_000L, attendanceDao.observeHistory().first().single().exitTime)
    }

    @Test
    fun deletingOfficeCascadesToAttendance() = runTest {
        val officeId = officeDao.upsert(office())
        attendanceDao.insert(
            AttendanceEntity(officeId = officeId, enterTime = 1_000, exitTime = null, createdTime = 1_000),
        )

        officeDao.deleteAll()

        assertEquals(0, attendanceDao.observeHistory().first().size)
    }

    private fun office() = OfficeEntity(
        name = "HQ",
        latitude = 52.52,
        longitude = 13.405,
        radiusMeters = 150f,
    )
}
