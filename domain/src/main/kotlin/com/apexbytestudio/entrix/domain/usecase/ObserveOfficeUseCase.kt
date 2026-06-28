package com.apexbytestudio.entrix.domain.usecase

import com.apexbytestudio.entrix.core.model.Office
import com.apexbytestudio.entrix.domain.repository.OfficeRepository
import kotlinx.coroutines.flow.Flow

class ObserveOfficeUseCase(
    private val officeRepository: OfficeRepository,
) {
    operator fun invoke(): Flow<Office?> = officeRepository.observeOffice()
}
