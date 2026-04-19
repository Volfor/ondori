package com.volfor.ondori.features.settings.di

import com.volfor.ondori.features.settings.data.repositories.SettingsRepositoryImpl
import com.volfor.ondori.features.settings.domain.repositories.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PrefsModule {

    @Singleton
    @Binds
    abstract fun bindAppPreferencesRepository(repository: SettingsRepositoryImpl): SettingsRepository
}