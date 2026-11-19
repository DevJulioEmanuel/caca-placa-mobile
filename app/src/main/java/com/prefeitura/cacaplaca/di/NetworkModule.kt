package com.prefeitura.cacaplaca.di

import com.prefeitura.cacaplaca.core.session.SessionManager
import com.prefeitura.cacaplaca.feature.auth.data.AuthApi
import com.prefeitura.cacaplaca.feature.registration.data.api.RegistrationApi
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val BASE_URL = "https://cacaplaca-api.duckdns.org/"

val networkModule = module {

    single {
        val dynamicUrlInterceptor = Interceptor { chain ->
            val sessionManager = GlobalContext.get().get<SessionManager>()
            var request = chain.request()
            val customUrl = sessionManager.getBaseUrl()
            
            if (!customUrl.isNullOrEmpty()) {
                val formattedUrl = if (!customUrl.startsWith("http://") && !customUrl.startsWith("https://")) {
                    "https://$customUrl"
                } else {
                    customUrl
                }
                
                val newBaseUrl = formattedUrl.toHttpUrlOrNull()
                if (newBaseUrl != null) {
                    val newUrl = request.url.newBuilder()
                        .scheme(newBaseUrl.scheme)
                        .host(newBaseUrl.host)
                        .port(newBaseUrl.port)
                        .build()
                    request = request.newBuilder().url(newUrl).build()
                }
            }
            chain.proceed(request)
        }

        val authInterceptor = Interceptor { chain ->
            val sessionManager = GlobalContext.get().get<SessionManager>()
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
            level = HttpLoggingInterceptor.Level.HEADERS
        }

        val tokenAuthenticator = com.prefeitura.cacaplaca.core.network.TokenAuthenticator()

        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(dynamicUrlInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(logging)
            .build()
    }

    single(org.koin.core.qualifier.named("bareOkHttp")) {
        val dynamicUrlInterceptor = Interceptor { chain ->
            val sessionManager = GlobalContext.get().get<SessionManager>()
            var request = chain.request()
            val customUrl = sessionManager.getBaseUrl()
            
            if (!customUrl.isNullOrEmpty()) {
                val formattedUrl = if (!customUrl.startsWith("http://") && !customUrl.startsWith("https://")) {
                    "https://$customUrl"
                } else {
                    customUrl
                }
                
                val newBaseUrl = formattedUrl.toHttpUrlOrNull()
                if (newBaseUrl != null) {
                    val newUrl = request.url.newBuilder()
                        .scheme(newBaseUrl.scheme)
                        .host(newBaseUrl.host)
                        .port(newBaseUrl.port)
                        .build()
                    request = request.newBuilder().url(newUrl).build()
                }
            }
            chain.proceed(request)
        }

        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(dynamicUrlInterceptor)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single(org.koin.core.qualifier.named("refreshApi")) {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get(org.koin.core.qualifier.named("bareOkHttp")))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    single { get<Retrofit>().create(AuthApi::class.java) }
    single { get<Retrofit>().create(RegistrationApi::class.java) }
}