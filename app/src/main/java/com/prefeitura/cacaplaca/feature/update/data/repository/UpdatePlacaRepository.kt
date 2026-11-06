package com.prefeitura.cacaplaca.feature.update.data.repository

import com.prefeitura.cacaplaca.feature.registration.data.model.PlacaItemResponse
import java.io.File

interface UpdatePlacaRepository {
    suspend fun updateEventoPlaca(
        placaId: String,
        eventoId: String,
        estadoConservacao: String?,
        estadoPelicula: String?,
        estadoSubstrato: String?,
        estadoSuporte: String?,
        observacao: String?,
        foto: File?
    ): Result<PlacaItemResponse>
}