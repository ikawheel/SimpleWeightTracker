package com.example.simpleweighttracker.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simpleweighttracker.data.WeightRecordRepository
import com.example.simpleweighttracker.data.WeightTrackerDatabase
import com.example.simpleweighttracker.model.DailyWeightPoint
import com.example.simpleweighttracker.model.GraphRange
import com.example.simpleweighttracker.model.WeightRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.sin
import kotlin.random.Random

class WeightTrackerViewModel(
    private val repository: WeightRecordRepository
) : ViewModel() {
    private val records = MutableStateFlow<List<WeightRecord>>(emptyList())
    private val selectedGraphRange = MutableStateFlow(GraphRange.All)
    private val formState = MutableStateFlow(RecordFormState())

    val uiState: StateFlow<WeightUiState> = combine(
        records,
        selectedGraphRange,
        formState
    ) { currentRecords, graphRange, currentForm ->
        val allDailyChartData = WeightChartAggregator.buildDaily(currentRecords)
        WeightUiState(
            records = currentRecords,
            latestClothesWeight = currentRecords.firstOrNull()?.clothesWeight ?: 0.0,
            selectedGraphRange = graphRange,
            dailyChartData = filterDailyChartData(allDailyChartData, graphRange),
            formState = currentForm
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WeightUiState()
    )

    init {
        viewModelScope.launch {
            repository.observeAll().collect { currentRecords ->
                records.value = currentRecords
            }
        }

        viewModelScope.launch {
            val latestClothesWeight = repository.getLatestRecord()?.clothesWeight ?: 0.0
            formState.update { current ->
                if (
                    current.isEditing ||
                    current.measuredWeightInput.isNotBlank() ||
                    current.clothesWeightInput != WeightTrackerFormatters.formatInput(0.0)
                ) {
                    current
                } else {
                    current.copy(
                        clothesWeightInput = WeightTrackerFormatters.formatInput(latestClothesWeight),
                        netWeightPreview = calculateNetWeightPreview(
                            measuredWeightInput = current.measuredWeightInput,
                            clothesWeightInput = WeightTrackerFormatters.formatInput(latestClothesWeight)
                        )
                    )
                }
            }
        }
    }

    fun updateMeasuredWeight(input: String) {
        updateForm {
            copy(
                measuredWeightInput = WeightTrackerFormatters.normalizeDecimalInput(input),
                measuredWeightError = null,
                generalError = null
            )
        }
    }

    fun updateClothesWeight(input: String) {
        updateForm {
            copy(
                clothesWeightInput = WeightTrackerFormatters.normalizeDecimalInput(input),
                clothesWeightError = null,
                generalError = null
            )
        }
    }

    fun updateDate(date: LocalDate) {
        updateForm {
            copy(
                date = date,
                generalError = null
            )
        }
    }

    fun selectGraphRange(graphRange: GraphRange) {
        selectedGraphRange.value = graphRange
    }

    fun startEditing(record: WeightRecord) {
        formState.value = RecordFormState(
            recordId = record.id,
            date = record.date,
            measuredWeightInput = WeightTrackerFormatters.formatInput(record.measuredWeight),
            clothesWeightInput = WeightTrackerFormatters.formatInput(record.clothesWeight),
            netWeightPreview = WeightTrackerFormatters.roundToTwoDecimals(record.netWeight),
            isEditing = true
        )
    }

    fun startCreating() {
        resetForm(defaultClothesWeight = currentLatestClothesWeight())
    }

    fun cancelEditing() {
        resetForm(defaultClothesWeight = currentLatestClothesWeight())
    }

    fun saveRecord(): Boolean {
        val currentForm = formState.value
        val validationResult = validate(currentForm)

        if (validationResult != null) {
            formState.value = validationResult
            return false
        }

        val measuredWeight = WeightTrackerFormatters.parseDecimal(currentForm.measuredWeightInput) ?: return false
        val clothesWeight = WeightTrackerFormatters.parseDecimal(currentForm.clothesWeightInput) ?: return false
        val netWeight = WeightTrackerFormatters.roundToTwoDecimals(measuredWeight - clothesWeight)
        val now = System.currentTimeMillis()
        val existingRecord = currentForm.recordId?.let { recordId ->
            records.value.firstOrNull { it.id == recordId }
        }

        val record = WeightRecord(
            id = currentForm.recordId ?: 0,
            date = currentForm.date,
            measuredWeight = measuredWeight,
            clothesWeight = clothesWeight,
            netWeight = netWeight,
            createdAt = existingRecord?.createdAt ?: now,
            updatedAt = now
        )

        viewModelScope.launch {
            if (currentForm.isEditing) {
                repository.update(record)
            } else {
                repository.insert(record)
            }

            resetForm(defaultClothesWeight = clothesWeight)
        }

        return true
    }

    fun deleteRecord(record: WeightRecord) {
        viewModelScope.launch {
            val previousLatestClothesWeight = currentLatestClothesWeight()
            repository.delete(record)

            val shouldResetEditedRecord = formState.value.recordId == record.id
            if (shouldResetEditedRecord || shouldRefreshBlankForm(previousLatestClothesWeight)) {
                val latestClothesWeight = repository.getLatestRecord()?.clothesWeight ?: 0.0
                resetForm(defaultClothesWeight = latestClothesWeight)
            }
        }
    }

    fun insertDebugRecords() {
        viewModelScope.launch {
            val existingDates = records.value.map(WeightRecord::date).toSet()
            val endDate = LocalDate.now()
            val startDate = endDate.minusMonths(6)
            val zoneId = ZoneId.systemDefault()
            val random = Random(System.currentTimeMillis())

            var currentDate = startDate
            while (!currentDate.isAfter(endDate)) {
                if (currentDate !in existingDates) {
                    val dayOffset = java.time.temporal.ChronoUnit.DAYS.between(startDate, currentDate).toDouble()
                    val measuredWeight = WeightTrackerFormatters.roundToTwoDecimals(
                        (70.0 + sin(dayOffset / 9.0) * 0.45 + random.nextDouble(-0.35, 0.35))
                            .coerceIn(68.8, 71.4)
                    )
                    val clothesWeight = WeightTrackerFormatters.roundToTwoDecimals(
                        (0.6 + random.nextDouble(-0.18, 0.18)).coerceIn(0.2, 1.0)
                    )
                    val netWeight = WeightTrackerFormatters.roundToTwoDecimals(measuredWeight - clothesWeight)
                    val createdAt = currentDate
                        .atTime(7 + random.nextInt(0, 3), random.nextInt(0, 60))
                        .atZone(zoneId)
                        .toInstant()
                        .toEpochMilli()

                    repository.insert(
                        WeightRecord(
                            date = currentDate,
                            measuredWeight = measuredWeight,
                            clothesWeight = clothesWeight,
                            netWeight = netWeight,
                            createdAt = createdAt,
                            updatedAt = createdAt
                        )
                    )
                }

                currentDate = currentDate.plusDays(1)
            }
        }
    }

    fun deleteAllRecords() {
        viewModelScope.launch {
            repository.deleteAll()
            resetForm(defaultClothesWeight = 0.0)
        }
    }

    private fun filterDailyChartData(
        points: List<DailyWeightPoint>,
        graphRange: GraphRange
    ): List<DailyWeightPoint> {
        val latestDate = points.lastOrNull()?.date ?: return emptyList()
        val startDate = when (graphRange) {
            GraphRange.OneMonth -> latestDate.minusMonths(1)
            GraphRange.ThreeMonths -> latestDate.minusMonths(3)
            GraphRange.SixMonths -> latestDate.minusMonths(6)
            GraphRange.OneYear -> latestDate.minusYears(1)
            GraphRange.All -> null
        }

        return if (startDate == null) {
            points
        } else {
            points.filter { point -> !point.date.isBefore(startDate) }
        }
    }

    private fun shouldRefreshBlankForm(previousLatestClothesWeight: Double): Boolean {
        val currentForm = formState.value
        return !currentForm.isEditing &&
            currentForm.measuredWeightInput.isBlank() &&
            currentForm.clothesWeightInput == WeightTrackerFormatters.formatInput(previousLatestClothesWeight)
    }

    private fun currentLatestClothesWeight(): Double = records.value.firstOrNull()?.clothesWeight ?: 0.0

    private fun updateForm(transform: RecordFormState.() -> RecordFormState) {
        formState.update { current ->
            val updated = current.transform()
            updated.copy(
                netWeightPreview = calculateNetWeightPreview(
                    measuredWeightInput = updated.measuredWeightInput,
                    clothesWeightInput = updated.clothesWeightInput
                )
            )
        }
    }

    private fun calculateNetWeightPreview(
        measuredWeightInput: String,
        clothesWeightInput: String
    ): Double? {
        val measuredWeight = WeightTrackerFormatters.parseDecimal(measuredWeightInput) ?: return null
        val clothesWeight = WeightTrackerFormatters.parseDecimal(clothesWeightInput) ?: return null
        return WeightTrackerFormatters.roundToTwoDecimals(measuredWeight - clothesWeight)
    }

    private fun validate(currentForm: RecordFormState): RecordFormState? {
        val measuredWeight = WeightTrackerFormatters.parseDecimal(currentForm.measuredWeightInput)
        val clothesWeight = WeightTrackerFormatters.parseDecimal(currentForm.clothesWeightInput)

        var measuredError: String? = null
        var clothesError: String? = null
        var generalError: String? = null

        if (measuredWeight == null) {
            measuredError = "体重を入力してください"
        } else if (measuredWeight !in 20.0..300.0) {
            measuredError = "体重は20.0kgから300.0kgの範囲で入力してください"
        }

        if (clothesWeight == null) {
            clothesError = "服の重さを入力してください"
        } else if (clothesWeight !in 0.0..10.0) {
            clothesError = "服の重さは0.0kgから10.0kgの範囲で入力してください"
        }

        if (measuredWeight != null && clothesWeight != null && measuredWeight - clothesWeight <= 0.0) {
            generalError = "記録体重が0以下になるため保存できません"
        }

        return if (measuredError == null && clothesError == null && generalError == null) {
            null
        } else {
            currentForm.copy(
                measuredWeightError = measuredError,
                clothesWeightError = clothesError,
                generalError = generalError,
                netWeightPreview = calculateNetWeightPreview(
                    measuredWeightInput = currentForm.measuredWeightInput,
                    clothesWeightInput = currentForm.clothesWeightInput
                )
            )
        }
    }

    private fun resetForm(defaultClothesWeight: Double) {
        formState.value = RecordFormState(
            date = LocalDate.now(),
            clothesWeightInput = WeightTrackerFormatters.formatInput(defaultClothesWeight),
            isEditing = false
        )
    }

    class Factory(
        application: Application
    ) : ViewModelProvider.Factory {
        private val repository = WeightRecordRepository(
            WeightTrackerDatabase.getInstance(application).weightRecordDao()
        )

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeightTrackerViewModel::class.java)) {
                return WeightTrackerViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
