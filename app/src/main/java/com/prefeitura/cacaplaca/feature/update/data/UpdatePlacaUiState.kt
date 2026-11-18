package com.prefeitura.cacaplaca.feature.update.data

import android.net.Uri

data class UpdatePlacaUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val placaId: String = "",
    val codigoPlaca: String = "",
    val localizacao: String = "",
    val photoUri: Uri? = null,
    val currentFotoUrl: String? = null,
    val tipoPelicula: String = "",
    val tipoSubstrato: String = "",
    val tipoSuporte: String = "",
    val errorMessage: String? = null,
    val savedOffline: Boolean = false
)