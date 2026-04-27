package com.volfor.ondori.features.alarm.di

import android.app.AlarmManager
import android.content.Context
import com.volfor.ondori.data.local.db.OndoriDatabase
import com.volfor.ondori.features.alarm.data.local.db.dao.AlarmDao
import com.volfor.ondori.features.alarm.data.repositories.AlarmRepositoryImpl
import com.volfor.ondori.features.alarm.data.services.AlarmRingerImpl
import com.volfor.ondori.features.alarm.data.services.AlarmSchedulerImpl
import com.volfor.ondori.features.alarm.data.services.AlarmTimeCalculatorImpl
import com.volfor.ondori.features.alarm.data.services.MissedAlarmNotifierImpl
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import com.volfor.ondori.features.alarm.domain.services.MissedAlarmNotifier
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmModule {
    @Singleton
    @Binds
    abstract fun bindAlarmScheduler(alarmScheduler: AlarmSchedulerImpl): AlarmScheduler

    @Singleton
    @Binds
    abstract fun bindAlarmRinger(alarmRinger: AlarmRingerImpl): AlarmRinger

    @Singleton
    @Binds
    abstract fun bindAlarmTimeCalculator(timeCalculator: AlarmTimeCalculatorImpl): AlarmTimeCalculator

    @Singleton
    @Binds
    abstract fun bindAlarmRepository(repository: AlarmRepositoryImpl): AlarmRepository

    @Singleton
    @Binds
    abstract fun bindMissedAlarmNotifier(notifier: MissedAlarmNotifierImpl): MissedAlarmNotifier

    companion object {
        @Provides
        @Singleton
        fun provideAlarmManager(
            @ApplicationContext context: Context
        ): AlarmManager {
            return context.applicationContext.getSystemService(AlarmManager::class.java)
        }

        @Provides
        @Singleton
        fun provideAlarmDao(database: OndoriDatabase): AlarmDao = database.alarmDao()
    }
}