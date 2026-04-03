package com.volfor.ondori.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.volfor.ondori.data.local.datastore.appDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideAppPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.applicationContext.appDataStore
    }
}
