package com.reuben.core_testing.util

import com.google.common.io.Resources
import com.reuben.core_network.api.ApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

fun getJson(path: String): String {

    val uri = Resources.getResource(path)
    val file = File(uri.path)

    return String(file.readBytes())
}

val genericError = "{\"message\": \"This is just another error lorem ipsum\"}"

const val genericErrorContent = "This is just another error lorem ipsum"

//TODO: Move this
val testOkhttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()


fun testApiService(mockWebServer: MockWebServer) = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(testOkhttpClient)
        .addConverterFactory(MoshiConverterFactory.create(testMoshi))
        .build()
        .create(ApiService::class.java)


val testMoshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()