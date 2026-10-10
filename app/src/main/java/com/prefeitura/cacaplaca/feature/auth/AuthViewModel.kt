package com.prefeitura.cacaplaca.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prefeitura.cacaplaca.feature.auth.data.AuthRepository
import com.prefeitura.cacaplaca.feature.auth.data.LoginRequest
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

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthState.Error("Preencha todos os campos")
            return
        }

        _uiState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.login(LoginRequest(email, password))
            result.onSuccess {
                _uiState.value = AuthState.Success
            }.onFailure {
                _uiState.value = AuthState.Error(it.message ?: "Erro desconhecido")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthState.Idle
    }
}