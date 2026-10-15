package com.prefeitura.cacaplaca.feature.auth.data

import com.prefeitura.cacaplaca.core.session.SessionManager
import kotlinx.coroutines.delay
import org.json.JSONObject
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

class MockAuthRepository(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {

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

    override suspend fun login(username: String, password: String): Result<LoginResponse> {
        delay(1000)
        return if (username == "joao@exemplo.com" && password == "string") {
            val mockToken = "mock_token_12345"
            sessionManager.saveToken(mockToken)
            Result.success(LoginResponse(mockToken, "bearer"))
        } else {
            Result.failure(Exception("Credenciais inválidas no Mock. Use joao@exemplo.com / string"))
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val response = api.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful) {
                Result.success(Unit)
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

    override suspend fun resetPassword(token: String, newPassword: String): Result<Unit> {
        return try {
            val response = api.resetPassword(token, ResetPasswordRequest(newPassword))
            if (response.isSuccessful) {
                Result.success(Unit)
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
}