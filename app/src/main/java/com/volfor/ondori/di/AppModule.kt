package com.volfor.ondori.di

import com.volfor.ondori.app.ui.SnackbarManager
import com.volfor.ondori.app.ui.SnackbarManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun bindSnackbarManager(snackbarManagerImpl: SnackbarManagerImpl): SnackbarManager
}
