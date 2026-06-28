package com.apexbytestudio.entrix.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.apexbytestudio.entrix.core.navigation.EntrixRoute

/** The three bottom-bar tabs. Office Setup is intentionally not here — it is a pushed screen. */
enum class TopLevelDestination(
    val route: EntrixRoute,
    val label: String,
    val icon: ImageVector,
) {
    DASHBOARD(EntrixRoute.Dashboard, "Home", Icons.Default.Home),
    HISTORY(EntrixRoute.Attendance, "History", Icons.AutoMirrored.Filled.List),
    SETTINGS(EntrixRoute.Settings, "Settings", Icons.Default.Settings),
}
