package com.librefolio.main.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Equity Room Database main class
 */
@Database(entities = [EquityInfo::class], version = 1, exportSchema = false)
abstract class EquityDatabase : RoomDatabase() {
    /**
     * Get the main Equity Database Dao
     *
     * @return The Equity Dao
     */
    abstract fun equityDao(): EquityDao
}