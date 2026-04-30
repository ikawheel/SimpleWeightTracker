package com.example.simpleweighttracker.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.simpleweighttracker.R
import com.example.simpleweighttracker.ui.WeightUiState
import com.example.simpleweighttracker.ui.chart.ChartLineColorOption
import com.example.simpleweighttracker.ui.chart.ChartLineColorPalette
import com.example.simpleweighttracker.ui.chart.DefaultRecordLineColorArgb

@Composable
internal fun ColorSettingsScreen(
    uiState: WeightUiState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onRecordLineColorSelected: (Int?) -> Unit,
    onMovingAverageLineColorSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(
            onClick = onBack,
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
        ) {
            Text(stringResource(R.string.action_back))
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_color),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                ColorSettingSection(
                    titleResId = R.string.record_line_title,
                    selectedColorArgb = uiState.chartColorSettings.recordLineColorArgb
                        ?: DefaultRecordLineColorArgb,
                    options = ChartLineColorPalette,
                    onColorSelected = { colorArgb ->
                        onRecordLineColorSelected(colorArgb)
                    }
                )

                ColorSettingSection(
                    titleResId = R.string.settings_moving_average,
                    selectedColorArgb = uiState.chartColorSettings.movingAverageLineColorArgb,
                    options = ChartLineColorPalette,
                    onColorSelected = onMovingAverageLineColorSelected
                )
            }
        }
    }
}

@Composable
private fun ColorSettingSection(
    @StringRes titleResId: Int,
    selectedColorArgb: Int?,
    options: List<ChartLineColorOption>,
    onColorSelected: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(titleResId),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        options.chunked(6).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowOptions.forEach { option ->
                    ColorOptionButton(
                        option = option,
                        selected = option.colorArgb == selectedColorArgb,
                        onClick = { onColorSelected(option.colorArgb) },
                        modifier = Modifier.weight(1f)
                    )
                }

                repeat(6 - rowOptions.size) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ColorOptionButton(
    option: ChartLineColorOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    val previewColor = Color(option.colorArgb)
    val outerBorderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Box(
        modifier = modifier
            .height(44.dp)
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else previewColor,
                shape = shape
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = outerBorderColor,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(if (selected) 3.dp else 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(previewColor, shape)
                .border(
                    width = if (selected) 1.dp else 0.dp,
                    color = Color.White,
                    shape = shape
                )
        )
    }
}
