package com.example.simpleweighttracker.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
internal fun WeightChartCanvas(
    modifier: Modifier,
    points: List<ChartPoint>,
    chartScale: ChartScale,
    dateRange: ChartDateRange,
    recordLineColor: Color,
    movingAverageLineColor: Color
) {
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        val primaryStrokeWidth = with(density) { 2.dp.toPx() }
        val secondaryStrokeWidth = with(density) { 2.4.dp.toPx() }
        val gridStroke = with(density) { 1.dp.toPx() }
        val integerGridStroke = with(density) { 2.dp.toPx() }
        val dottedGridEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                with(density) { 3.dp.toPx() },
                with(density) { 4.dp.toPx() }
            ),
            phase = 0f
        )
        val topPadding = with(density) { 8.dp.toPx() }
        val bottomPadding = with(density) { 10.dp.toPx() }

        val left = 0f
        val right = size.width
        val top = topPadding
        val bottom = size.height - bottomPadding
        val width = right - left
        val height = bottom - top
        val span = (chartScale.max - chartScale.min).coerceAtLeast(0.1)

        val dateSpanDays = ChronoUnit.DAYS
            .between(dateRange.start, dateRange.end)
            .coerceAtLeast(1L)

        fun xPosition(date: LocalDate): Float {
            val elapsedDays = ChronoUnit.DAYS
                .between(dateRange.start, date)
                .coerceIn(0L, dateSpanDays)
            return left + width * (elapsedDays.toFloat() / dateSpanDays.toFloat())
        }

        fun yPosition(value: Double): Float {
            val ratio = ((value - chartScale.min) / span).toFloat()
            return bottom - (ratio * height)
        }

        chartScale.dottedTicks.forEach { tick ->
            val y = yPosition(tick)
            drawLine(
                color = outlineColor.copy(alpha = 0.45f),
                start = Offset(left, y),
                end = Offset(right, y),
                strokeWidth = gridStroke,
                pathEffect = dottedGridEffect
            )
        }

        chartScale.ticks.forEach { tick ->
            val y = yPosition(tick)
            val stroke = if (tick % 1.0 == 0.0) {
                integerGridStroke
            } else {
                gridStroke
            }
            drawLine(
                color = outlineColor.copy(alpha = 0.7f),
                start = Offset(left, y),
                end = Offset(right, y),
                strokeWidth = stroke
            )
        }

        drawLine(
            color = outlineColor,
            start = Offset(left, top),
            end = Offset(left, bottom),
            strokeWidth = gridStroke
        )
        drawLine(
            color = outlineColor,
            start = Offset(left, bottom),
            end = Offset(right, bottom),
            strokeWidth = gridStroke
        )

        val primaryOffsets = points.map { point ->
            Offset(xPosition(point.date), yPosition(point.value))
        }

        drawSeries(primaryOffsets, color = recordLineColor, strokeWidth = primaryStrokeWidth)

        buildSecondarySegments(
            points = points,
            xPosition = { date -> xPosition(date) },
            yPosition = { value -> yPosition(value) }
        ).forEach { offsets ->
            drawSeries(
                offsets = offsets,
                color = movingAverageLineColor,
                strokeWidth = secondaryStrokeWidth
            )
        }
    }
}

private fun buildSecondarySegments(
    points: List<ChartPoint>,
    xPosition: (LocalDate) -> Float,
    yPosition: (Double) -> Float
): List<List<Offset>> {
    val segments = mutableListOf<List<Offset>>()
    var currentSegment = mutableListOf<Offset>()

    points.forEach { point ->
        val secondaryValue = point.secondaryValue
        if (secondaryValue == null) {
            if (currentSegment.isNotEmpty()) {
                segments.add(currentSegment.toList())
            }
            currentSegment = mutableListOf()
        } else {
            currentSegment.add(Offset(xPosition(point.date), yPosition(secondaryValue)))
        }
    }

    if (currentSegment.isNotEmpty()) {
        segments.add(currentSegment.toList())
    }

    return segments
}

private fun DrawScope.drawSeries(
    offsets: List<Offset>,
    color: Color,
    strokeWidth: Float
) {
    if (offsets.isEmpty()) {
        return
    }

    if (offsets.size == 1) {
        drawCircle(
            color = color,
            radius = strokeWidth,
            center = offsets.first()
        )
        return
    }

    val path = Path().apply {
        moveTo(offsets.first().x, offsets.first().y)
        offsets.drop(1).forEach { offset ->
            lineTo(offset.x, offset.y)
        }
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}
