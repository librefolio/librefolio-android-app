package com.librefolio.main.api

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Hilt/Dagger module to provide the Porfolio service API client.
 *
 * @constructor Create empty Portfolio service module
 */
@Module
@InstallIn(SingletonComponent::class)
class PortfolioServiceModule {
    /**
     * Provide the Portfolio service API client using Retrofit.Builder.
     *
     * @return A Portfolio service API client Retrofit singleton.
     */
    @Singleton
    @Provides
    fun providePortfolioService(): PortfolioService {
        val httpClient = OkHttpClient.Builder()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(PortfolioConstants.GOOGLE_API_ENDPOINT)
            .client(httpClient.build())
            .build()

        return retrofit.create(PortfolioService::class.java)
    }
}