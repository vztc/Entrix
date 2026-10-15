package com.apexbytestudio.entrix.domain.repository

import com.apexbytestudio.entrix.core.model.CsvExportStatus
import kotlinx.coroutines.flow.Flow

interface AutomaticCsvExportRepository {
    fun observeStatus(): Flow<CsvExportStatus>
    suspend fun linkDocument(uri: String): Result<Unit>
    suspend fun disconnect()
}
