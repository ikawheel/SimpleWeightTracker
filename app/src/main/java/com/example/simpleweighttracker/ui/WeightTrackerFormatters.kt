package com.example.simpleweighttracker.ui

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.round

object WeightTrackerFormatters {
    private val decimalSymbols = DecimalFormatSymbols(Locale.US)
    private val weightFormatter = DecimalFormat("0.0#", decimalSymbols)
    private val fullDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    private val shortDateFormatter = DateTimeFormatter.ofPattern("M/d")
    private val monthFormatter = DateTimeFormatter.ofPattern("yyyy/MM")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun normalizeDecimalInput(input: String): String {
        val builder = StringBuilder()
        var decimalInserted = false

        input.forEach { char ->
            when {
                char.isDigit() -> builder.append(char)
                (char == '.' || char == ',') && !decimalInserted -> {
                    builder.append('.')
                    decimalInserted = true
                }
            }
        }

        return builder.toString()
    }

    fun parseDecimal(input: String): Double? = input.trim().replace(',', '.').toDoubleOrNull()

    fun formatWeight(value: Double): String = "${weightFormatter.format(value)} kg"

    fun formatValue(value: Double): String = weightFormatter.format(value)

    fun formatInput(value: Double): String = weightFormatter.format(value)

    fun formatFullDate(date: LocalDate): String = date.format(fullDateFormatter)

    fun formatShortDate(date: LocalDate): String = date.format(shortDateFormatter)

    fun formatWeekLabel(date: LocalDate): String = "${formatShortDate(date)}週"

    fun formatMonth(date: LocalDate): String = date.format(monthFormatter)

    fun formatTime(epochMillis: Long): String =
        Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
            .format(timeFormatter)

    fun roundToTwoDecimals(value: Double): Double = round(value * 100.0) / 100.0

    fun localDateToUtcMillis(date: LocalDate): Long =
        date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    fun utcMillisToLocalDate(utcMillis: Long): LocalDate =
        Instant.ofEpochMilli(utcMillis).atZone(ZoneOffset.UTC).toLocalDate()
}
