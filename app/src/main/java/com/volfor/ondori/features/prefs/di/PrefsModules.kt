package com.volfor.ondori.features.prefs.di

import com.volfor.ondori.features.prefs.data.repositories.AppPreferencesRepositoryImpl
import com.volfor.ondori.features.prefs.domain.repositories.AppPreferencesRepository
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
    abstract fun bindAppPreferencesRepository(repository: AppPreferencesRepositoryImpl): AppPreferencesRepository
}