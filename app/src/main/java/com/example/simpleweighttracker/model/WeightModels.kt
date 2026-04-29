package com.example.simpleweighttracker.model

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
    val label: String
) {
    OneMonth(label = "1か月"),
    ThreeMonths(label = "3か月"),
    SixMonths(label = "6か月"),
    OneYear(label = "1年"),
    All(label = "全期間")
}
