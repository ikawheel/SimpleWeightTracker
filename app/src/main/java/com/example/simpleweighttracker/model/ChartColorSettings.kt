package com.example.simpleweighttracker.model

val DefaultMovingAverageLineColorArgb: Int = 0xFFFFA726.toInt()
const val DefaultMovingAverageDays = 7

data class ChartColorSettings(
    val recordLineColorArgb: Int? = null,
    val movingAverageLineColorArgb: Int = DefaultMovingAverageLineColorArgb,
    val movingAverageDays: Int = DefaultMovingAverageDays
)
