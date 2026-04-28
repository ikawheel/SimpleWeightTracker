package com.example.simpleweighttracker.data

import com.example.simpleweighttracker.model.WeightRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeightRecordRepository(
    private val dao: WeightRecordDao
) {
    fun observeAll(): Flow<List<WeightRecord>> = dao.observeAll().map { records ->
        records.map(WeightRecordEntity::toModel)
    }

    suspend fun getLatestRecord(): WeightRecord? = dao.getLatestRecord()?.toModel()

    suspend fun insert(record: WeightRecord): Long = dao.insert(record.toEntity())

    suspend fun update(record: WeightRecord) = dao.update(record.toEntity())

    suspend fun delete(record: WeightRecord) = dao.delete(record.toEntity())

    suspend fun deleteAll() = dao.deleteAll()
}
