package com.librefolio.main.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Equity Room Database dao
 */
@Dao
abstract class EquityDao {
    /**
     * Get all EquityInfo from the table as an asynchronous Flow.
     *
     * @return The EquityInfo List Flow.
     */
    @Query("SELECT * FROM equityInfo")
    abstract fun getAll(): Flow<List<EquityInfo>>

    /**
     * Insert all EquityInfo asynchronously
     *
     * @param equities List of EquityInfo
     */
    @Insert
    abstract suspend fun insertAll(equities: List<EquityInfo>)

    /**
     * Delete all EquityInfo from the table asynchronously
     */
    @Query("DELETE FROM equityInfo")
    abstract suspend fun deleteAll()

    /**
     * Atomically replace the entire table with the List of EquityInfo provided, asynchronously.
     *
     * @param equities List of EquityInfo
     */
    @Transaction
    open suspend fun deleteAllAndInsertAll(equities: List<EquityInfo>) {
        deleteAll()
        insertAll(equities)
    }
}