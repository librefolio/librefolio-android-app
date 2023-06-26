package com.librefolio.main.persistence

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.librefolio.main.api.PortfolioConstants
import com.librefolio.main.persistence.EquityInfo.Companion.QUANTITY_DEFAULT_VALUE
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.ClassCastException

@RunWith(RobolectricTestRunner::class)
class EquityInfoTest {
    companion object {
        private const val EQUITY_ID = "equityId"

        private const val TICKER = "ticker"
        private const val NAME = "name"
        private const val CURRENCY = "currency"
        private const val PRICE = 2
        private const val QUANTITY = 2
        private const val TIMESTAMP = 10000
    }

    private val stockJsonObject = mockk<JsonObject>()
    private val tickerJsonElement = mockk<JsonElement>()
    private val nameJsonElement = mockk<JsonElement>()
    private val currencyJsonElement = mockk<JsonElement>()
    private val priceJsonElement = mockk<JsonElement>()
    private val quantityJsonElement = mockk<JsonElement>()
    private val timestampJsonElement = mockk<JsonElement>()

    @Before
    fun before() {
        every { stockJsonObject.get(PortfolioConstants.TICKER_FIELD) } returns tickerJsonElement
        every { tickerJsonElement.asString } returns TICKER

        every { stockJsonObject.get(PortfolioConstants.NAME_FIELD) } returns nameJsonElement
        every { nameJsonElement.asString } returns NAME

        every { stockJsonObject.get(PortfolioConstants.CURRENCY_FIELD) } returns currencyJsonElement
        every { currencyJsonElement.asString } returns CURRENCY

        every { stockJsonObject.get(PortfolioConstants.PRICE_FIELD) } returns priceJsonElement
        every { priceJsonElement.asInt } returns PRICE

        every { stockJsonObject.get(PortfolioConstants.QUANTITY_FIELD) } returns quantityJsonElement
        every { quantityJsonElement.asInt } returns QUANTITY
        every { quantityJsonElement.isJsonNull } returns false

        every { stockJsonObject.get(PortfolioConstants.TIMESTAMP_FIELD) } returns timestampJsonElement
        every { timestampJsonElement.asInt } returns TIMESTAMP
    }

    @Test
    fun test_happyPath() {
        val equityInfo = EquityInfo.getEquityFromStockJsonObject(EQUITY_ID, stockJsonObject)

        assertEquals(EQUITY_ID, equityInfo?.equityId)
        assertEquals(TICKER, equityInfo?.ticker)
        assertEquals(NAME, equityInfo?.name)
        assertEquals(CURRENCY, equityInfo?.currency)
        assertEquals(PRICE, equityInfo?.currentPriceCents)
        assertEquals(QUANTITY, equityInfo?.quantity)
        assertEquals(TIMESTAMP, equityInfo?.currentPriceTimestamp)
    }

    @Test
    fun test_tickerFieldMissing() {
        every { stockJsonObject.get(PortfolioConstants.TICKER_FIELD) } returns null
        val equityInfo = EquityInfo.getEquityFromStockJsonObject(EQUITY_ID, stockJsonObject)
        Assert.assertNull(equityInfo)
    }

    @Test
    fun test_nameFieldMissing() {
        every { stockJsonObject.get(PortfolioConstants.NAME_FIELD) } returns null
        val equityInfo = EquityInfo.getEquityFromStockJsonObject(EQUITY_ID, stockJsonObject)
        Assert.assertNull(equityInfo)
    }

    @Test
    fun test_currencyFieldMissing() {
        every { stockJsonObject.get(PortfolioConstants.CURRENCY_FIELD) } returns null
        val equityInfo = EquityInfo.getEquityFromStockJsonObject(EQUITY_ID, stockJsonObject)
        Assert.assertNull(equityInfo)
    }

    @Test
    fun test_priceFieldMissing() {
        every { stockJsonObject.get(PortfolioConstants.PRICE_FIELD) } returns null
        val equityInfo = EquityInfo.getEquityFromStockJsonObject(EQUITY_ID, stockJsonObject)
        Assert.assertNull(equityInfo)
    }

    @Test
    fun test_priceFieldNonInt() {
        every { priceJsonElement.asInt } throws ClassCastException()
        val equityInfo = EquityInfo.getEquityFromStockJsonObject(EQUITY_ID, stockJsonObject)
        Assert.assertNull(equityInfo)
    }

    @Test
    fun test_quantityFieldMissing() {
        every { stockJsonObject.get(PortfolioConstants.QUANTITY_FIELD) } returns null
        val equityInfo = EquityInfo.getEquityFromStockJsonObject(EQUITY_ID, stockJsonObject)

        assertEquals(EQUITY_ID, equityInfo?.equityId)
        assertEquals(TICKER, equityInfo?.ticker)
        assertEquals(NAME, equityInfo?.name)
        assertEquals(CURRENCY, equityInfo?.currency)
        assertEquals(PRICE, equityInfo?.currentPriceCents)
        assertEquals(QUANTITY_DEFAULT_VALUE, equityInfo?.quantity)
        assertEquals(TIMESTAMP, equityInfo?.currentPriceTimestamp)
    }

    @Test
    fun test_quantityFieldNull() {
        every { quantityJsonElement.isJsonNull } returns true
        val equityInfo = EquityInfo.getEquityFromStockJsonObject(EQUITY_ID, stockJsonObject)

        assertEquals(EQUITY_ID, equityInfo?.equityId)
        assertEquals(TICKER, equityInfo?.ticker)
        assertEquals(NAME, equityInfo?.name)
        assertEquals(CURRENCY, equityInfo?.currency)
        assertEquals(PRICE, equityInfo?.currentPriceCents)
        assertEquals(QUANTITY_DEFAULT_VALUE, equityInfo?.quantity)
        assertEquals(TIMESTAMP, equityInfo?.currentPriceTimestamp)
    }

    @Test
    fun test_quantityFieldNonInt() {
        every { quantityJsonElement.asInt } throws ClassCastException()
        val equityInfo = EquityInfo.getEquityFromStockJsonObject(EQUITY_ID, stockJsonObject)
        Assert.assertNull(equityInfo)
    }

    @Test
    fun test_timestampFieldMissing() {
        every { stockJsonObject.get(PortfolioConstants.TIMESTAMP_FIELD) } returns null
        val equityInfo = EquityInfo.getEquityFromStockJsonObject(EQUITY_ID, stockJsonObject)
        Assert.assertNull(equityInfo)
    }

    @Test
    fun test_timestampFieldNonInt() {
        every { timestampJsonElement.asInt } throws ClassCastException()
        val equityInfo = EquityInfo.getEquityFromStockJsonObject(EQUITY_ID, stockJsonObject)
        Assert.assertNull(equityInfo)
    }
}