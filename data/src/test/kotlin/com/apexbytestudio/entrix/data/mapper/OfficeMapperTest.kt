package com.apexbytestudio.entrix.data.mapper

import com.apexbytestudio.entrix.core.model.Office
import org.junit.Assert.assertEquals
import org.junit.Test

class OfficeMapperTest {

    @Test
    fun `office round-trips through its entity`() {
        val model = Office(
            id = 5,
            name = "HQ",
            latitude = 52.52,
            longitude = 13.405,
            radiusMeters = 150f,
        )

        assertEquals(model, model.toEntity().toModel())
    }
}
