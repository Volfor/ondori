package com.volfor.ondori.features.punisher.di

import com.volfor.ondori.data.local.db.OndoriDatabase
import com.volfor.ondori.features.punisher.data.local.db.dao.UserProfileDao
import com.volfor.ondori.features.punisher.data.repositories.PunisherRepositoryImpl
import com.volfor.ondori.features.punisher.domain.repositories.PunisherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PunisherModule {

    @Singleton
    @Binds
    abstract fun bindPunisherRepository(repository: PunisherRepositoryImpl): PunisherRepository

    companion object {
        @Provides
        @Singleton
        fun provideUserProfileDao(database: OndoriDatabase): UserProfileDao =
            database.userProfileDao()
    }
}