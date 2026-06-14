package com.ikeansoft.simpleweighttracker.ui.settings

import androidx.annotation.StringRes
import com.ikeansoft.simpleweighttracker.R

internal data class MovingAverageDaysInputState(
    val value: Int?,
    @StringRes val errorResId: Int?
)

internal fun normalizeMovingAverageDaysInput(input: String): String {
    return input.filter { char -> char.isDigit() }
}

internal fun buildMovingAverageDaysInputState(input: String): MovingAverageDaysInputState {
    val parsedDays = input.toIntOrNull()
    val errorResId = if (parsedDays == null || parsedDays < 1) {
        R.string.validation_moving_average_days
    } else {
        null
    }

    return MovingAverageDaysInputState(
        value = parsedDays,
        errorResId = errorResId
    )
}
