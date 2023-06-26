package com.librefolio.main.persistence

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.JsonObject
import com.librefolio.main.api.PortfolioConstants
import org.jetbrains.annotations.VisibleForTesting
import java.lang.ClassCastException

/**
 * Equity Info, mapped straight from the Portfolio API call for storage in the Room Database table.
 *
 * @property equityId The Primary Key of the table.
 * @property ticker This is the ticker symbol for a given stock.
 * @property name The name of the company the ticker belongs to.
 * @property currency The denominating currency for this stock.
 * @property currentPriceCents The currently trading price for this specific stock in USD cents.
 * @property quantity The amount of this stock that is currently in the portfolio.
 * @property currentPriceTimestamp A Unix timestamp represented in UTC from when the current price
 *                                 was last calculated.
 */
@Entity
data class EquityInfo(
    @PrimaryKey val equityId: String,
    @ColumnInfo(name = TICKER_COLUMN_NAME) val ticker: String,
    @ColumnInfo(name = NAME_COLUMN_NAME) val name: String,
    @ColumnInfo(name = CURRENCY_COLUMN_NAME) val currency: String,
    @ColumnInfo(name = PRICE_COLUMN_NAME) val currentPriceCents: Int,
    @ColumnInfo(name = QUANTITY_COLUMN_NAME) val quantity: Int,
    @ColumnInfo(name = TIMESTAMP_COLUMN_NAME) val currentPriceTimestamp: Int
) {
    companion object {
        private val LOG_TAG = EquityInfo::class.simpleName

        const val TICKER_COLUMN_NAME = "ticker"
        const val NAME_COLUMN_NAME = "name"
        const val CURRENCY_COLUMN_NAME = "currency"
        const val PRICE_COLUMN_NAME = "currentPriceCents"
        const val QUANTITY_COLUMN_NAME = "quantity"
        const val TIMESTAMP_COLUMN_NAME = "currentPriceTimestamp"

        @VisibleForTesting
        const val QUANTITY_DEFAULT_VALUE = 0

        /**
         * A static constructor for mapping the Portfolio "stocks" field JsonObject into the
         * EquityInfo object that will be stored in the Room Database table.
         *
         * @param equityId The Primary Key of the entry.
         * @param stockJsonObject The "stocks" Json Object.
         * @return The EquityInfo object for persistence.
         */
        fun getEquityFromStockJsonObject(equityId: String, stockJsonObject: JsonObject): EquityInfo? {
            // If these fields are null, return a null EquityInfo object
            listOf(
                PortfolioConstants.TICKER_FIELD,
                PortfolioConstants.NAME_FIELD,
                PortfolioConstants.CURRENCY_FIELD,
                PortfolioConstants.PRICE_FIELD,
                PortfolioConstants.TIMESTAMP_FIELD
            ).forEach { field ->
                stockJsonObject.get(field) ?: run {
                    Log.e(LOG_TAG, "For equity id $equityId, the field $field is null.")
                    return null
                }
            }

            // If these fields are not integers, return a null EquityInfo object
            try {
                listOf(
                    PortfolioConstants.PRICE_FIELD,
                    PortfolioConstants.TIMESTAMP_FIELD
                ).forEach { field ->
                    stockJsonObject.get(field)!!.asInt
                }
            } catch (e: ClassCastException) {
                Log.e(LOG_TAG, "For equity id $equityId, an int field was invalid: $e.")
                return null
            }

            // If quantity is not null and not an integer, return a null EquityInfo object
            val quantity = try {
                stockJsonObject.get(PortfolioConstants.QUANTITY_FIELD)?.let {
                    if (!it.isJsonNull) {
                        it.asInt
                    } else {
                        QUANTITY_DEFAULT_VALUE
                    }
                } ?: QUANTITY_DEFAULT_VALUE
            } catch (e: ClassCastException) {
                Log.e(LOG_TAG, "For equity id $equityId, the quantity field was invalid: $e.")
                return null
            }

            return EquityInfo(
                equityId,
                stockJsonObject
                        .get(PortfolioConstants.TICKER_FIELD)
                        ?.asString!!,
                stockJsonObject
                        .get(PortfolioConstants.NAME_FIELD)
                        ?.asString!!,
                stockJsonObject
                        .get(PortfolioConstants.CURRENCY_FIELD)
                        ?.asString!!,
                stockJsonObject
                        .get(PortfolioConstants.PRICE_FIELD)
                        ?.asInt!!,
                quantity,
                stockJsonObject
                        .get(PortfolioConstants.TIMESTAMP_FIELD)
                        ?.asInt!!
            )
        }
    }
}