package com.reuben.core_di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @WeatherApiKey
    @Singleton
    fun provideApiKey(@ApplicationContext context: Context): String = context.getString(com.reuben.core_ui.R.string.api_key)

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WeatherApiKey