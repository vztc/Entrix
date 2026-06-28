package com.apexbytestudio.entrix.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.apexbytestudio.entrix.core.designsystem.theme.EntrixSpacing

/**
 * The app's standard surface container: a low-elevation tonal card with consistent inner padding.
 * Subtle by design — the spec calls for whitespace over heavy shadows.
 */
@Composable
fun EntrixCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = EntrixSpacing.xxs),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EntrixSpacing.md),
            content = content,
        )
    }
}
