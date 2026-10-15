package com.apexbytestudio.entrix.domain.export

import com.apexbytestudio.entrix.core.model.Attendance
import com.apexbytestudio.entrix.core.model.Office
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class AttendanceCsvTest {
    private val office = Office(1L, "Office", 52.0, 5.0, 150f)
    private fun visit(id: Long, enter: String, exit: String?) = Attendance(
        id, 1L, Instant.parse(enter), exit?.let(Instant::parse), Instant.parse(enter),
    )

    @Test fun `empty export contains a valid Excel header`() {
        assertEquals("\uFEFF${AttendanceCsv.HEADER}\r\n", AttendanceCsv.render(office, emptyList()))
    }

    @Test fun `export uses Dutch Excel numbers and Amsterdam time`() {
        val csv = AttendanceCsv.render(office, listOf(visit(2, "2026-10-16T06:00:00Z", "2026-10-16T14:30:00Z")))
        assertTrue(csv.contains("2;Office;2026-10-16 08:00:00;2026-10-16 16:30:00;8:30:00;8,5000;Afgerond;Europe/Amsterdam\r\n"))
    }

    @Test fun `duration remains correct across daylight saving and midnight`() {
        val csv = AttendanceCsv.render(office, listOf(visit(1, "2026-10-24T22:00:00Z", "2026-10-25T03:00:00Z")))
        assertTrue(csv.contains("2026-10-25 00:00:00;2026-10-25 04:00:00;5:00:00;5,0000"))
    }

    @Test fun `open visits have no invented end time or final duration`() {
        val csv = AttendanceCsv.render(office, listOf(visit(1, "2026-10-16T06:00:00Z", null)))
        assertTrue(csv.contains("2026-10-16 08:00:00;;;;Bezig;Europe/Amsterdam"))
    }

    @Test fun `names are escaped and spreadsheet formulas neutralized`() {
        val csv = AttendanceCsv.render(office.copy(name = "=SUM(1;2) \"test\""), listOf(visit(1, "2026-10-16T06:00:00Z", null)))
        assertTrue(csv.contains("\"'=SUM(1;2) \"\"test\"\"\""))
    }

    @Test fun `snapshots have stable oldest first ordering without duplicate rows`() {
        val a = visit(1, "2026-10-16T06:00:00Z", null)
        val b = visit(2, "2026-10-17T06:00:00Z", null)
        assertEquals(AttendanceCsv.render(office, listOf(a, b)), AttendanceCsv.render(office, listOf(b, a)))
    }
}
