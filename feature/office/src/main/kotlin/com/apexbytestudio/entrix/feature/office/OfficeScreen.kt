package com.apexbytestudio.entrix.feature.office

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apexbytestudio.entrix.core.designsystem.component.EntrixButton
import com.apexbytestudio.entrix.core.designsystem.component.EntrixOutlinedButton
import com.apexbytestudio.entrix.core.designsystem.theme.EntrixSpacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun OfficeScreen(
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OfficeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                OfficeEvent.Saved -> onSaved()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) viewModel.useCurrentLocation() else viewModel.onLocationPermissionDenied()
    }

    fun requestCurrentLocation() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) viewModel.useCurrentLocation()
        else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(EntrixSpacing.md),
        verticalArrangement = Arrangement.spacedBy(EntrixSpacing.md),
    ) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = viewModel::onNameChange,
            label = { Text("Office name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        EntrixOutlinedButton(
            text = if (uiState.isLoadingLocation) "Getting location…" else "Use my current location",
            onClick = ::requestCurrentLocation,
            enabled = !uiState.isLoadingLocation,
        )
        if (uiState.isLoadingLocation) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            }
        }

        OutlinedTextField(
            value = uiState.latitude,
            onValueChange = viewModel::onLatitudeChange,
            label = { Text("Latitude") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = uiState.longitude,
            onValueChange = viewModel::onLongitudeChange,
            label = { Text("Longitude") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )

        Column {
            Text(
                text = "Radius: ${uiState.radiusMeters.toInt()} m",
                style = MaterialTheme.typography.bodyMedium,
            )
            Slider(
                value = uiState.radiusMeters,
                onValueChange = viewModel::onRadiusChange,
                valueRange = 100f..2000f,
            )
        }

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        EntrixButton(
            text = if (uiState.isSaving) "Saving…" else "Save office",
            onClick = viewModel::save,
            enabled = uiState.canSave,
        )
    }
}
