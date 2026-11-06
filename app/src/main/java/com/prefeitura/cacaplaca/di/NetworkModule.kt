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

val BASE_URL = "http://192.168.100.27:8000/"

val networkModule = module {

    single {
        val dynamicUrlInterceptor = Interceptor { chain ->
            val sessionManager = GlobalContext.get().get<SessionManager>()
            var request = chain.request()
            val customUrl = sessionManager.getBaseUrl()
            
            if (!customUrl.isNullOrEmpty()) {
                val formattedUrl = if (!customUrl.startsWith("http://") && !customUrl.startsWith("https://")) {
                    "http://$customUrl"
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

        OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .addInterceptor(dynamicUrlInterceptor)
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