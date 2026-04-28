package com.example.simpleweighttracker.ui

import com.example.simpleweighttracker.model.DailyWeightPoint
import com.example.simpleweighttracker.model.PeriodWeightPoint
import com.example.simpleweighttracker.model.WeightRecord
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

object WeightChartAggregator {
    fun buildDaily(records: List<WeightRecord>): List<DailyWeightPoint> {
        val dailyMinimums = records
            .groupBy(WeightRecord::date)
            .map { (date, items) ->
                DailyWeightPoint(
                    date = date,
                    netWeight = items.minOf(WeightRecord::netWeight),
                    movingAverage = null
                )
            }
            .sortedBy(DailyWeightPoint::date)

        return dailyMinimums.mapIndexed { index, point ->
            val movingAverage = if (index >= 6) {
                val slice = dailyMinimums.subList(index - 6, index + 1)
                slice.map(DailyWeightPoint::netWeight).average()
            } else {
                null
            }

            point.copy(movingAverage = movingAverage?.let(WeightTrackerFormatters::roundToTwoDecimals))
        }
    }

    fun buildWeekly(records: List<WeightRecord>): List<PeriodWeightPoint> {
        return records
            .groupBy { it.date.startOfWeek() }
            .map { (weekStart, items) ->
                PeriodWeightPoint(
                    periodStart = weekStart,
                    netWeight = items.minOf(WeightRecord::netWeight)
                )
            }
            .sortedBy(PeriodWeightPoint::periodStart)
    }

    fun buildMonthly(records: List<WeightRecord>): List<PeriodWeightPoint> {
        return records
            .groupBy { YearMonth.from(it.date) }
            .map { (yearMonth, items) ->
                PeriodWeightPoint(
                    periodStart = yearMonth.atDay(1),
                    netWeight = items.minOf(WeightRecord::netWeight)
                )
            }
            .sortedBy(PeriodWeightPoint::periodStart)
    }
}

private fun LocalDate.startOfWeek(): LocalDate =
    with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
