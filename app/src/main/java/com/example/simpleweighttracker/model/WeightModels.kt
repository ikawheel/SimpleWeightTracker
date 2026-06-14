package com.ikeansoft.simpleweighttracker.model

import androidx.annotation.StringRes
import com.ikeansoft.simpleweighttracker.R
import java.time.LocalDate

data class WeightRecord(
    val id: Long = 0,
    val date: LocalDate,
    val measuredWeight: Double,
    val clothesWeight: Double,
    val netWeight: Double,
    val createdAt: Long,
    val updatedAt: Long
)

data class DailyWeightPoint(
    val date: LocalDate,
    val netWeight: Double,
    val movingAverage: Double?
)

data class PeriodWeightPoint(
    val periodStart: LocalDate,
    val netWeight: Double
)

enum class GraphType {
    Daily,
    Weekly,
    Monthly
}

enum class GraphRange(
    @StringRes val labelResId: Int
) {
    OneMonth(labelResId = R.string.graph_range_one_month),
    ThreeMonths(labelResId = R.string.graph_range_three_months),
    SixMonths(labelResId = R.string.graph_range_six_months),
    OneYear(labelResId = R.string.graph_range_one_year),
    All(labelResId = R.string.graph_range_all)
}
