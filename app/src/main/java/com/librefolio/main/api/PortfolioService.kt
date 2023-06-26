package com.librefolio.main.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET

/**
 * Retrofit API function declaration for the Portfolio service.
 */
interface PortfolioService {
    /**
     * Retrofit API function declaration to get the portfolio.
     *
     * Note simply returning JsonObject may handle null poorly
     * See: https://github.com/square/retrofit/issues/3075
     *
     * @return The Response with JsonObject.
     */
    @GET(PortfolioConstants.MAIN_ENDPOINT)
    suspend fun getPortfolio(): Response<JsonObject?>?
}