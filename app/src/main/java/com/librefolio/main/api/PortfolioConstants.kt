package com.librefolio.main.api

/**
 * Portfolio API constants
 */
class PortfolioConstants {
    companion object {
        const val GOOGLE_API_ENDPOINT = "https://storage.googleapis.com"

        const val MAIN_ENDPOINT = "/cash-homework/cash-stocks-api/portfolio.json"
        const val MALFORMED_ENDPOINT = "/cash-homework/cash-stocks-api/portfolio_malformed.json"
        const val EMPTY_ENDPOINT = "/cash-homework/cash-stocks-api/portfolio_empty.json"

        const val STOCKS_FIELD = "stocks"
        const val TICKER_FIELD = "ticker"
        const val NAME_FIELD = "name"
        const val CURRENCY_FIELD = "currency"
        const val PRICE_FIELD = "current_price_cents"
        const val QUANTITY_FIELD = "quantity"
        const val TIMESTAMP_FIELD = "current_price_timestamp"
    }
}