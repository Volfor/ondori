package com.volfor.ondori.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.volfor.ondori.data.local.db.OndoriDatabase
import com.volfor.ondori.features.punisher.data.local.db.entities.UserProfileEntity
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
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "INSERT OR IGNORE INTO user_profile (id, score, lastDismissedAlarmTime) VALUES (${UserProfileEntity.SINGLETON_ID}, 0, NULL)"
                )
            }
        }).build()
    }
}
