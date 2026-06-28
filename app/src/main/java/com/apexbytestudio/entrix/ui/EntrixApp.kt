package com.apexbytestudio.entrix.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.apexbytestudio.entrix.core.navigation.EntrixRoute
import com.apexbytestudio.entrix.feature.attendance.AttendanceScreen
import com.apexbytestudio.entrix.feature.dashboard.DashboardScreen
import com.apexbytestudio.entrix.feature.dashboard.DashboardViewModel
import com.apexbytestudio.entrix.feature.office.OfficeScreen
import com.apexbytestudio.entrix.feature.settings.SettingsScreen
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrixApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { CenterAlignedTopAppBar(title = { Text("Entrix") }) },
        bottomBar = {
            NavigationBar {
                TopLevelDestination.entries.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.hasRoute(destination.route::class)
                    } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = EntrixRoute.Dashboard,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable<EntrixRoute.Dashboard> {
                val viewModel: DashboardViewModel = koinViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                DashboardScreen(
                    uiState = uiState,
                    onEditOffice = { navController.navigate(EntrixRoute.Office) },
                )
            }
            composable<EntrixRoute.Attendance> {
                AttendanceScreen()
            }
            composable<EntrixRoute.Settings> {
                SettingsScreen(
                    onEditOffice = { navController.navigate(EntrixRoute.Office) },
                )
            }
            composable<EntrixRoute.Office> {
                OfficeScreen(onSaved = { navController.popBackStack() })
            }
        }
    }
}
