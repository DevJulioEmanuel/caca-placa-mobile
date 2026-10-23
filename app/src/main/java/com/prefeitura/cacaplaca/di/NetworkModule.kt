package com.prefeitura.cacaplaca.di

import com.prefeitura.cacaplaca.core.session.SessionManager
import com.prefeitura.cacaplaca.feature.auth.data.AuthApi
import com.prefeitura.cacaplaca.feature.registration.data.RegistrationApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor

val BASE_URL = "http://192.168.0.12:8000/"
val networkModule = module {

    single {
        val sessionManager = get<SessionManager>()

        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val token = sessionManager.getToken()

            if (!token.isNullOrEmpty()) {
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(originalRequest)
            }
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(AuthApi::class.java) }
    single { get<Retrofit>().create(RegistrationApi::class.java) }
}