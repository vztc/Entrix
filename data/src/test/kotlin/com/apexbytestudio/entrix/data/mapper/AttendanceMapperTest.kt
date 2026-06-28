package com.apexbytestudio.entrix.data.mapper

import com.apexbytestudio.entrix.core.model.Attendance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class AttendanceMapperTest {

    @Test
    fun `open visit round-trips with a null exit time`() {
        val model = Attendance(
            id = 7,
            officeId = 3,
            enterTime = Instant.parse("2026-06-28T08:00:00Z"),
            exitTime = null,
            createdTime = Instant.parse("2026-06-28T08:00:00Z"),
        )

        val restored = model.toEntity().toModel()

        assertEquals(model, restored)
        assertNull(restored.exitTime)
    }

    @Test
    fun `closed visit preserves millisecond timestamps`() {
        val model = Attendance(
            id = 1,
            officeId = 2,
            enterTime = Instant.ofEpochMilli(1_700_000_000_123L),
            exitTime = Instant.ofEpochMilli(1_700_000_900_456L),
            createdTime = Instant.ofEpochMilli(1_700_000_000_123L),
        )

        val entity = model.toEntity()
        assertEquals(1_700_000_000_123L, entity.enterTime)
        assertEquals(1_700_000_900_456L, entity.exitTime)
        assertEquals(model, entity.toModel())
    }
}
