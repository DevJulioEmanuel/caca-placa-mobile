package com.prefeitura.cacaplaca.core.network

import com.prefeitura.cacaplaca.core.session.SessionManager
import com.prefeitura.cacaplaca.feature.auth.data.AuthApi
import com.prefeitura.cacaplaca.feature.auth.data.RefreshRequest
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.koin.core.context.GlobalContext

class TokenAuthenticator : Authenticator {

    // Lazy injection to avoid circular dependency loops when building OkHttpClient
    private val sessionManager: SessionManager by lazy {
        GlobalContext.get().get<SessionManager>()
    }

    // We will inject a bare Retrofit AuthApi specifically for Refresh token,
    // which DOES NOT use this authenticator to avoid infinite loops.
    private val authApi: AuthApi by lazy {
        GlobalContext.get().get<AuthApi>(org.koin.core.qualifier.named("refreshApi"))
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        val originalRequest = response.request
        val path = originalRequest.url.encodedPath

        // If the request that failed is login or refresh, we don't try to refresh again
        if (path.contains("auth/login") || path.contains("auth/refresh")) {
            return null
        }

        // Avoid infinite loop if we already retried this request multiple times
        if (responseCount(response) >= 2) {
            return null
        }

        synchronized(this) {
            val currentToken = sessionManager.getToken()
            val requestHeaderToken = originalRequest.header("Authorization")?.replace("Bearer ", "")

            // If the token in the session is DIFFERENT from the one that failed,
            // it means another thread already refreshed the token. Just retry with the new one.
            if (currentToken != null && requestHeaderToken != currentToken) {
                return originalRequest.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshToken = sessionManager.getRefreshToken()
            if (refreshToken.isNullOrBlank()) {
                sessionManager.clearSession()
                return null
            }

            // Sync API Call for Refreshing token
            try {
                val refreshResponse = authApi.refreshToken(RefreshRequest(refreshToken)).execute()

                if (refreshResponse.isSuccessful) {
                    refreshResponse.body()?.let { loginResponse ->
                        sessionManager.saveToken(loginResponse.accessToken)
                        sessionManager.saveRefreshToken(loginResponse.refreshToken)
                        return originalRequest.newBuilder()
                            .header("Authorization", "Bearer ${loginResponse.accessToken}")
                            .build()
                    }
                } else {
                    // Refresh token failed (e.g. expired or invalid)
                    sessionManager.clearSession()
                    return null
                }
            } catch (e: Exception) {
                // If it fails due to network, we can either return null or let it be handled
                return null
            }
        }
        return null
    }

    private fun responseCount(response: Response?): Int {
        var result = 1
        var priorResponse = response?.priorResponse
        while (priorResponse != null) {
            result++
            priorResponse = priorResponse.priorResponse
        }
        return result
    }
}
