package com.apexbytestudio.entrix.domain.repository

import com.apexbytestudio.entrix.core.model.Office
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for the configured office. The MVP persists at most one office;
 * [observeOffice] therefore emits the current office or `null` when none is configured.
 */
interface OfficeRepository {
    fun observeOffice(): Flow<Office?>

    suspend fun getOffice(): Office?

    /** Inserts or updates the office and returns it with its persisted id. */
    suspend fun saveOffice(office: Office): Office

    suspend fun deleteOffice()
}
