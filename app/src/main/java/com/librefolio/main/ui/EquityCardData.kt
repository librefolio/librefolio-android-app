package com.librefolio.main.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.librefolio.main.ui.UIConstants.Companion.CENTS_PER_DOLLAR
import com.librefolio.main.ui.UIConstants.Companion.CURRENCY_DISPLAY_UNITS
import com.librefolio.main.ui.UIConstants.Companion.EMPTY_STRING
import com.librefolio.main.ui.UIConstants.Companion.SUPPORTED_CURRENCY
import com.librefolio.main.ui.UIConstants.Companion.ZERO_AMOUNT
import com.librefolio.main.R
import com.librefolio.main.persistence.EquityInfo
import java.text.NumberFormat
import java.time.Duration
import java.time.Instant
import java.util.Currency

/**
 * Equity card display data, for the UX. Requires some Composable functions to get string resources
 * inline.
 *
 * @property ticker The equity ticker.
 * @property name The equity friendly name.
 * @property formattedPrice The formatted current price, in USD, e.g. "$123.45".
 * @property timeAgo The number of second(s), minute(s), hour(s), or day(s) ago it was updated, or "just now".
 * @property amountUnits The number of equity units owned in readable text, e.g. "10 Units".
 * @property amountFormattedPrice The actual amount owned in USD, e.g. "$1234.50".
 */
data class EquityCardData(
    val ticker: String,
    val name: String,
    val formattedPrice: String,
    val timeAgo: String,
    val amountUnits: String,
    val amountFormattedPrice: String
) {
    companion object {
        private val LOG_TAG = EquityCardData::class.simpleName

        /**
         * Time ago formatted, e.g. "5 minute(s) ago"
         *
         * @param timeAgoTimestamp The timestamp in seconds since the epoch when the equity was updated.
         * @return The formatted timeAgo string.
         */
        @Composable
        private fun timeAgoFormatted(timeAgoTimestamp: Long): String {
            val timeAgo = Duration.between(
                Instant.ofEpochSecond(timeAgoTimestamp),
                Instant.now()
            )

            listOf(
                Pair(timeAgo.toDays(), R.string.days_ago),
                Pair(timeAgo.toHours(), R.string.hours_ago),
                Pair(timeAgo.toMinutes(), R.string.minutes_ago),
                Pair(timeAgo.seconds, R.string.seconds_ago)
            ).forEach {
                if (it.first >= 1) {
                    return "${it.first} ${stringResource(it.second)}"
                }
            }

            return stringResource(R.string.just_now)
        }

        /**
         * Price formatted, e.g. "$123.45"
         *
         * @param priceUnformatted The unformatted price in Cents, an Integer
         * @return The formatted price string
         */
        private fun priceFormatted(priceUnformatted: Int): String {
            val format: NumberFormat = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = CURRENCY_DISPLAY_UNITS
            format.currency = Currency.getInstance(SUPPORTED_CURRENCY)

            return format.format(priceUnformatted.toFloat() / CENTS_PER_DOLLAR)
        }

        /**
         * Amount formatted string, e.g. "$1234.50" or empty if 0.
         *
         * @param amountUnformatted The unformatted amount string, and Integer
         * @return The amount formatted string
         */
        private fun amountFormatted(amountUnformatted: Int): String {
            if (amountUnformatted == ZERO_AMOUNT) {
                return EMPTY_STRING
            }

            return priceFormatted(amountUnformatted)
        }

        /**
         * Amount units formatted string, e.g. "10 Units" or empty if 0.
         *
         * @param quantity
         * @return
         */
        @Composable
        private fun amountUnitsFormatted(quantity: Int): String {
            if (quantity == ZERO_AMOUNT) {
                return EMPTY_STRING
            }

            return "$quantity ${stringResource(R.string.units)}"
        }

        /**
         * Get the EquityCardData for UX mapped from an EquityInfo object from the Room Database.
         *
         * @param equityInfo EquityInfo object from the Room Database.
         * @return The EquityCardData for UX
         */
        @Composable
        fun fromEquityInfo(equityInfo: EquityInfo): EquityCardData? {
            if (equityInfo.currency != SUPPORTED_CURRENCY) {
                Log.w(LOG_TAG, "Currency ${equityInfo.currency} not yet supported.")
                return null
            }

            return EquityCardData(
                equityInfo.ticker,
                equityInfo.name,
                priceFormatted(equityInfo.currentPriceCents),
                timeAgoFormatted(equityInfo.currentPriceTimestamp.toLong()),
                amountUnitsFormatted(equityInfo.quantity),
                amountFormatted(equityInfo.quantity * equityInfo.currentPriceCents),
            )
        }
    }
}