package com.apexbytestudio.entrix.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexbytestudio.entrix.domain.usecase.ObserveTrackingEnabledUseCase
import com.apexbytestudio.entrix.domain.usecase.SetTrackingEnabledUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    observeTrackingEnabled: ObserveTrackingEnabledUseCase,
    private val setTrackingEnabled: SetTrackingEnabledUseCase,
) : ViewModel() {

    val trackingEnabled: StateFlow<Boolean> = observeTrackingEnabled()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = false,
        )

    fun setTracking(enabled: Boolean) {
        viewModelScope.launch { setTrackingEnabled(enabled) }
    }
}
