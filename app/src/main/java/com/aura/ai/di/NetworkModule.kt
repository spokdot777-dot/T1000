package com.aura.ai.di

import com.aura.ai.data.remote.ollama.OllamaApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        // Read timeout is intentionally generous and streaming-friendly; the
        // per-request timeout is enforced at the repository layer using the
        // user's configured value via withTimeout(...).
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideOllamaApi(client: OkHttpClient, json: Json): OllamaApi {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            // Placeholder base URL; every call supplies an absolute @Url so the
            // host is fully user-configurable at runtime.
            .baseUrl("http://localhost/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(OllamaApi::class.java)
    }
}
