package com.rkbapps.autoreply.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rkbapps.autoreply.data.AutoReplyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAutoReplyDatabase(
        @ApplicationContext context: Context
    ): AutoReplyDatabase {
        return Room.databaseBuilder(
            context,
            AutoReplyDatabase::class.java,
            "auto_reply_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAutoReplyDao(database: AutoReplyDatabase) = database.dao

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }


}