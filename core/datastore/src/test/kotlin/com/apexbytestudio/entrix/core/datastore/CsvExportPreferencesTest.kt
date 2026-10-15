package com.apexbytestudio.entrix.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class CsvExportPreferencesTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun `change during a write remains pending and survives process restart`() = runTest {
        val file = File(temporary.root, "export.preferences_pb")
        var job = SupervisorJob()
        fun source(job: Job) = EntrixPreferencesDataSource(PreferenceDataStoreFactory.create(
            scope = CoroutineScope(job + Dispatchers.IO), produceFile = { file },
        ))
        val prefs = source(job)
        prefs.linkCsv("content://provider/one.csv", "Entrix-uren.csv")
        prefs.markCsvChanged()
        val snapshot = prefs.csvExportStatus.first()
        prefs.markCsvChanged() // A departure arrives while the arrival snapshot is being uploaded.
        prefs.markCsvWritten(snapshot.documentUri!!, snapshot.revision, "arrival-digest", 100L)
        assertTrue(prefs.csvExportStatus.first().isPending)
        job.cancelAndJoin()

        job = SupervisorJob()
        val restored = source(job).csvExportStatus.first()
        assertEquals("content://provider/one.csv", restored.documentUri)
        assertEquals("arrival-digest", restored.lastDigest)
        assertTrue(restored.isPending)
        job.cancelAndJoin()
    }

    @Test fun `old write cannot mark a different linked document as current`() = runTest {
        val job = SupervisorJob()
        val prefs = EntrixPreferencesDataSource(PreferenceDataStoreFactory.create(
            scope = CoroutineScope(job + Dispatchers.IO),
            produceFile = { File(temporary.root, "export.preferences_pb") },
        ))
        prefs.linkCsv("content://provider/old.csv", "old.csv")
        prefs.linkCsv("content://provider/new.csv", "new.csv")
        prefs.markCsvWritten("content://provider/old.csv", 0, "old-digest", 100)
        val state = prefs.csvExportStatus.first()
        assertEquals("content://provider/new.csv", state.documentUri)
        assertNull(state.lastDigest)
        assertTrue(state.isPending)
        prefs.unlinkCsv()
        assertFalse(prefs.csvExportStatus.first().isLinked)
        job.cancelAndJoin()
    }
}
