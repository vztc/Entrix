package com.apexbytestudio.entrix.core.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes. Declared as serializable objects so Navigation Compose can use them
 * directly without string routes. New arguments become constructor parameters on these types.
 */
sealed interface EntrixRoute {

    @Serializable
    data object Dashboard : EntrixRoute

    @Serializable
    data object Attendance : EntrixRoute

    @Serializable
    data object Settings : EntrixRoute

    @Serializable
    data object Office : EntrixRoute
}
