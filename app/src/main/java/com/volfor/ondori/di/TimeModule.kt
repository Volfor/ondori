package com.volfor.ondori.di

import com.volfor.ondori.core.time.SystemTimeFormatStore
import com.volfor.ondori.core.time.TimeFormatStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeModule {

    companion object {
        @Singleton
        @Provides
        fun provideClock(): Clock = Clock.systemDefaultZone()
    }

    @Singleton
    @Binds
    abstract fun bindTimeFormatStore(timeFormatStore: SystemTimeFormatStore): TimeFormatStore
}