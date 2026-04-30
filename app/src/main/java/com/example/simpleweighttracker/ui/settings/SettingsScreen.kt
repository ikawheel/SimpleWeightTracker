package com.example.simpleweighttracker.ui.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.simpleweighttracker.ui.WeightUiState

@Composable
fun SettingsScreen(
    uiState: WeightUiState,
    contentPadding: PaddingValues,
    onRecordLineColorSelected: (Int?) -> Unit,
    onMovingAverageLineColorSelected: (Int) -> Unit,
    onMovingAverageDaysChanged: (Int) -> Unit
) {
    var settingsPage by rememberSaveable { mutableStateOf(SettingsPage.Menu) }

    when (settingsPage) {
        SettingsPage.Menu -> SettingsMenuScreen(
            contentPadding = contentPadding,
            onOpenColorSettings = { settingsPage = SettingsPage.Color },
            onOpenMovingAverageSettings = { settingsPage = SettingsPage.MovingAverage }
        )

        SettingsPage.Color -> ColorSettingsScreen(
            uiState = uiState,
            contentPadding = contentPadding,
            onBack = { settingsPage = SettingsPage.Menu },
            onRecordLineColorSelected = onRecordLineColorSelected,
            onMovingAverageLineColorSelected = onMovingAverageLineColorSelected
        )

        SettingsPage.MovingAverage -> MovingAverageSettingsScreen(
            uiState = uiState,
            contentPadding = contentPadding,
            onBack = { settingsPage = SettingsPage.Menu },
            onMovingAverageDaysChanged = onMovingAverageDaysChanged
        )
    }
}

private enum class SettingsPage {
    Menu,
    Color,
    MovingAverage
}
