package com.prefeitura.cacaplaca.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prefeitura.cacaplaca.core.session.SessionManager
import com.prefeitura.cacaplaca.feature.auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    object Success : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}

sealed class ResetPasswordState {
    object Idle : ResetPasswordState()
    object Loading : ResetPasswordState()
    object Success : ResetPasswordState()
    data class Error(val message: String) : ResetPasswordState()
}

class AuthViewModel(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private val _forgotPasswordState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val forgotPasswordState: StateFlow<ForgotPasswordState> = _forgotPasswordState.asStateFlow()

    private val _resetPasswordState = MutableStateFlow<ResetPasswordState>(ResetPasswordState.Idle)
    val resetPasswordState: StateFlow<ResetPasswordState> = _resetPasswordState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthState.Error("Preencha todos os campos")
            return
        }

        _uiState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.login(email, password)
            result.onSuccess {
                _uiState.value = AuthState.Success
            }.onFailure {
                _uiState.value = AuthState.Error(it.message ?: "Erro desconhecido")
            }
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            _forgotPasswordState.value = ForgotPasswordState.Error("O e-mail não pode estar vazio")
            return
        }

        _forgotPasswordState.value = ForgotPasswordState.Loading

        viewModelScope.launch {
            val result = repository.forgotPassword(email)
            result.onSuccess {
                _forgotPasswordState.value = ForgotPasswordState.Success
            }.onFailure {
                _forgotPasswordState.value = ForgotPasswordState.Error(it.message ?: "Erro desconhecido")
            }
        }
    }

    fun resetPassword(token: String, newPassword: String) {
        if (token.isBlank() || newPassword.isBlank()) {
            _resetPasswordState.value = ResetPasswordState.Error("Preencha todos os campos")
            return
        }

        _resetPasswordState.value = ResetPasswordState.Loading

        viewModelScope.launch {
            val result = repository.resetPassword(token, newPassword)
            result.onSuccess {
                _resetPasswordState.value = ResetPasswordState.Success
            }.onFailure {
                _resetPasswordState.value = ResetPasswordState.Error(it.message ?: "Erro desconhecido")
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        resetState()
    }

    fun resetState() {
        _uiState.value = AuthState.Idle
        _forgotPasswordState.value = ForgotPasswordState.Idle
        _resetPasswordState.value = ResetPasswordState.Idle
    }
}