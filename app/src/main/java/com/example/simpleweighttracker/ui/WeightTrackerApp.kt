package com.example.simpleweighttracker.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.simpleweighttracker.R
import com.example.simpleweighttracker.ui.chart.ChartScreen
import com.example.simpleweighttracker.ui.editor.RecordEditorDialog
import com.example.simpleweighttracker.ui.records.RecordsScreen
import com.example.simpleweighttracker.ui.settings.SettingsScreen

@Composable
fun WeightTrackerApp(
    viewModel: WeightTrackerViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by rememberSaveable { mutableStateOf(AppTab.List) }
    var showEditorDialog by rememberSaveable { mutableStateOf(false) }

    MaterialTheme {
        val navigationBackground = MaterialTheme.colorScheme.surfaceContainer
        val statusBarBackground = MaterialTheme.colorScheme.primary

        if (showEditorDialog) {
            RecordEditorDialog(
                uiState = uiState,
                onDateChanged = viewModel::updateDate,
                onMeasuredWeightChanged = viewModel::updateMeasuredWeight,
                onMeasuredWeightFocused = viewModel::clearMeasuredWeightDefaultOnFocus,
                onMeasuredWeightUnfocused = viewModel::restoreMeasuredWeightDefaultOnBlur,
                onClothesWeightChanged = viewModel::updateClothesWeight,
                onClothesWeightFocused = viewModel::clearClothesWeightDefaultOnFocus,
                onClothesWeightUnfocused = viewModel::restoreClothesWeightDefaultOnBlur,
                onSave = {
                    if (viewModel.saveRecord()) {
                        showEditorDialog = false
                    }
                },
                onDismiss = {
                    viewModel.cancelEditing()
                    showEditorDialog = false
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = navigationBackground,
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(navigationBackground)
                                .navigationBarsPadding()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .background(navigationBackground)
                                    .padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AppTab.entries.forEach { tab ->
                                    val selected = selectedTab == tab
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .background(navigationBackground)
                                            .clickable { selectedTab = tab }
                                            .padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = stringResource(tab.titleResId),
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (selected) {
                                                    MaterialTheme.colorScheme.onSurface
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                }
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .height(2.dp)
                                                    .width(36.dp)
                                                    .background(
                                                        color = if (selected) {
                                                            MaterialTheme.colorScheme.primary
                                                        } else {
                                                            Color.Transparent
                                                        },
                                                        shape = RoundedCornerShape(999.dp)
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                when (selectedTab) {
                    AppTab.List -> RecordsScreen(
                        records = uiState.records,
                        contentPadding = innerPadding,
                        onAdd = {
                            viewModel.startCreating()
                            showEditorDialog = true
                        },
                        onEdit = { record ->
                            viewModel.startEditing(record)
                            showEditorDialog = true
                        },
                        onDelete = viewModel::deleteRecord
                    )

                    AppTab.Chart -> ChartScreen(
                        uiState = uiState,
                        contentPadding = innerPadding,
                        onGraphRangeSelected = viewModel::selectGraphRange
                    )

                    AppTab.Settings -> SettingsScreen(
                        uiState = uiState,
                        contentPadding = innerPadding,
                        onRecordLineColorSelected = viewModel::updateRecordLineColor,
                        onMovingAverageLineColorSelected = viewModel::updateMovingAverageLineColor,
                        onMovingAverageDaysChanged = viewModel::updateMovingAverageDays
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(statusBarBackground)
            )
        }
    }
}

private enum class AppTab(
    @StringRes val titleResId: Int
) {
    List(titleResId = R.string.tab_list),
    Chart(titleResId = R.string.tab_chart),
    Settings(titleResId = R.string.tab_settings)
}
