package com.prefeitura.cacaplaca.feature.profile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prefeitura.cacaplaca.feature.profile.data.repository.ProfileRepository
import com.prefeitura.cacaplaca.feature.profile.ui.state.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository,
    private val sessionManager: com.prefeitura.cacaplaca.core.session.SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.getProfile()
            result.onSuccess { profile ->
                _uiState.update { it.copy(isLoading = false, profile = profile) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Erro desconhecido") }
            }
        }
    }

    fun updateProfile(nome: String, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, updateError = null, updateSuccess = false) }
            val result = repository.updateProfile(nome, email)
            result.onSuccess { profile ->
                sessionManager.saveUserName(profile.nome)
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        updateSuccess = true,
                        profile = profile
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        updateError = error.message ?: "Erro ao atualizar dados"
                    )
                }
            }
        }
    }

    fun resetUpdateState() {
        _uiState.update { it.copy(updateSuccess = false, updateError = null) }
    }
}
