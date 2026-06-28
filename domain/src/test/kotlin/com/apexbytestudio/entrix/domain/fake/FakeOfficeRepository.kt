package com.apexbytestudio.entrix.domain.fake

import com.apexbytestudio.entrix.core.model.Office
import com.apexbytestudio.entrix.domain.repository.OfficeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeOfficeRepository : OfficeRepository {
    private val office = MutableStateFlow<Office?>(null)
    private var nextId = 1L

    override fun observeOffice(): Flow<Office?> = office.asStateFlow()

    override suspend fun getOffice(): Office? = office.value

    override suspend fun saveOffice(office: Office): Office {
        val persisted = if (office.id == Office.NO_ID) office.copy(id = nextId++) else office
        this.office.value = persisted
        return persisted
    }

    override suspend fun deleteOffice() {
        office.value = null
    }
}
