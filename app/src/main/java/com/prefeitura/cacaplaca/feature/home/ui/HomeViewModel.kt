package com.prefeitura.cacaplaca.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prefeitura.cacaplaca.core.session.SessionManager
import com.prefeitura.cacaplaca.feature.auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Inicializa com o nome salvo, se existir
        val savedName = sessionManager.getUserName()
        if (!savedName.isNullOrBlank()) {
            _uiState.update { it.copy(agentName = savedName) }
        } else {
            // Se por algum motivo não houver nome salvo (usuário antigo), busca
            fetchUser()
        }
    }

    private fun fetchUser() {
        viewModelScope.launch {
            authRepository.fetchCurrentUser().onSuccess { user ->
                _uiState.update { it.copy(agentName = user.nome) }
            }
        }
    }

    fun onTabSelected(tab: HomeTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun updateAgentName(name: String) {
        _uiState.update { it.copy(agentName = name) }
    }
}
