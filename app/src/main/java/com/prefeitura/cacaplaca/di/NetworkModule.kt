package com.prefeitura.cacaplaca.di

import com.prefeitura.cacaplaca.data.ServerConfigManager
import com.prefeitura.cacaplaca.feature.auth.data.AuthApi
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { ServerConfigManager(androidContext()) }

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val serverConfigManager = get<ServerConfigManager>()

        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                val dynamicUrl = serverConfigManager.getBaseUrl().toHttpUrlOrNull()
                if (dynamicUrl != null) {
                    val newUrl = request.url.newBuilder()
                        .scheme(dynamicUrl.scheme)
                        .host(dynamicUrl.host)
                        .port(dynamicUrl.port)
                        .build()
                    val newRequest = request.newBuilder()
                        .url(newUrl)
                        .build()
                    chain.proceed(newRequest)
                } else {
                    chain.proceed(request)
                }
            }
            .addInterceptor(logging)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("http://localhost/") // This is overridden by the interceptor
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(AuthApi::class.java) }
}