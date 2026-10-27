package com.prefeitura.cacaplaca.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable data object Auth : Screen
    @Serializable data object RecoverPassword : Screen
    @Serializable data object EmailSent : Screen
    @Serializable
    data object Home : Screen

    @Serializable
    data object Map : Screen

    @Serializable
    data object Registration : Screen

    @Serializable
    data object Profile : Screen
}
