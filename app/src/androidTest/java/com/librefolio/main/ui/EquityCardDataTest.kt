package com.librefolio.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import com.librefolio.main.ui.UIConstants.Companion.SUPPORTED_CURRENCY
import com.librefolio.main.persistence.EquityInfo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

/**
 * EquityCardData test. This is an instrumentation test since we require Composable functions and
 * string resources for the UX display.
 */
@RunWith(AndroidJUnit4::class)
class EquityCardDataTest {

    companion object {
        const val EQUITY_ID = "equityId"
        const val TICKER = "ticker"
        const val NAME = "name"
        const val CURRENCY = SUPPORTED_CURRENCY
        const val CURRENT_PRICE_CENTS = 100
        const val QUANTITY = 10
        const val TIMESTAMP = 12345

        const val TICKER_TAG = "tickerTag"
        const val NAME_TAG = "nameTag"
        const val FORMATTED_PRICE_TAG = "formattedPriceTag"
        const val TIME_AGO_TAG = "timeAgoTag"
        const val AMOUNT_FORMATTED_PRICE_TAG = "amountFormattedPrice"
        const val AMOUNT_UNITS_TAG = "amountUnitsTag"
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun getEquityInfo(): EquityInfo =
        EquityInfo(
            EQUITY_ID,
            TICKER,
            NAME,
            CURRENCY,
            CURRENT_PRICE_CENTS,
            QUANTITY,
            TIMESTAMP
        )

    @Composable
    private fun TestEquityCardDataColumn(equityInfo: EquityInfo) {
        return EquityCardData.fromEquityInfo(
            equityInfo = equityInfo
        )?.let { equityCardData ->
            Column {
                Text(text = equityCardData.ticker, Modifier.testTag(TICKER_TAG))
                Text(text = equityCardData.name, Modifier.testTag(NAME_TAG))
                Text(text = equityCardData.formattedPrice, Modifier.testTag(FORMATTED_PRICE_TAG))
                Text(text = equityCardData.timeAgo, Modifier.testTag(TIME_AGO_TAG))
                Text(
                    text = equityCardData.amountFormattedPrice,
                    Modifier.testTag(AMOUNT_FORMATTED_PRICE_TAG)
                )
                Text(text = equityCardData.amountUnits, Modifier.testTag(AMOUNT_UNITS_TAG))
            }
        } ?: run {
            Column {}
        }
    }

    @Test
    fun test_happyPath() {
        composeTestRule.setContent {
            TestEquityCardDataColumn(getEquityInfo())
        }

        composeTestRule.onNodeWithText(TICKER).assertIsDisplayed()
        composeTestRule.onNodeWithText(NAME).assertIsDisplayed()
        composeTestRule.onNodeWithTag(FORMATTED_PRICE_TAG).assertTextEquals("$1.00")
        composeTestRule.onNodeWithTag(TIME_AGO_TAG).assertTextContains(" day(s) ago", true)
        composeTestRule.onNodeWithTag(AMOUNT_FORMATTED_PRICE_TAG).assertTextEquals("$10.00")
        composeTestRule.onNodeWithTag(AMOUNT_UNITS_TAG).assertTextEquals("10 Units")
    }

    @Test
    fun test_unsupportedCurrency() {
        composeTestRule.setContent {
            TestEquityCardDataColumn(getEquityInfo().copy(currency = "EURO"))
        }

        composeTestRule.onNodeWithTag(TICKER).assertDoesNotExist()
        composeTestRule.onNodeWithTag(NAME).assertDoesNotExist()
        composeTestRule.onNodeWithTag(FORMATTED_PRICE_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TIME_AGO_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(AMOUNT_FORMATTED_PRICE_TAG).assertDoesNotExist()
        composeTestRule.onNodeWithTag(AMOUNT_UNITS_TAG).assertDoesNotExist()
    }

    @Test
    fun test_quantityZero() {
        composeTestRule.setContent {
            TestEquityCardDataColumn(getEquityInfo().copy(quantity = 0))
        }

        composeTestRule.onNodeWithText(TICKER).assertIsDisplayed()
        composeTestRule.onNodeWithText(NAME).assertIsDisplayed()
        composeTestRule.onNodeWithTag(FORMATTED_PRICE_TAG).assertTextEquals("$1.00")
        composeTestRule.onNodeWithTag(TIME_AGO_TAG).assertTextContains(" day(s) ago", true)
        composeTestRule.onNodeWithTag(AMOUNT_FORMATTED_PRICE_TAG).assertTextEquals("")
        composeTestRule.onNodeWithTag(AMOUNT_UNITS_TAG).assertTextEquals("")
    }

    @Test
    fun test_priceZero() {
        composeTestRule.setContent {
            TestEquityCardDataColumn(getEquityInfo().copy(currentPriceCents = 0))
        }

        composeTestRule.onNodeWithText(TICKER).assertIsDisplayed()
        composeTestRule.onNodeWithText(NAME).assertIsDisplayed()
        composeTestRule.onNodeWithTag(FORMATTED_PRICE_TAG).assertTextEquals("$0.00")
        composeTestRule.onNodeWithTag(TIME_AGO_TAG).assertTextContains(" day(s) ago", true)
        composeTestRule.onNodeWithTag(AMOUNT_FORMATTED_PRICE_TAG).assertTextEquals("")
        composeTestRule.onNodeWithTag(AMOUNT_UNITS_TAG).assertTextEquals("10 Units")
    }

    // Test could be flaky, possibly if it takes more than 1 hour to execute. This is unlikely.
    @Test
    @FlakyTest
    fun test_updatedRecently() {
        composeTestRule.setContent {
            TestEquityCardDataColumn(getEquityInfo().copy(currentPriceTimestamp = Instant.now().epochSecond.toInt() - 3601))
        }
        composeTestRule.onNodeWithText(TICKER).assertIsDisplayed()
        composeTestRule.onNodeWithText(NAME).assertIsDisplayed()
        composeTestRule.onNodeWithTag(FORMATTED_PRICE_TAG).assertTextEquals("$1.00")
        composeTestRule.onNodeWithTag(TIME_AGO_TAG).assertTextContains(" hour(s) ago", true)
        composeTestRule.onNodeWithTag(AMOUNT_FORMATTED_PRICE_TAG).assertTextEquals("$10.00")
        composeTestRule.onNodeWithTag(AMOUNT_UNITS_TAG).assertTextEquals("10 Units")
    }
}