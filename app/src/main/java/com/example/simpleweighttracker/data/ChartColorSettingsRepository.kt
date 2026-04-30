package com.example.simpleweighttracker.data

import android.content.Context
import com.example.simpleweighttracker.model.ChartColorSettings
import com.example.simpleweighttracker.model.DefaultMovingAverageDays
import com.example.simpleweighttracker.model.DefaultMovingAverageLineColorArgb

class ChartColorSettingsRepository(
    context: Context
) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun load(): ChartColorSettings {
        return ChartColorSettings(
            recordLineColorArgb = if (preferences.contains(KEY_RECORD_LINE_COLOR)) {
                preferences.getInt(KEY_RECORD_LINE_COLOR, 0)
            } else {
                null
            },
            movingAverageLineColorArgb = preferences.getInt(
                KEY_MOVING_AVERAGE_LINE_COLOR,
                DefaultMovingAverageLineColorArgb
            ),
            movingAverageDays = preferences.getInt(
                KEY_MOVING_AVERAGE_DAYS,
                DefaultMovingAverageDays
            ).coerceAtLeast(1)
        )
    }

    fun saveRecordLineColor(colorArgb: Int?) {
        val editor = preferences.edit()
        if (colorArgb == null) {
            editor.remove(KEY_RECORD_LINE_COLOR)
        } else {
            editor.putInt(KEY_RECORD_LINE_COLOR, colorArgb)
        }
        editor.apply()
    }

    fun saveMovingAverageLineColor(colorArgb: Int) {
        preferences.edit()
            .putInt(KEY_MOVING_AVERAGE_LINE_COLOR, colorArgb)
            .apply()
    }

    fun saveMovingAverageDays(days: Int) {
        preferences.edit()
            .putInt(KEY_MOVING_AVERAGE_DAYS, days.coerceAtLeast(1))
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "chart_color_settings"
        const val KEY_RECORD_LINE_COLOR = "record_line_color"
        const val KEY_MOVING_AVERAGE_LINE_COLOR = "moving_average_line_color"
        const val KEY_MOVING_AVERAGE_DAYS = "moving_average_days"
    }
}
