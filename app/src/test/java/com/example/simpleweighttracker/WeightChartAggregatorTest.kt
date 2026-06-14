package com.ikeansoft.simpleweighttracker

import com.ikeansoft.simpleweighttracker.model.WeightRecord
import com.ikeansoft.simpleweighttracker.ui.WeightChartAggregator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class WeightChartAggregatorTest {
    @Test
    fun buildDaily_usesDailyMinimums_andStartsMovingAverageFromSeventhPoint() {
        val startDate = LocalDate.of(2026, 4, 1)
        val records = listOf(
            record(date = startDate, netWeight = 70.4, createdAt = 1),
            record(date = startDate, netWeight = 69.9, createdAt = 2),
            record(date = startDate.plusDays(1), netWeight = 69.8, createdAt = 3),
            record(date = startDate.plusDays(2), netWeight = 70.1, createdAt = 4),
            record(date = startDate.plusDays(3), netWeight = 69.7, createdAt = 5),
            record(date = startDate.plusDays(4), netWeight = 69.6, createdAt = 6),
            record(date = startDate.plusDays(5), netWeight = 69.5, createdAt = 7),
            record(date = startDate.plusDays(6), netWeight = 69.4, createdAt = 8),
            record(date = startDate.plusDays(7), netWeight = 69.2, createdAt = 9)
        )

        val result = WeightChartAggregator.buildDaily(records)

        assertEquals(8, result.size)
        assertEquals(69.9, result.first().netWeight, 0.0)
        assertNull(result[5].movingAverage)
        assertEquals(69.71, result[6].movingAverage ?: 0.0, 0.0)
        assertEquals(69.61, result[7].movingAverage ?: 0.0, 0.0)
    }

    @Test
    fun buildWeekly_groupsByMondayAndUsesMinimumWithinWeek() {
        val records = listOf(
            record(date = LocalDate.of(2026, 4, 6), netWeight = 70.0, createdAt = 1),
            record(date = LocalDate.of(2026, 4, 7), netWeight = 69.7, createdAt = 2),
            record(date = LocalDate.of(2026, 4, 12), netWeight = 69.9, createdAt = 3),
            record(date = LocalDate.of(2026, 4, 13), netWeight = 69.4, createdAt = 4),
            record(date = LocalDate.of(2026, 4, 15), netWeight = 69.6, createdAt = 5)
        )

        val result = WeightChartAggregator.buildWeekly(records)

        assertEquals(2, result.size)
        assertEquals(LocalDate.of(2026, 4, 6), result[0].periodStart)
        assertEquals(69.7, result[0].netWeight, 0.0)
        assertEquals(LocalDate.of(2026, 4, 13), result[1].periodStart)
        assertEquals(69.4, result[1].netWeight, 0.0)
    }

    @Test
    fun buildMonthly_usesMonthlyMinimum() {
        val records = listOf(
            record(date = LocalDate.of(2026, 4, 1), netWeight = 70.1, createdAt = 1),
            record(date = LocalDate.of(2026, 4, 12), netWeight = 69.4, createdAt = 2),
            record(date = LocalDate.of(2026, 4, 28), netWeight = 69.9, createdAt = 3),
            record(date = LocalDate.of(2026, 5, 2), netWeight = 69.2, createdAt = 4)
        )

        val result = WeightChartAggregator.buildMonthly(records)

        assertEquals(2, result.size)
        assertEquals(LocalDate.of(2026, 4, 1), result[0].periodStart)
        assertEquals(69.4, result[0].netWeight, 0.0)
        assertEquals(LocalDate.of(2026, 5, 1), result[1].periodStart)
        assertEquals(69.2, result[1].netWeight, 0.0)
    }

    private fun record(
        date: LocalDate,
        netWeight: Double,
        createdAt: Long
    ): WeightRecord = WeightRecord(
        id = createdAt,
        date = date,
        measuredWeight = netWeight + 0.5,
        clothesWeight = 0.5,
        netWeight = netWeight,
        createdAt = createdAt,
        updatedAt = createdAt
    )
}
