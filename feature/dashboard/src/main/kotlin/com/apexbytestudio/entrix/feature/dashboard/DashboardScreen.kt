package com.apexbytestudio.entrix.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.apexbytestudio.entrix.core.designsystem.component.EntrixCard
import com.apexbytestudio.entrix.core.designsystem.component.EntrixOutlinedButton
import com.apexbytestudio.entrix.core.designsystem.component.EmptyState
import com.apexbytestudio.entrix.core.designsystem.component.LoadingState
import com.apexbytestudio.entrix.core.designsystem.component.EntrixButton
import com.apexbytestudio.entrix.core.designsystem.theme.EntrixSpacing
import com.apexbytestudio.entrix.core.designsystem.theme.EntrixTheme

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onEditOffice: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        uiState.isLoading -> LoadingState(modifier)
        !uiState.officeConfigured -> EmptyState(
            title = "No office configured",
            description = "Set up your office location to start tracking attendance automatically.",
            modifier = modifier,
            action = { EntrixButton(text = "Set up office", onClick = onEditOffice) },
        )

        else -> DashboardContent(uiState = uiState, onEditOffice = onEditOffice, modifier = modifier)
    }
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onEditOffice: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(EntrixSpacing.md),
        verticalArrangement = Arrangement.spacedBy(EntrixSpacing.md),
    ) {
        StatusCard(insideOffice = uiState.insideOffice, officeName = uiState.officeName)
        TodayCard(
            enter = uiState.todayEnter,
            exit = uiState.todayExit,
            duration = uiState.todayDuration,
        )
        TrackingCard(enabled = uiState.trackingEnabled)
        EntrixOutlinedButton(text = "Edit office", onClick = onEditOffice)
    }
}

@Composable
private fun StatusCard(insideOffice: Boolean, officeName: String?) {
    EntrixCard {
        Text(
            text = if (insideOffice) "Inside office" else "Outside office",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        if (officeName != null) {
            Text(
                text = officeName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TodayCard(enter: String?, exit: String?, duration: String?) {
    EntrixCard {
        Text(
            text = "Today",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = EntrixSpacing.sm),
        )
        LabeledValue(label = "Entry", value = enter ?: "—")
        LabeledValue(label = "Exit", value = exit ?: "—")
        LabeledValue(label = "Duration", value = duration ?: "—")
    }
}

@Composable
private fun TrackingCard(enabled: Boolean) {
    EntrixCard {
        LabeledValue(
            label = "Tracking",
            value = if (enabled) "Enabled" else "Disabled",
        )
    }
}

@Composable
private fun LabeledValue(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = EntrixSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Preview
@Composable
private fun DashboardContentPreview() {
    EntrixTheme {
        DashboardContent(
            uiState = DashboardUiState(
                isLoading = false,
                officeConfigured = true,
                officeName = "HQ",
                insideOffice = true,
                trackingEnabled = true,
                todayEnter = "08:02",
                todayExit = null,
                todayDuration = "1h 30m",
            ),
            onEditOffice = {},
        )
    }
}
