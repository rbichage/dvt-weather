package com.reuben.core_di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@InstallIn(SingletonComponent::class)
@Module
object CoroutinesModule {

    @CoroutineIODispatcher
    @Provides
    fun provideIODispatcher() = Dispatchers.IO

    @Provides
    fun provideMainDispatcher() = Dispatchers.Main

    @Provides
    fun provideDefaultDispatcher() = Dispatchers.Default


}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CoroutineIODispatcher