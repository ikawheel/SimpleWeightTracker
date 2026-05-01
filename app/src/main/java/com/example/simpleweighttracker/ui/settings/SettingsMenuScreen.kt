package com.example.simpleweighttracker.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.simpleweighttracker.R

@Composable
internal fun SettingsMenuScreen(
    contentPadding: PaddingValues,
    onOpenColorSettings: () -> Unit,
    onOpenMovingAverageSettings: () -> Unit,
    onOpenLicenseSettings: () -> Unit,
    onCsvExport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SettingsMenuItem(
            titleResId = R.string.settings_color,
            onClick = onOpenColorSettings
        )
        SettingsMenuItem(
            titleResId = R.string.settings_moving_average,
            onClick = onOpenMovingAverageSettings
        )
        SettingsMenuItem(
            titleResId = R.string.settings_csv_export,
            onClick = onCsvExport,
            showsNavigationIndicator = false
        )
        SettingsMenuItem(
            titleResId = R.string.settings_licenses,
            onClick = onOpenLicenseSettings
        )
    }
}

@Composable
private fun SettingsMenuItem(
    @StringRes titleResId: Int,
    onClick: () -> Unit,
    showsNavigationIndicator: Boolean = true
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(titleResId),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            if (showsNavigationIndicator) {
                Text(
                    text = ">",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
