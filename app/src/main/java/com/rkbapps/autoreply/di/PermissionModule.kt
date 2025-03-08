package com.rkbapps.autoreply.di

import android.content.Context
import com.rkbapps.autoreply.manager.PermissionManager
import com.rkbapps.autoreply.manager.PermissionManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PermissionModule {

    @Provides
    @Singleton
    fun providePermissionManager(
        @ApplicationContext context: Context
    ): PermissionManager{
        return PermissionManagerImpl(context);
    }
}