package com.apexbytestudio.entrix.domain.usecase

import com.apexbytestudio.entrix.core.model.Office
import com.apexbytestudio.entrix.domain.fake.FakeOfficeRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveOfficeUseCaseTest {

    private val repository = FakeOfficeRepository()
    private val useCase = SaveOfficeUseCase(repository)

    private fun office(
        name: String = "HQ",
        lat: Double = 52.52,
        lng: Double = 13.405,
        radius: Float = 150f,
    ) = Office(name = name, latitude = lat, longitude = lng, radiusMeters = radius)

    @Test
    fun `valid office is persisted with a trimmed name and assigned id`() = runTest {
        val result = useCase(office(name = "  HQ  "))

        assertTrue(result.isSuccess)
        val saved = result.getOrThrow()
        assertEquals("HQ", saved.name)
        assertTrue(saved.id != Office.NO_ID)
        assertEquals(saved, repository.getOffice())
    }

    @Test
    fun `blank name is rejected`() = runTest {
        val result = useCase(office(name = "   "))

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InvalidOfficeException)
    }

    @Test
    fun `out of range coordinates are rejected`() = runTest {
        assertTrue(useCase(office(lat = 95.0)).isFailure)
        assertTrue(useCase(office(lng = 200.0)).isFailure)
    }

    @Test
    fun `radius below the minimum is rejected`() = runTest {
        val result = useCase(office(radius = SaveOfficeUseCase.MIN_RADIUS_METERS - 1f))
        assertTrue(result.isFailure)
    }

    @Test
    fun `radius above the maximum is rejected`() = runTest {
        val result = useCase(office(radius = SaveOfficeUseCase.MAX_RADIUS_METERS + 1f))
        assertTrue(result.isFailure)
    }

    @Test
    fun `nothing is persisted when validation fails`() = runTest {
        useCase(office(name = ""))
        assertFalse(repository.getOffice() != null)
    }
}
