package com.example.simpleweighttracker.ui.chart

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.simpleweighttracker.R
import com.example.simpleweighttracker.model.GraphRange
import com.example.simpleweighttracker.ui.WeightTrackerFormatters
import com.example.simpleweighttracker.ui.WeightUiState

@Composable
fun ChartScreen(
    uiState: WeightUiState,
    contentPadding: PaddingValues,
    onGraphRangeSelected: (GraphRange) -> Unit
) {
    val recordLineColor = Color(
        uiState.chartColorSettings.recordLineColorArgb ?: DefaultRecordLineColorArgb
    )
    val movingAverageLineColor = Color(uiState.chartColorSettings.movingAverageLineColorArgb)
    val chartDateRange = remember(uiState.records, uiState.selectedGraphRange) {
        buildChartDateRange(
            records = uiState.records,
            graphRange = uiState.selectedGraphRange
        )
    }
    val trendPerMonth = remember(uiState.dailyChartData) {
        calculateMonthlyTrendPerMonth(uiState.dailyChartData)
    }
    val trendLabel = trendPerMonth?.let { monthlyTrend ->
        val monthlyTrendText = stringResource(
            R.string.monthly_trend_value,
            WeightTrackerFormatters.formatSignedWeightValue(monthlyTrend)
        )
        stringResource(R.string.chart_trend_label, monthlyTrendText)
    }
    val dailyPoints = remember(uiState.dailyChartData) {
        buildChartPoints(uiState.dailyChartData)
    }
    val xAxisTicks = remember(chartDateRange, uiState.selectedGraphRange) {
        buildChartXAxisTicks(
            dateRange = chartDateRange,
            graphRange = uiState.selectedGraphRange
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WeightChart(
            modifier = Modifier.weight(1f),
            points = dailyPoints,
            dateRange = chartDateRange,
            xAxisTicks = xAxisTicks,
            recordLineColor = recordLineColor,
            movingAverageLineColor = movingAverageLineColor,
            movingAverageDays = uiState.chartColorSettings.movingAverageDays,
            trendLabel = trendLabel
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            GraphRange.entries.forEach { graphRange ->
                GraphRangeButton(
                    graphRange = graphRange,
                    selected = uiState.selectedGraphRange == graphRange,
                    onClick = { onGraphRangeSelected(graphRange) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GraphRangeButton(
    graphRange: GraphRange,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(999.dp)
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .height(36.dp)
            .clip(shape)
            .background(backgroundColor, shape)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(graphRange.labelResId),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}
