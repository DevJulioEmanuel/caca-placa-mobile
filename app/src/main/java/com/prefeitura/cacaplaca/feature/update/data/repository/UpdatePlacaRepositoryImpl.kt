package com.prefeitura.cacaplaca.feature.update.data.repository

import com.prefeitura.cacaplaca.feature.registration.data.model.PlacaItemResponse
import com.prefeitura.cacaplaca.feature.update.data.api.UpdatePlacaApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UpdatePlacaRepositoryImpl(
    private val api: UpdatePlacaApi
) : UpdatePlacaRepository {

    override suspend fun updateEventoPlaca(
        placaId: String,
        eventoId: String,
        estadoConservacao: String?,
        estadoPelicula: String?,
        estadoSubstrato: String?,
        estadoSuporte: String?,
        observacao: String?,
        foto: File?
    ): Result<PlacaItemResponse> {
        return try {
            val conservacaoBody = estadoConservacao?.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
            val peliculaBody = estadoPelicula?.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
            val substratoBody = estadoSubstrato?.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
            val suporteBody = estadoSuporte?.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
            val observacaoBody = observacao?.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())

            val fotoPart = foto?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("foto", it.name, requestFile)
            }

            val response = api.updateEventoPlaca(
                placaId = placaId,
                eventoId = eventoId,
                estadoConservacao = conservacaoBody,
                estadoPelicula = peliculaBody,
                estadoSubstrato = substratoBody,
                estadoSuporte = suporteBody,
                observacao = observacaoBody,
                foto = fotoPart
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erro HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}