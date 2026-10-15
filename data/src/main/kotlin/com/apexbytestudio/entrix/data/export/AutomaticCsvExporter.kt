package com.apexbytestudio.entrix.data.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.room.withTransaction
import com.apexbytestudio.entrix.core.database.EntrixDatabase
import com.apexbytestudio.entrix.core.datastore.EntrixPreferencesDataSource
import com.apexbytestudio.entrix.core.model.CsvExportStatus
import com.apexbytestudio.entrix.data.mapper.toModel
import com.apexbytestudio.entrix.domain.export.AttendanceCsv
import com.apexbytestudio.entrix.domain.repository.AutomaticCsvExportRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException
import java.security.MessageDigest

class AutomaticCsvExporter(
    private val context: Context,
    private val database: EntrixDatabase,
    private val preferences: EntrixPreferencesDataSource,
    private val scheduler: CsvExportScheduler,
) : AutomaticCsvExportRepository {
    private val mutex = Mutex()
    private val resolver get() = context.contentResolver
    private val grantFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

    override fun observeStatus(): Flow<CsvExportStatus> = preferences.csvExportStatus

    override suspend fun linkDocument(uri: String): Result<Unit> = withContext(Dispatchers.IO) {
        mutex.withLock {
            val target = Uri.parse(uri)
            val previous = preferences.csvExportStatus.first().documentUri
            try {
                require(target.scheme == "content" && DocumentsContract.isDocumentUri(context, target)) {
                    "Choose a document using the system file picker."
                }
                resolver.takePersistableUriPermission(target, grantFlags)
                if (resolver.persistedUriPermissions.none { it.uri == target && it.isReadPermission && it.isWritePermission }) {
                    throw SecurityException("This provider did not grant permanent read/write access. For OneDrive, use RSAF.")
                }
                val name = validateTarget(target)
                preferences.linkCsv(uri, name)
                scheduler.enqueue()
                if (previous != null && previous != uri) releaseGrant(Uri.parse(previous))
                Result.success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (previous != uri) releaseGrant(target)
                Result.failure(e)
            }
        }
    }

    override suspend fun disconnect() = withContext(Dispatchers.IO) {
        mutex.withLock {
            val previous = preferences.csvExportStatus.first().documentUri
            preferences.unlinkCsv()
            scheduler.cancel()
            previous?.let { releaseGrant(Uri.parse(it)) }
            Unit
        }
    }

    suspend fun resumeAutomaticExport() {
        val status = preferences.csvExportStatus.first()
        if (status.isLinked) {
            scheduler.ensurePeriodic()
            if (status.isPending) scheduler.enqueue()
        }
    }

    suspend fun sync() = withContext(Dispatchers.IO) {
        mutex.withLock {
            val status = preferences.csvExportStatus.first()
            val targetUri = status.documentUri ?: return@withLock
            val uri = Uri.parse(targetUri)
            // Check identity and header on every write: some cloud providers can revoke/reassign URIs.
            validateTarget(uri, status.fileName)
            val csv = database.withTransaction {
                AttendanceCsv.render(
                    database.officeDao().getOffice()?.toModel(),
                    database.attendanceDao().getHistory().map { it.toModel() },
                )
            }.toByteArray(Charsets.UTF_8)
            val digest = MessageDigest.getInstance("SHA-256").digest(csv)
                .joinToString("") { "%02x".format(it.toInt() and 0xff) }
            var writtenAt: Long? = null
            if (digest != status.lastDigest) {
                // Explicit truncation prevents trailing old rows after a history deletion.
                val stream = resolver.openOutputStream(uri, "wt")
                    ?: throw IOException("The storage provider could not open the CSV for writing.")
                stream.use { it.write(csv); it.flush() }
                // This means handed to the provider; its cloud upload may finish later.
                writtenAt = System.currentTimeMillis()
            }
            preferences.markCsvWritten(targetUri, status.revision, digest, writtenAt)
        }
    }

    suspend fun reportFailure(error: Exception) {
        preferences.setCsvError(
            when (error) {
                is SecurityException -> "File access was lost. Link the CSV again; for OneDrive, use RSAF."
                is IllegalArgumentException -> error.message ?: "Choose the Entrix CSV again."
                else -> "File update failed. Entrix will retry automatically. Check your cloud provider."
            },
        )
    }

    private fun validateTarget(uri: Uri, expectedName: String? = null): String {
        val name = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
            if (!it.moveToFirst()) throw IOException("The linked CSV could not be found.")
            it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        } ?: throw IOException("The storage provider is unavailable.")
        require(name.endsWith(".csv", ignoreCase = true)) { "Choose an Entrix CSV file (.csv)." }
        require(expectedName == null || name == expectedName) { "The linked file changed. Select the Entrix CSV again." }
        val header = resolver.openInputStream(uri)?.use { stream ->
            // Read only the first line; do not load a large cloud file to inspect its format.
            stream.bufferedReader(Charsets.UTF_8).use { reader ->
                val line = StringBuilder()
                while (line.length <= AttendanceCsv.HEADER.length + 2) {
                    val next = reader.read()
                    if (next == -1 || next == '\n'.code || next == '\r'.code) break
                    line.append(next.toChar())
                }
                line.toString().removePrefix("\uFEFF")
            }
        } ?: throw IOException("The linked CSV cannot be read.")
        require(header.isEmpty() || header == AttendanceCsv.HEADER ||
            (expectedName != null && AttendanceCsv.HEADER.startsWith(header))
        ) {
            "This is not an Entrix export. Create a new CSV to avoid overwriting another file."
        }
        return name
    }

    private fun releaseGrant(uri: Uri) {
        runCatching { resolver.releasePersistableUriPermission(uri, grantFlags) }
    }
}
