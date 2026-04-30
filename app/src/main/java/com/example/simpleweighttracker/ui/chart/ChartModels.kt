package com.example.simpleweighttracker.ui.chart

import java.time.LocalDate

internal data class ChartPoint(
    val date: LocalDate,
    val value: Double,
    val secondaryValue: Double? = null
)

internal data class ChartDateRange(
    val start: LocalDate,
    val end: LocalDate
)

internal data class ChartScale(
    val min: Double,
    val max: Double,
    val ticks: List<Double>,
    val dottedTicks: List<Double>
)
