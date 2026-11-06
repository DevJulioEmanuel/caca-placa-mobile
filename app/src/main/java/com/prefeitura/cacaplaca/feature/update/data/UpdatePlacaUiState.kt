package com.prefeitura.cacaplaca.feature.update.data

import android.net.Uri

data class UpdatePlacaUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val placaId: String = "",
    val eventoId: String = "",
    val codigoPlaca: String = "",
    val localizacao: String = "",
    val photoUri: Uri? = null,
    val estadoConservacao: String = "",
    val estadoPelicula: String = "",
    val estadoSubstrato: String = "",
    val estadoSuporte: String = "",
    val observacao: String = "",
    val errorMessage: String? = null
)