package com.ikeansoft.simpleweighttracker.ui.settings

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ikeansoft.simpleweighttracker.R
import com.ikeansoft.simpleweighttracker.export.WeightRecordCsvExporter
import com.ikeansoft.simpleweighttracker.export.WeightRecordCsvHeaders
import com.ikeansoft.simpleweighttracker.ui.WeightUiState

@Composable
fun SettingsScreen(
    uiState: WeightUiState,
    contentPadding: PaddingValues,
    onRecordLineColorSelected: (Int?) -> Unit,
    onMovingAverageLineColorSelected: (Int) -> Unit,
    onMovingAverageDaysChanged: (Int) -> Unit
) {
    val context = LocalContext.current
    var settingsPage by rememberSaveable { mutableStateOf(SettingsPage.Menu) }
    var showCsvExportDialog by rememberSaveable { mutableStateOf(false) }
    val latestRecords by rememberUpdatedState(uiState.records)
    val csvHeaders = WeightRecordCsvHeaders(
        date = stringResource(R.string.csv_header_date),
        measuredWeight = stringResource(R.string.csv_header_measured_weight),
        clothesWeight = stringResource(R.string.csv_header_clothes_weight),
        netWeight = stringResource(R.string.csv_header_net_weight)
    )
    val latestCsvHeaders by rememberUpdatedState(csvHeaders)
    val csvExportSuccessMessage = stringResource(R.string.csv_export_success)
    val csvExportFailureMessage = stringResource(R.string.csv_export_failure)

    if (showCsvExportDialog) {
        AlertDialog(
            onDismissRequest = { showCsvExportDialog = false },
            title = { Text(stringResource(R.string.csv_export_title)) },
            text = { Text(stringResource(R.string.csv_export_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCsvExportDialog = false
                        val exported = WeightRecordCsvExporter.export(
                            context = context,
                            records = latestRecords,
                            headers = latestCsvHeaders
                        )
                        Toast.makeText(
                            context,
                            if (exported) csvExportSuccessMessage else csvExportFailureMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Text(stringResource(R.string.action_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCsvExportDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    when (settingsPage) {
        SettingsPage.Menu -> SettingsMenuScreen(
            contentPadding = contentPadding,
            onOpenColorSettings = { settingsPage = SettingsPage.Color },
            onOpenMovingAverageSettings = { settingsPage = SettingsPage.MovingAverage },
            onOpenLicenseSettings = { settingsPage = SettingsPage.Licenses },
            onCsvExport = { showCsvExportDialog = true }
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

        SettingsPage.Licenses -> LicenseScreen(
            contentPadding = contentPadding,
            onBack = { settingsPage = SettingsPage.Menu }
        )
    }
}

private enum class SettingsPage {
    Menu,
    Color,
    MovingAverage,
    Licenses
}
