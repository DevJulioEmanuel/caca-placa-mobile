package com.prefeitura.cacaplaca.feature.home.ui

enum class HomeTab {
    HOME,
    MAP,
    PROFILE
}

data class HomeUiState(
    val agentName: String = "Agente Pedro",
    val selectedTab: HomeTab = HomeTab.HOME,
    val pendingSyncCount: Int = 0,
)
