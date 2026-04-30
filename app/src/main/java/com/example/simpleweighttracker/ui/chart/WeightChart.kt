package com.example.simpleweighttracker.ui.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.simpleweighttracker.R
import com.example.simpleweighttracker.ui.WeightTrackerFormatters

@Composable
internal fun WeightChart(
    modifier: Modifier = Modifier,
    points: List<ChartPoint>,
    dateRange: ChartDateRange,
    recordLineColor: Color,
    movingAverageLineColor: Color,
    movingAverageDays: Int,
    trendLabel: String?
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
                modifier = Modifier.padding(start = 44.dp)
            )
            return@Column
        }

        val chartScale = remember(points) {
            buildChartScale(points)
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

            WeightChartCanvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                points = points,
                chartScale = chartScale,
                dateRange = dateRange,
                recordLineColor = recordLineColor,
                movingAverageLineColor = movingAverageLineColor
            )
        }

        ChartXAxis(
            dateRange = dateRange,
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
private fun ChartXAxis(
    dateRange: ChartDateRange,
    modifier: Modifier = Modifier
) {
    val labels = remember(dateRange) {
        buildChartXAxisLabels(dateRange)
    }

    when (labels.size) {
        1 -> Box(
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = labels.first(),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        2 -> Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = labels[0],
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = labels[1],
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = labels[2],
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End
            )
        }
    }
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
