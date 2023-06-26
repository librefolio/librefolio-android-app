package com.librefolio.main.viewmodel

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonIOException
import com.google.gson.JsonObject
import com.librefolio.main.api.PortfolioConstants
import com.librefolio.main.api.PortfolioService
import com.librefolio.main.persistence.EquityDao
import com.librefolio.main.persistence.EquityDatabase
import com.librefolio.main.persistence.EquityInfo
import com.librefolio.main.persistence.EquityInfoRepository
import io.mockk.Called
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

private class FakeEquityInfoRepository(
    equityDatabase: EquityDatabase
) : EquityInfoRepository(
    equityDatabase
) {
    private val flow = MutableSharedFlow<List<EquityInfo>>()
    suspend fun emit(value: List<EquityInfo>) = flow.emit(value)
    override fun equityInfoAll(): Flow<List<EquityInfo>> = flow
}

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class LibreFolioViewModelTest {
    companion object {
        private const val EQUITY_ID = "equityId"
        private const val TICKER = "ticker"
        private const val NAME = "name"
        private const val CURRENCY = "currency"
        private const val PRICE = 2
        private const val QUANTITY = 2
        private const val TIMESTAMP = 10000
    }

    private val equityDatabase = mockk<EquityDatabase>()
    private val portfolioService = mockk<PortfolioService>()
    private val responseJsonObject = mockk<Response<JsonObject?>>()
    private val jsonObject = mockk<JsonObject>()
    private val jsonStocksElement = mockk<JsonElement>()
    private val jsonStocksArray = JsonArray()
    private val jsonStocksElement1 = mockk<JsonElement>()
    private val jsonStocksObject1 = mockk<JsonObject>()
    private val equityDao = mockk<EquityDao>()

    private val equityInfo = EquityInfo(
        EQUITY_ID,
        TICKER,
        NAME,
        CURRENCY,
        PRICE,
        QUANTITY,
        TIMESTAMP
    )

    private val listEquities = listOf(equityInfo)
    private val fakeEquityInfoRepository = FakeEquityInfoRepository(equityDatabase)
    private val viewModel = LibreFolioViewModel(
        equityDatabase,
        portfolioService,
        fakeEquityInfoRepository
    )

    @Before
    fun before() = runTest {
        coEvery { portfolioService.getPortfolio() } returns responseJsonObject
        every { responseJsonObject.isSuccessful } returns true
        every { responseJsonObject.body() } returns jsonObject
        every { jsonObject.get(PortfolioConstants.STOCKS_FIELD) } returns jsonStocksElement
        every { jsonStocksElement.asJsonArray } returns jsonStocksArray
        jsonStocksArray.add(jsonStocksElement1)
        every { jsonStocksElement1.asJsonObject } returns jsonStocksObject1

        mockkObject(EquityInfo)
        every { EquityInfo.getEquityFromStockJsonObject(any(), any()) } returns equityInfo

        every { equityDatabase.equityDao() } returns equityDao

        coEvery { equityDao.deleteAllAndInsertAll(any()) } just Runs

        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun after() = runTest {
        Dispatchers.resetMain()
    }

    @Test
    fun test_viewModelUiStateSet() = runTest {
        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        assertEquals(0, viewModel.uiState.value.size)

        fakeEquityInfoRepository.emit(listEquities)

        assertEquals(listEquities, viewModel.uiState.value)
    }

    @Test
    fun test_updateDatabase_happyPath() {
        viewModel.updateDatabase()

        coVerify(exactly = 1) { equityDao.deleteAllAndInsertAll(listEquities) }
    }

    @Test
    fun test_updateDatabase_responseNull() {
        coEvery { portfolioService.getPortfolio() } returns null

        viewModel.updateDatabase()

        verify { equityDao wasNot Called }
    }

    @Test
    fun test_updateDatabase_responseMalformed() {
        coEvery { portfolioService.getPortfolio() } throws JsonIOException("JSON document was not fully consumed.")

        viewModel.updateDatabase()

        verify { equityDao wasNot Called }
    }

    @Test
    fun test_updateDatabase_responseFailed() {
        every { responseJsonObject.isSuccessful } returns false

        viewModel.updateDatabase()

        verify { equityDao wasNot Called }
    }

    @Test
    fun test_updateDatabase_responseBodyNull() {
        every { responseJsonObject.body() } returns null

        viewModel.updateDatabase()

        verify { equityDao wasNot Called }
    }

    @Test
    fun test_updateDatabase_responseStocksFieldNull() {
        every { jsonObject.get(PortfolioConstants.STOCKS_FIELD) } returns null

        viewModel.updateDatabase()

        verify { equityDao wasNot Called }
    }

    @Test
    fun test_updateDatabase_responseStocksArrayEmpty() {
        every { jsonStocksElement.asJsonArray } returns JsonArray()

        viewModel.updateDatabase()

        verify { equityDao wasNot Called }
    }

    @Test
    fun test_updateDatabase_responseStockJsonObjectNull() {
        every { jsonStocksElement1.asJsonObject } returns null

        viewModel.updateDatabase()

        verify { equityDao wasNot Called }
    }

    @Test
    fun test_updateDatabase_responseEquityInfoInvalid() {
        every { EquityInfo.getEquityFromStockJsonObject(any(), any()) } returns null

        viewModel.updateDatabase()

        verify { equityDao wasNot Called }
    }
}