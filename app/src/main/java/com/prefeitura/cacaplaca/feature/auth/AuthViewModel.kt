package com.prefeitura.cacaplaca.feature.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prefeitura.cacaplaca.core.session.SessionManager
import com.prefeitura.cacaplaca.feature.auth.data.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface AuthState {
    data object Idle : AuthState
    data object Loading : AuthState
}

sealed interface ForgotPasswordState {
    data object Idle : ForgotPasswordState
    data object Loading : ForgotPasswordState
}

sealed interface AuthEvent {
    data object NavigateToHome : AuthEvent
    data object NavigateToEmailSent : AuthEvent
    data class ShowError(val message: String) : AuthEvent
    data class ShowLoginFieldError(val emailError: String?, val passwordError: String?) : AuthEvent
    data class ShowRecoverFieldError(val emailError: String?) : AuthEvent
}

class AuthViewModel(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private val _forgotPasswordState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val forgotPasswordState: StateFlow<ForgotPasswordState> = _forgotPasswordState.asStateFlow()

    private val _uiEvent = Channel<AuthEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun login(email: String, password: String) {
        var isValid = true
        var emailError: String? = null
        var passwordError: String? = null

        if (email.isBlank()) {
            emailError = "O e-mail não pode estar vazio"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Formato de e-mail inválido"
            isValid = false
        }

        if (password.isBlank()) {
            passwordError = "A senha não pode estar vazia"
            isValid = false
        }

        if (!isValid) {
            viewModelScope.launch { _uiEvent.send(AuthEvent.ShowLoginFieldError(emailError, passwordError)) }
            return
        }

        _uiState.value = AuthState.Loading

        viewModelScope.launch {
            repository.login(email, password)
                .onSuccess {
                    _uiState.value = AuthState.Idle
                    _uiEvent.send(AuthEvent.NavigateToHome)
                }.onFailure {
                    _uiState.value = AuthState.Idle
                    _uiEvent.send(AuthEvent.ShowError(it.message ?: "Erro desconhecido"))
                }
        }
    }

    fun forgotPassword(email: String) {
        var isValid = true
        var emailError: String? = null

        if (email.isBlank()) {
            emailError = "O e-mail não pode estar vazio"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Formato de e-mail inválido"
            isValid = false
        }

        if (!isValid) {
            viewModelScope.launch { _uiEvent.send(AuthEvent.ShowRecoverFieldError(emailError)) }
            return
        }

        _forgotPasswordState.value = ForgotPasswordState.Loading

        viewModelScope.launch {
            repository.forgotPassword(email)
                .onSuccess {
                    _forgotPasswordState.value = ForgotPasswordState.Idle
                    _uiEvent.send(AuthEvent.NavigateToEmailSent)
                }.onFailure {
                    _forgotPasswordState.value = ForgotPasswordState.Idle
                    _uiEvent.send(AuthEvent.ShowError(it.message ?: "Erro desconhecido"))
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
    }
}
