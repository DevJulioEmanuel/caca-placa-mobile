package com.prefeitura.cacaplaca.feature.auth.data

import com.prefeitura.cacaplaca.core.session.SessionManager
import org.json.JSONObject
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

class AuthRepository(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) {

    private fun extractErrorDetail(response: Response<*>): String {
        val rawError = response.errorBody()?.string()
        return try {
            if (rawError != null) {
                val jsonObject = JSONObject(rawError)
                if (jsonObject.has("detail")) {
                    jsonObject.getString("detail")
                } else {
                    "Erro no servidor (Código: ${response.code()})."
                }
            } else {
                "Erro no servidor (Código: ${response.code()})."
            }
        } catch (e: Exception) {
            "Erro no servidor (Código: ${response.code()})."
        }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    @Suppress("UNCHECKED_CAST")
                    Result.success(Unit as T)
                }
            } else {
                Result.failure(Exception(extractErrorDetail(response)))
            }
        } catch (e: ConnectException) {
            Result.failure(Exception("Não foi possível conectar ao servidor. Verifique a sua conexão com a internet."))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("O tempo limite de conexão esgotou. Tente novamente."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return safeApiCall { api.login(username, password) }.onSuccess { body ->
            sessionManager.saveToken(body.accessToken)
            // Após logar com sucesso, busca os dados do usuário e salva o nome
            fetchCurrentUser()
        }
    }

    suspend fun fetchCurrentUser(): Result<UserResponse> {
        return safeApiCall { api.getMe() }.onSuccess { user ->
            sessionManager.saveUserName(user.nome)
        }
    }

    suspend fun forgotPassword(email: String): Result<Unit> {
        return safeApiCall { api.forgotPassword(ForgotPasswordRequest(email)) }
    }
}