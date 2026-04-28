package com.example.simpleweighttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.simpleweighttracker.model.WeightRecord
import java.time.LocalDate

@Entity(tableName = "weight_records")
data class WeightRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val measuredWeight: Double,
    val clothesWeight: Double,
    val netWeight: Double,
    val createdAt: Long,
    val updatedAt: Long
)

fun WeightRecordEntity.toModel(): WeightRecord = WeightRecord(
    id = id,
    date = date,
    measuredWeight = measuredWeight,
    clothesWeight = clothesWeight,
    netWeight = netWeight,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun WeightRecord.toEntity(): WeightRecordEntity = WeightRecordEntity(
    id = id,
    date = date,
    measuredWeight = measuredWeight,
    clothesWeight = clothesWeight,
    netWeight = netWeight,
    createdAt = createdAt,
    updatedAt = updatedAt
)
