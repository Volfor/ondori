package com.volfor.ondori.features.alarm.di

import android.app.AlarmManager
import android.content.Context
import com.volfor.ondori.data.local.db.OndoriDatabase
import com.volfor.ondori.features.alarm.data.local.db.dao.AlarmDao
import com.volfor.ondori.features.alarm.data.repositories.AlarmRepositoryImpl
import com.volfor.ondori.features.alarm.data.scheduler.AlarmSchedulerImpl
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
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
        return context.applicationContext.getSystemService(AlarmManager::class.java)
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
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAlarmRepository(repository: AlarmRepositoryImpl): AlarmRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun provideAlarmDao(database: OndoriDatabase): AlarmDao = database.alarmDao()
}