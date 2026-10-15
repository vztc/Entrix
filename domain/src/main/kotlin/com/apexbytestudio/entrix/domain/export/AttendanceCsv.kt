package com.apexbytestudio.entrix.domain.export

import com.apexbytestudio.entrix.core.model.Attendance
import com.apexbytestudio.entrix.core.model.Office
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Complete snapshots avoid duplicate rows when a background update is retried. */
object AttendanceCsv {
    const val FILE_NAME = "Entrix-uren.csv"
    const val HEADER = "Registratie_ID;Werklocatie;Aankomst;Vertrek;Duur;Uren;Status;Tijdzone"
    private val zone = ZoneId.of("Europe/Amsterdam")
    private val dateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(zone)

    fun render(office: Office?, visits: List<Attendance>): String = buildString {
        append('\uFEFF') // UTF-8 BOM for Excel.
        append(HEADER).append("\r\n")
        visits.sortedWith(compareBy<Attendance> { it.enterTime }.thenBy { it.id }).forEach { visit ->
            val duration = visit.duration?.coerceAtLeast(Duration.ZERO)
            val seconds = duration?.seconds
            val values = listOf(
                visit.id.toString(),
                office?.takeIf { it.id == visit.officeId }?.name.orEmpty(),
                dateTime.format(visit.enterTime),
                visit.exitTime?.let(dateTime::format).orEmpty(),
                seconds?.let { "%d:%02d:%02d".format(Locale.US, it / 3600, it / 60 % 60, it % 60) }.orEmpty(),
                seconds?.let { "%.4f".format(Locale.US, it / 3600.0).replace('.', ',') }.orEmpty(),
                if (visit.isOpen) "Bezig" else "Afgerond",
                zone.id,
            )
            append(values.joinToString(";", transform = ::escapeCell)).append("\r\n")
        }
    }

    private fun escapeCell(value: String): String {
        // Quoting alone does not stop spreadsheet formula evaluation.
        val safe = if (value.trimStart().firstOrNull() in listOf('=', '+', '-', '@') ||
            value.startsWith('\t') || value.startsWith('\r')
        ) "'$value" else value
        return if (safe.any { it in ";\"\r\n" }) "\"${safe.replace("\"", "\"\"")}\"" else safe
    }
}
