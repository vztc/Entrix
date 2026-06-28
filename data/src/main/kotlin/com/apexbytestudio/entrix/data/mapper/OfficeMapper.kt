package com.apexbytestudio.entrix.data.mapper

import com.apexbytestudio.entrix.core.database.entity.OfficeEntity
import com.apexbytestudio.entrix.core.model.Office

fun OfficeEntity.toModel(): Office = Office(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    radiusMeters = radiusMeters,
)

fun Office.toEntity(): OfficeEntity = OfficeEntity(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    radiusMeters = radiusMeters,
)
