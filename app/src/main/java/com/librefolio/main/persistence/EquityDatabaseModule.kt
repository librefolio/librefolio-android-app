package com.librefolio.main.persistence

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt/Dagger module providing the Equity Room Database and related persistence instances.
 */
@Module
@InstallIn(SingletonComponent::class)
class EquityDatabaseModule {
    companion object {
        const val EQUITY_DATABASE_NAME = "equity-database"
    }

    /**
     * Provide the main Equity Database singleton
     *
     * @param applicationContext Android application context, required for the Room builder.
     * @return The Equity Database singleton
     */
    @Singleton
    @Provides
    fun provideEquityDatabase(
        @ApplicationContext applicationContext: Context
    ): EquityDatabase {
        return Room.databaseBuilder(
            applicationContext,
            EquityDatabase::class.java,
            EQUITY_DATABASE_NAME
        ).build()
    }

    /**
     * Provide the EquityInfo repository, an abstraction for getting all data to simplify the
     * ViewModel.
     *
     * @param equityDatabase The Equity Database singleton
     * @return The EquityInfo repository
     */
    @Singleton
    @Provides
    fun provideEquityInfoRepository(
        equityDatabase: EquityDatabase
    ): EquityInfoRepository {
        return EquityInfoRepository(equityDatabase)
    }
}