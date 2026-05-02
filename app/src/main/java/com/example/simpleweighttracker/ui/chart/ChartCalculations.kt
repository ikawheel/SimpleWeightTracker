package com.example.simpleweighttracker.ui.chart

import com.example.simpleweighttracker.model.DailyWeightPoint
import com.example.simpleweighttracker.model.GraphRange
import com.example.simpleweighttracker.model.WeightRecord
import com.example.simpleweighttracker.ui.WeightTrackerFormatters
import java.time.LocalDate
import kotlin.math.ceil
import kotlin.math.floor

internal fun buildChartPoints(points: List<DailyWeightPoint>): List<ChartPoint> {
    return points.map { point ->
        ChartPoint(
            date = point.date,
            value = point.netWeight,
            secondaryValue = point.movingAverage
        )
    }
}

internal fun buildChartDateRange(
    records: List<WeightRecord>,
    graphRange: GraphRange
): ChartDateRange {
    val fallbackEnd = LocalDate.now()
    val end = records.maxByOrNull { record -> record.date.toEpochDay() }?.date ?: fallbackEnd
    val start = when (graphRange) {
        GraphRange.OneMonth -> end.minusMonths(1)
        GraphRange.ThreeMonths -> end.minusMonths(3)
        GraphRange.SixMonths -> end.minusMonths(6)
        GraphRange.OneYear -> end.minusYears(1)
        GraphRange.All -> records.minByOrNull { record -> record.date.toEpochDay() }?.date ?: end
    }
    return ChartDateRange(start = start, end = end)
}

internal fun buildChartScale(points: List<ChartPoint>): ChartScale {
    val allValues = buildList {
        addAll(points.map(ChartPoint::value))
        addAll(points.mapNotNull(ChartPoint::secondaryValue))
    }
    val rawMin = allValues.minOrNull() ?: 0.0
    val rawMax = allValues.maxOrNull() ?: rawMin
    val initialChartMinHalf = floor(rawMin * 2.0).toInt().coerceAtLeast(0)
    val initialChartMaxHalf = ceil(rawMax * 2.0).toInt().coerceAtLeast(initialChartMinHalf)
    val chartMinHalf = if (initialChartMinHalf == initialChartMaxHalf) {
        (initialChartMinHalf - 1).coerceAtLeast(0)
    } else {
        initialChartMinHalf
    }
    val chartMaxHalf = if (initialChartMinHalf == initialChartMaxHalf) {
        initialChartMaxHalf + 1
    } else {
        initialChartMaxHalf
    }
    val chartMin = chartMinHalf / 2.0
    val chartMax = chartMaxHalf / 2.0
    val ticks = (chartMinHalf..chartMaxHalf)
        .map { halfStep -> halfStep / 2.0 }
        .asReversed()

    return ChartScale(
        min = chartMin,
        max = chartMax,
        ticks = ticks,
        dottedTicks = buildDottedQuarterTicks(chartMin = chartMin, chartMax = chartMax)
    )
}

internal fun buildChartXAxisTicks(
    dateRange: ChartDateRange,
    graphRange: GraphRange
): List<ChartXAxisTick> {
    val dates = when (graphRange) {
        GraphRange.OneMonth -> buildWeeklyTickDates(dateRange)
        GraphRange.ThreeMonths,
        GraphRange.SixMonths,
        GraphRange.OneYear,
        GraphRange.All -> buildMonthlyFirstTickDates(dateRange)
    }

    return dates.map { date ->
        ChartXAxisTick(
            date = date,
            label = if (graphRange == GraphRange.OneYear) {
                date.monthValue.toString()
            } else {
                WeightTrackerFormatters.formatShortDate(date)
            }
        )
    }
}

internal fun calculateMonthlyTrendPerMonth(
    points: List<DailyWeightPoint>
): Double? {
    if (points.size < 2) {
        return null
    }

    val baseEpochDay = points.first().date.toEpochDay().toDouble()
    val xs = points.map { point -> point.date.toEpochDay().toDouble() - baseEpochDay }
    val ys = points.map { point -> point.netWeight }
    val meanX = xs.average()
    val meanY = ys.average()
    val numerator = xs.indices.sumOf { index ->
        (xs[index] - meanX) * (ys[index] - meanY)
    }
    val denominator = xs.sumOf { x ->
        val diff = x - meanX
        diff * diff
    }

    if (denominator == 0.0) {
        return null
    }

    val slopePerDay = numerator / denominator
    val slopePerMonth = slopePerDay * 30.44
    return WeightTrackerFormatters.roundToTwoDecimals(slopePerMonth)
}

private fun buildDottedQuarterTicks(
    chartMin: Double,
    chartMax: Double
): List<Double> {
    val minQuarter = ceil(chartMin * 4.0).toInt()
    val maxQuarter = floor(chartMax * 4.0).toInt()
    return (minQuarter..maxQuarter)
        .filter { quarterStep -> quarterStep % 2 != 0 }
        .map { quarterStep -> quarterStep / 4.0 }
}

private fun buildWeeklyTickDates(dateRange: ChartDateRange): List<LocalDate> {
    return generateSequence(dateRange.start) { date -> date.plusWeeks(1) }
        .takeWhile { date -> !date.isAfter(dateRange.end) }
        .toList()
}

private fun buildMonthlyFirstTickDates(dateRange: ChartDateRange): List<LocalDate> {
    val firstMonthStart = dateRange.start.withDayOfMonth(1)
    val firstTick = if (firstMonthStart.isBefore(dateRange.start)) {
        firstMonthStart.plusMonths(1)
    } else {
        firstMonthStart
    }

    return generateSequence(firstTick) { date -> date.plusMonths(1) }
        .takeWhile { date -> !date.isAfter(dateRange.end) }
        .toList()
}
