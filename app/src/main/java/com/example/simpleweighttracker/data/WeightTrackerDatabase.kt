package com.example.simpleweighttracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [WeightRecordEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateConverters::class)
abstract class WeightTrackerDatabase : RoomDatabase() {
    abstract fun weightRecordDao(): WeightRecordDao

    companion object {
        @Volatile
        private var instance: WeightTrackerDatabase? = null

        fun getInstance(context: Context): WeightTrackerDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    WeightTrackerDatabase::class.java,
                    "weight_tracker.db"
                ).build().also { database ->
                    instance = database
                }
            }
        }
    }
}
