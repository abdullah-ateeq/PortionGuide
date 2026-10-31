package com.example.portionguide.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CalculationEntity::class], version = 1, exportSchema = false)
abstract class PortionDatabase : RoomDatabase() {

    abstract fun calculationDao(): CalculationDao

    companion object {
        @Volatile
        private var INSTANCE: PortionDatabase? = null

        fun getInstance(context: Context): PortionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PortionDatabase::class.java,
                    "portion_guide.db"
                ).fallbackToDestructiveMigration(dropAllTables = false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
