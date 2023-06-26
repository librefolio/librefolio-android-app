package com.librefolio.main.persistence

import kotlinx.coroutines.flow.Flow

/**
 * EquityInfo Repository, a pattern used here to help simplify the logic in the ViewModel.
 *
 * @property equityDatabase The Equity Room Database object.
 */
open class EquityInfoRepository(
    private val equityDatabase: EquityDatabase
) {
    /**
     * Get the EquityInfo List in an asynchronous Flow by making the Dao query getAll().
     *
     * @return The EquityInfo List Flow
     */
    open fun equityInfoAll(): Flow<List<EquityInfo>> {
        return equityDatabase.equityDao().getAll()
    }
}