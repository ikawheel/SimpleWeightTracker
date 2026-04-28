package com.example.simpleweighttracker.ui

import com.example.simpleweighttracker.model.DailyWeightPoint
import com.example.simpleweighttracker.model.GraphType
import com.example.simpleweighttracker.model.PeriodWeightPoint
import com.example.simpleweighttracker.model.WeightRecord
import java.time.LocalDate

data class RecordFormState(
    val recordId: Long? = null,
    val date: LocalDate = LocalDate.now(),
    val measuredWeightInput: String = "",
    val clothesWeightInput: String = "0.0",
    val netWeightPreview: Double? = null,
    val measuredWeightError: String? = null,
    val clothesWeightError: String? = null,
    val generalError: String? = null,
    val isEditing: Boolean = false
)

data class WeightUiState(
    val records: List<WeightRecord> = emptyList(),
    val latestClothesWeight: Double = 0.0,
    val selectedGraphType: GraphType = GraphType.Daily,
    val dailyChartData: List<DailyWeightPoint> = emptyList(),
    val weeklyChartData: List<PeriodWeightPoint> = emptyList(),
    val monthlyChartData: List<PeriodWeightPoint> = emptyList(),
    val formState: RecordFormState = RecordFormState()
)
