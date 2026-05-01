package com.example.simpleweighttracker.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simpleweighttracker.R
import com.example.simpleweighttracker.data.ChartColorSettingsRepository
import com.example.simpleweighttracker.data.WeightRecordRepository
import com.example.simpleweighttracker.data.WeightTrackerDatabase
import com.example.simpleweighttracker.model.DailyWeightPoint
import com.example.simpleweighttracker.model.GraphRange
import com.example.simpleweighttracker.model.WeightRecord
import com.example.simpleweighttracker.ui.chart.WeightChartAggregator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val DEFAULT_ZERO_INPUT = "0"
private const val DEFAULT_NEW_MEASURED_WEIGHT_INPUT = DEFAULT_ZERO_INPUT

class WeightTrackerViewModel(
    private val repository: WeightRecordRepository,
    private val chartColorSettingsRepository: ChartColorSettingsRepository
) : ViewModel() {
    private val records = MutableStateFlow<List<WeightRecord>>(emptyList())
    private val selectedGraphRange = MutableStateFlow(GraphRange.All)
    private val formState = MutableStateFlow(RecordFormState())
    private val chartColorSettings = MutableStateFlow(chartColorSettingsRepository.load())

    val uiState: StateFlow<WeightUiState> = combine(
        records,
        selectedGraphRange,
        formState,
        chartColorSettings
    ) { currentRecords, graphRange, currentForm, currentChartColorSettings ->
        val allDailyChartData = WeightChartAggregator.buildDaily(
            records = currentRecords,
            movingAverageDays = currentChartColorSettings.movingAverageDays
        )
        WeightUiState(
            records = currentRecords,
            latestClothesWeight = currentRecords.firstOrNull()?.clothesWeight ?: 0.0,
            selectedGraphRange = graphRange,
            dailyChartData = filterDailyChartData(allDailyChartData, graphRange),
            chartColorSettings = currentChartColorSettings,
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
                        clothesWeightInput = formatDefaultClothesWeightInput(latestClothesWeight),
                        clothesWeightUsesDefaultPlaceholder = latestClothesWeight == 0.0,
                        netWeightPreview = calculateNetWeightPreview(
                            measuredWeightInput = current.measuredWeightInput,
                            clothesWeightInput = formatDefaultClothesWeightInput(latestClothesWeight)
                        )
                    )
                }
            }
        }
    }

    fun updateMeasuredWeight(input: String) {
        val previousInput = formState.value.measuredWeightInput
        updateForm {
            copy(
                measuredWeightInput = normalizeMeasuredWeightInput(
                    previousInput = previousInput,
                    newInput = input
                ),
                measuredWeightUsesDefaultPlaceholder = false,
                measuredWeightError = null,
                generalError = null
            )
        }
    }

    fun clearMeasuredWeightDefaultOnFocus() {
        val currentForm = formState.value
        if (
            currentForm.isEditing ||
            !currentForm.measuredWeightUsesDefaultPlaceholder ||
            currentForm.measuredWeightInput != DEFAULT_NEW_MEASURED_WEIGHT_INPUT
        ) {
            return
        }

        updateForm {
            copy(
                measuredWeightInput = "",
                measuredWeightUsesDefaultPlaceholder = false,
                measuredWeightError = null,
                generalError = null
            )
        }
    }

    fun restoreMeasuredWeightDefaultOnBlur() {
        val currentForm = formState.value
        if (currentForm.measuredWeightInput.isNotBlank()) {
            return
        }

        updateForm {
            copy(
                measuredWeightInput = DEFAULT_ZERO_INPUT,
                measuredWeightUsesDefaultPlaceholder = true,
                measuredWeightError = null,
                generalError = null
            )
        }
    }

    fun updateClothesWeight(input: String) {
        updateForm {
            copy(
                clothesWeightInput = WeightTrackerFormatters.normalizeDecimalInput(input),
                clothesWeightUsesDefaultPlaceholder = false,
                clothesWeightError = null,
                generalError = null
            )
        }
    }

    fun clearClothesWeightDefaultOnFocus() {
        val currentForm = formState.value
        if (
            !currentForm.clothesWeightUsesDefaultPlaceholder ||
            currentForm.clothesWeightInput != DEFAULT_ZERO_INPUT
        ) {
            return
        }

        updateForm {
            copy(
                clothesWeightInput = "",
                clothesWeightUsesDefaultPlaceholder = false,
                clothesWeightError = null,
                generalError = null
            )
        }
    }

    fun restoreClothesWeightDefaultOnBlur() {
        val currentForm = formState.value
        if (currentForm.clothesWeightInput.isNotBlank()) {
            return
        }

        updateForm {
            copy(
                clothesWeightInput = DEFAULT_ZERO_INPUT,
                clothesWeightUsesDefaultPlaceholder = true,
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

    fun updateRecordLineColor(colorArgb: Int?) {
        chartColorSettingsRepository.saveRecordLineColor(colorArgb)
        chartColorSettings.update { current ->
            current.copy(recordLineColorArgb = colorArgb)
        }
    }

    fun updateMovingAverageLineColor(colorArgb: Int) {
        chartColorSettingsRepository.saveMovingAverageLineColor(colorArgb)
        chartColorSettings.update { current ->
            current.copy(movingAverageLineColorArgb = colorArgb)
        }
    }

    fun updateMovingAverageDays(days: Int) {
        val normalizedDays = days.coerceAtLeast(1)
        chartColorSettingsRepository.saveMovingAverageDays(normalizedDays)
        chartColorSettings.update { current ->
            current.copy(movingAverageDays = normalizedDays)
        }
    }

    fun startEditing(record: WeightRecord) {
        formState.value = RecordFormState(
            recordId = record.id,
            date = record.date,
            measuredWeightInput = WeightTrackerFormatters.formatInput(record.measuredWeight),
            measuredWeightUsesDefaultPlaceholder = false,
            clothesWeightInput = WeightTrackerFormatters.formatInput(record.clothesWeight),
            clothesWeightUsesDefaultPlaceholder = false,
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
            (
                currentForm.measuredWeightInput.isBlank() ||
                    currentForm.measuredWeightInput == DEFAULT_NEW_MEASURED_WEIGHT_INPUT
                ) &&
            currentForm.clothesWeightInput == formatDefaultClothesWeightInput(previousLatestClothesWeight)
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

        var measuredError: Int? = null
        var clothesError: Int? = null
        var generalError: Int? = null

        if (measuredWeight == null) {
            measuredError = R.string.validation_measured_weight_required
        }

        if (clothesWeight == null) {
            clothesError = R.string.validation_clothes_weight_required
        } else if (clothesWeight !in 0.0..10.0) {
            clothesError = R.string.validation_clothes_weight_range
        }

        if (measuredWeight != null && clothesWeight != null && measuredWeight - clothesWeight <= 0.0) {
            generalError = R.string.validation_net_weight_positive
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

    private fun normalizeMeasuredWeightInput(
        previousInput: String,
        newInput: String
    ): String {
        val normalized = WeightTrackerFormatters.normalizeDecimalInput(newInput)
        if (previousInput != DEFAULT_NEW_MEASURED_WEIGHT_INPUT) {
            return normalized
        }

        return when {
            normalized.isBlank() -> normalized
            normalized == DEFAULT_NEW_MEASURED_WEIGHT_INPUT -> normalized
            normalized.startsWith("0.") -> normalized
            normalized.startsWith(".") -> "0$normalized"
            else -> normalized.trimStart('0').ifEmpty { DEFAULT_NEW_MEASURED_WEIGHT_INPUT }
        }
    }

    private fun resetForm(defaultClothesWeight: Double) {
        val clothesWeightInput = formatDefaultClothesWeightInput(defaultClothesWeight)
        formState.value = RecordFormState(
            date = LocalDate.now(),
            measuredWeightInput = DEFAULT_NEW_MEASURED_WEIGHT_INPUT,
            measuredWeightUsesDefaultPlaceholder = true,
            clothesWeightInput = clothesWeightInput,
            clothesWeightUsesDefaultPlaceholder = defaultClothesWeight == 0.0,
            netWeightPreview = calculateNetWeightPreview(
                measuredWeightInput = DEFAULT_NEW_MEASURED_WEIGHT_INPUT,
                clothesWeightInput = clothesWeightInput
            ),
            isEditing = false
        )
    }

    private fun formatDefaultClothesWeightInput(value: Double): String {
        return if (value == 0.0) {
            DEFAULT_ZERO_INPUT
        } else {
            WeightTrackerFormatters.formatInput(value)
        }
    }

    class Factory(
        application: Application
    ) : ViewModelProvider.Factory {
        private val repository = WeightRecordRepository(
            WeightTrackerDatabase.getInstance(application).weightRecordDao()
        )
        private val chartColorSettingsRepository = ChartColorSettingsRepository(application)

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeightTrackerViewModel::class.java)) {
                return WeightTrackerViewModel(
                    repository = repository,
                    chartColorSettingsRepository = chartColorSettingsRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
