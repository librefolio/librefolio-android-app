package com.librefolio.main.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonIOException
import com.librefolio.main.api.PortfolioConstants
import com.librefolio.main.api.PortfolioService
import com.librefolio.main.persistence.EquityInfo
import com.librefolio.main.persistence.EquityDatabase
import com.librefolio.main.persistence.EquityInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The main ViewModel connecting Equity data persistence to the UI. Also makes the Portfolio API
 * network call.
 *
 * @property equityDatabase The EquityDatabase, usually from Hilt.
 * @property portfolioService The Portfolio API client, usually from Hilt.
 * @param equityInfoRepository The EquityInfo Repository, to help simplify the uiState logic.
 */
@HiltViewModel
class LibreFolioViewModel @Inject constructor(
    private val equityDatabase: EquityDatabase,
    private val portfolioService: PortfolioService,
    equityInfoRepository: EquityInfoRepository
) : ViewModel() {
    companion object {
        val LOG_TAG = LibreFolioViewModel::class.simpleName

        const val UI_STATE_STOP_TIMEOUT = 5000L
    }

    // This non-mutable State declaration is simplified inline here via a data Repository pattern.
    val uiState: StateFlow<List<EquityInfo>> = equityInfoRepository.equityInfoAll().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(UI_STATE_STOP_TIMEOUT), emptyList()
    )

    /**
     * Update the database with the ViewModel Coroutine scope by making the Portfolio API network
     * call and replacing the Room Database table with it.
     */
    fun updateDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i(LOG_TAG, "Portfolio request initiating.")

            val response = try {
                portfolioService.getPortfolio()
            } catch (e: JsonIOException) {
                Log.e(LOG_TAG, "JSON parsing exception: $e")
                return@launch
            }

            if (response?.isSuccessful == true) {
                // Verbose log, as this may contain sensitive data
                Log.v(
                    LOG_TAG,
                    "Response: --${response.body()?.toString()}--"
                )

                response.body()
                        ?.get(PortfolioConstants.STOCKS_FIELD)
                        ?.asJsonArray?.mapIndexedNotNull { index, stock ->
                    stock?.asJsonObject?.let { stockJsonObject ->
                        EquityInfo.getEquityFromStockJsonObject(
                            // The primary key is thus simply the index within the list.
                            index.toString(),
                            stockJsonObject
                        )
                    }
                }?.let {
                    if (it.isNotEmpty()) {
                        Log.d(
                            LOG_TAG,
                            "Inserting ${it.size} records into the Equity Database."
                        )

                        // Blindly clear all data, as we aren't using a unique primary key.
                        equityDatabase.equityDao().deleteAllAndInsertAll(it)
                    } else {
                        Log.e(LOG_TAG, "Response contained no entries.")
                    }
                } ?: run {
                    Log.e(LOG_TAG, "Could not parse response.")
                }
            } else {
                Log.e(LOG_TAG, "Response was unsuccessful.")
            }
        }
    }
}