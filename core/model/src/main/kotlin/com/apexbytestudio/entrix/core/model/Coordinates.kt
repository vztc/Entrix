package com.apexbytestudio.entrix.core.model

/** A simple latitude/longitude pair, decoupled from any platform Location type. */
data class Coordinates(
    val latitude: Double,
    val longitude: Double,
)
