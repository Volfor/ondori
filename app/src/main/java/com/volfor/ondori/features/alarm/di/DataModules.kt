package com.volfor.ondori.features.alarm.di

import android.app.AlarmManager
import android.content.Context
import com.volfor.ondori.features.alarm.data.AlarmSchedulerImpl
import com.volfor.ondori.features.alarm.data.datasources.AlarmFakeLocalDataSourceImpl
import com.volfor.ondori.features.alarm.data.datasources.AlarmLocalDataSource
import com.volfor.ondori.features.alarm.data.repositories.AlarmRepositoryImpl
import com.volfor.ondori.features.alarm.domain.AlarmScheduler
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmManagerModule {

    @Provides
    @Singleton
    fun provideAlarmManager(
        @ApplicationContext context: Context
    ): AlarmManager {
        return context.getSystemService(AlarmManager::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmModule {

    @Singleton
    @Binds
    abstract fun bindAlarmScheduler(alarmScheduler: AlarmSchedulerImpl): AlarmScheduler
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmLocalDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindAlarmLocalDataSource(localDataSource: AlarmFakeLocalDataSourceImpl): AlarmLocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAlarmRepository(repository: AlarmRepositoryImpl): AlarmRepository
}