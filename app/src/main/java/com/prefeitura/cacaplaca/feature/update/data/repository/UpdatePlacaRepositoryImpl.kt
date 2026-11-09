package com.prefeitura.cacaplaca.feature.update.data.repository

import android.webkit.MimeTypeMap
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

    override suspend fun updatePlaca(
        placaId: String,
        codigoContran: String?,
        tipoPelicula: String?,
        tipoSubstrato: String?,
        tipoSuporte: String?,
        foto: File?
    ): Result<PlacaItemResponse> {
        return try {
            val codigoBody = codigoContran?.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
            val peliculaBody = tipoPelicula?.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
            val substratoBody = tipoSubstrato?.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
            val suporteBody = tipoSuporte?.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())

            val fotoPart = foto?.let {
                val extension = it.extension
                val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/jpeg"

                val requestFile = it.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("foto", it.name, requestFile)
            }

            val response = api.updatePlaca(
                placaId = placaId,
                codigoContran = codigoBody,
                afastamento = null,
                tipoPelicula = peliculaBody,
                tipoSubstrato = substratoBody,
                tipoSuporte = suporteBody,
                logradouro = null,
                bairro = null,
                cep = null,
                referencia = null,
                foto = fotoPart
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorJson = response.errorBody()?.string() ?: "Erro desconhecido: ${response.code()}"
                Result.failure(Exception(errorJson))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}