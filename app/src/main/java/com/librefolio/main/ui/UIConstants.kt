package com.librefolio.main.ui

import com.librefolio.main.R

/**
 * Constants for the UI.
 */
class UIConstants {
    companion object {
        const val HOME_NAVIGATION_ROUTE = "home"
        const val ABOUT_NAVIGATION_ROUTE = "about"

        const val EMPTY_STRING = ""
        const val ZERO_AMOUNT = 0

        const val SUPPORTED_CURRENCY = "USD"
        const val CENTS_PER_DOLLAR = 100
        const val CURRENCY_DISPLAY_UNITS = 2

        fun getAllRowNames(): List<Int> {
            return listOf(
                R.string.name_row_name,
                R.string.price_row_name,
                R.string.amount_row_name
            )
        }
    }
}