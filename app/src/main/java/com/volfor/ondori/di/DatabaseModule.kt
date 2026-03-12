package com.volfor.ondori.di

import android.content.Context
import androidx.room.Room
import com.volfor.ondori.data.local.db.OndoriDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): OndoriDatabase {
        return Room.databaseBuilder(
            context.applicationContext, OndoriDatabase::class.java, "Ondori.db"
        ).build()
    }
}
