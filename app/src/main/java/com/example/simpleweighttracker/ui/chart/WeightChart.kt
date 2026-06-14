package com.ikeansoft.simpleweighttracker.ui.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ikeansoft.simpleweighttracker.R
import com.ikeansoft.simpleweighttracker.ui.WeightTrackerFormatters
import java.time.temporal.ChronoUnit

@Composable
internal fun WeightChart(
    modifier: Modifier = Modifier,
    points: List<ChartPoint>,
    dateRange: ChartDateRange,
    xAxisTicks: List<ChartXAxisTick>,
    recordLineColor: Color,
    movingAverageLineColor: Color,
    movingAverageDays: Int,
    trendLabel: String?,
    onDateRangePan: (Long) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (points.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.empty_records))
            }
            ChartXAxis(
                dateRange = dateRange,
                ticks = xAxisTicks,
                onDateRangePan = onDateRangePan,
                modifier = Modifier.padding(start = 44.dp)
            )
            return@Column
        }

        val chartScale = remember(points) {
            buildChartScale(points)
        }
        var selectedPoint by remember(points, dateRange) {
            mutableStateOf<ChartPoint?>(null)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(36.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                chartScale.ticks.forEach { tick ->
                    Text(
                        text = WeightTrackerFormatters.formatAxisValue(tick),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                WeightChartCanvas(
                    modifier = Modifier.fillMaxSize(),
                    points = points,
                    chartScale = chartScale,
                    dateRange = dateRange,
                    xAxisTicks = xAxisTicks,
                    selectedPoint = selectedPoint,
                    recordLineColor = recordLineColor,
                    movingAverageLineColor = movingAverageLineColor,
                    onPointSelected = { point -> selectedPoint = point }
                )

                selectedPoint?.let { point ->
                    ChartSelectionInfo(
                        point = point,
                        movingAverageDays = movingAverageDays,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 8.dp, bottom = 14.dp)
                    )
                }
            }
        }

        ChartXAxis(
            dateRange = dateRange,
            ticks = xAxisTicks,
            onDateRangePan = onDateRangePan,
            modifier = Modifier.padding(start = 44.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(
                    color = recordLineColor,
                    label = stringResource(R.string.legend_record_weight)
                )
                if (points.any { it.secondaryValue != null }) {
                    LegendItem(
                        color = movingAverageLineColor,
                        label = stringResource(R.string.legend_moving_average, movingAverageDays)
                    )
                }
            }

            if (trendLabel != null) {
                Text(
                    text = trendLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun ChartSelectionInfo(
    point: ChartPoint,
    movingAverageDays: Int,
    modifier: Modifier = Modifier
) {
    val movingAverageValue = point.secondaryValue?.let { value ->
        WeightTrackerFormatters.formatWeight(value)
    } ?: stringResource(R.string.chart_selected_empty_value)

    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = stringResource(
                R.string.chart_selected_date,
                WeightTrackerFormatters.formatFullDate(point.date)
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(
                R.string.chart_selected_record_weight,
                WeightTrackerFormatters.formatWeight(point.value)
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(
                R.string.chart_selected_moving_average,
                movingAverageDays,
                movingAverageValue
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ChartXAxis(
    dateRange: ChartDateRange,
    ticks: List<ChartXAxisTick>,
    onDateRangePan: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(18.dp)
    ) {
        val labelWidth = 48.dp
        val dataWidth = (maxWidth - ChartRightInset).coerceAtLeast(0.dp)
        val density = LocalDensity.current
        val updatedOnDateRangePan by rememberUpdatedState(onDateRangePan)
        val dateSpanDays = ChronoUnit.DAYS
            .between(dateRange.start, dateRange.end)
            .coerceAtLeast(1L)
        val dataWidthPx = with(density) { dataWidth.toPx() }
        val pixelsPerDay = dataWidthPx / dateSpanDays.toFloat()
        var accumulatedDragPx by remember(dateSpanDays, dataWidthPx) {
            mutableStateOf(0f)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(dateSpanDays, dataWidthPx) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            accumulatedDragPx = 0f
                        },
                        onDragEnd = {
                            accumulatedDragPx = 0f
                        },
                        onDragCancel = {
                            accumulatedDragPx = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            if (pixelsPerDay > 0f) {
                                accumulatedDragPx += dragAmount
                                val dayOffset = (accumulatedDragPx / pixelsPerDay).toLong()
                                if (dayOffset != 0L) {
                                    updatedOnDateRangePan(-dayOffset)
                                    accumulatedDragPx -= dayOffset * pixelsPerDay
                                }
                            }
                        }
                    )
                }
        ) {
            ticks.forEach { tick ->
                val ratio = calculateDatePositionRatio(dateRange = dateRange, date = tick.date)
                val offset = dataWidth * ratio - labelWidth / 2f
                Text(
                    text = tick.label,
                    modifier = Modifier
                        .width(labelWidth)
                        .offset(x = offset),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun calculateDatePositionRatio(
    dateRange: ChartDateRange,
    date: java.time.LocalDate
): Float {
    val dateSpanDays = ChronoUnit.DAYS
        .between(dateRange.start, dateRange.end)
        .coerceAtLeast(1L)
    val elapsedDays = ChronoUnit.DAYS
        .between(dateRange.start, date)
        .coerceIn(0L, dateSpanDays)
    return elapsedDays.toFloat() / dateSpanDays.toFloat()
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
