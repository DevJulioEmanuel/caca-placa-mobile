package com.prefeitura.cacaplaca.feature.update.data.repository

import com.prefeitura.cacaplaca.feature.registration.data.model.PlacaItemResponse
import java.io.File

interface UpdatePlacaRepository {
    suspend fun updatePlaca(
        placaId: String,
        codigoContran: String?,
        tipoPelicula: String?,
        tipoSubstrato: String?,
        tipoSuporte: String?,
        foto: File?
    ): Result<PlacaItemResponse>
}