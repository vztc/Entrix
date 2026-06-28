package com.apexbytestudio.entrix.feature.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.Lifecycle
import com.apexbytestudio.entrix.core.designsystem.component.EntrixCard
import com.apexbytestudio.entrix.core.designsystem.theme.EntrixSpacing
import org.koin.androidx.compose.koinViewModel

private const val GITHUB_URL = "https://github.com/Nahian98/Entrix"

@Composable
fun SettingsScreen(
    onEditOffice: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val trackingEnabled by viewModel.trackingEnabled.collectAsStateWithLifecycle()

    // Bumped on resume and after permission results to recompute system-owned statuses.
    var refreshKey by remember { mutableIntStateOf(0) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { refreshKey++ }

    val fineGranted = remember(refreshKey) {
        context.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    val backgroundGranted = remember(refreshKey) {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
            context.isGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }
    val notificationsGranted = remember(refreshKey) {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.isGranted(Manifest.permission.POST_NOTIFICATIONS)
    }
    val batteryUnrestricted = remember(refreshKey) { context.isIgnoringBatteryOptimizations() }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { refreshKey++ }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(EntrixSpacing.md),
        verticalArrangement = Arrangement.spacedBy(EntrixSpacing.md),
    ) {
        EntrixCard {
            SettingRow(
                title = "Automatic tracking",
                subtitle = if (trackingEnabled) "On" else "Off",
                trailing = {
                    Switch(checked = trackingEnabled, onCheckedChange = viewModel::setTracking)
                },
            )
            HorizontalDivider()
            SettingRow(
                title = "Office location",
                subtitle = "Edit the tracked office",
                trailing = { TextButton(onClick = onEditOffice) { Text("Edit") } },
            )
        }

        EntrixCard {
            Text(
                "Permissions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = EntrixSpacing.sm),
            )
            PermissionRow(
                title = "Location",
                granted = fineGranted,
                onGrant = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                HorizontalDivider()
                PermissionRow(
                    title = "Background location",
                    granted = backgroundGranted,
                    onGrant = { permissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION) },
                )
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                HorizontalDivider()
                PermissionRow(
                    title = "Notifications",
                    granted = notificationsGranted,
                    onGrant = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                )
            }
            HorizontalDivider()
            SettingRow(
                title = "Battery optimization",
                subtitle = if (batteryUnrestricted) "Unrestricted" else "Restricted — may delay tracking",
                trailing = {
                    if (!batteryUnrestricted) {
                        TextButton(onClick = { context.openBatterySettings() }) { Text("Fix") }
                    }
                },
            )
        }

        EntrixCard {
            Text(
                "About",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = EntrixSpacing.sm),
            )
            SettingRow(title = "Version", subtitle = context.appVersion(), trailing = {})
            HorizontalDivider()
            SettingRow(
                title = "Source code",
                subtitle = "View on GitHub",
                trailing = {
                    TextButton(onClick = { context.openUrl(GITHUB_URL) }) { Text("Open") }
                },
            )
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = EntrixSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        trailing()
    }
}

@Composable
private fun PermissionRow(title: String, granted: Boolean, onGrant: () -> Unit) {
    SettingRow(
        title = title,
        subtitle = if (granted) "Granted" else "Not granted",
        trailing = {
            if (!granted) TextButton(onClick = onGrant) { Text("Grant") }
        },
    )
}

private fun Context.isGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

private fun Context.isIgnoringBatteryOptimizations(): Boolean {
    val powerManager = getSystemService(PowerManager::class.java) ?: return true
    return powerManager.isIgnoringBatteryOptimizations(packageName)
}

private fun Context.appVersion(): String =
    runCatching { packageManager.getPackageInfo(packageName, 0).versionName }
        .getOrNull() ?: "—"

private fun Context.openBatterySettings() {
    runCatching {
        startActivity(
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }
}

private fun Context.openUrl(url: String) {
    runCatching {
        startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }
}
