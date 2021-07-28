package com.dvt.weatherforecast.di

import android.content.Context
import com.dvt.weatherforecast.R
import com.dvt.weatherforecast.network.ApiService
import com.dvt.weatherforecast.utils.network.loggingInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    private var BASE_URL = "api.openweathermap.org/data/2.5/"

    @Provides
    @WeatherApiKey
    fun provideApiKey(@ApplicationContext context: Context): String =
        context.getString(R.string.api_key)

    @Provides
    @Singleton
    fun provideHttpClient(@WeatherApiKey apiKey: String): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .addInterceptor { chain ->
                val request = chain.request()
                val authRequest = request.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("fcm", apiKey)
                    .build()


                return@addInterceptor chain.proceed(authRequest)
            }
            .addNetworkInterceptor(loggingInterceptor)
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class WeatherApiKey
}