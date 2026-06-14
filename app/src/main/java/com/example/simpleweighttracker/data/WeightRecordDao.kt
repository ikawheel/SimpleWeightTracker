package com.ikeansoft.simpleweighttracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightRecordDao {
    @Query("SELECT * FROM weight_records ORDER BY date DESC, createdAt DESC")
    fun observeAll(): Flow<List<WeightRecordEntity>>

    @Query("SELECT * FROM weight_records ORDER BY date DESC, createdAt DESC LIMIT 1")
    suspend fun getLatestRecord(): WeightRecordEntity?

    @Insert
    suspend fun insert(record: WeightRecordEntity): Long

    @Update
    suspend fun update(record: WeightRecordEntity)

    @Delete
    suspend fun delete(record: WeightRecordEntity)
}
