package com.prefeitura.cacaplaca.feature.registration.data

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.net.ConnectException
import java.net.SocketTimeoutException

class RegistrationRepository(
    private val api: RegistrationApi
) {
    private fun createPartFromString(stringData: String): RequestBody {
        return stringData.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    suspend fun submitRegistration(
        data: PlacaCreateData,
        photo: CapturedPhoto
    ): Result<Boolean> {
        return try {
            val file = File(photo.localPath)
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val fotoPart = MultipartBody.Part.createFormData("foto", file.name, requestFile)

            val codigoContranPart = createPartFromString(data.codigoContran)
            val latitudePart = createPartFromString(data.latitude.toString())
            val longitudePart = createPartFromString(data.longitude.toString())
            val estadoConservacaoPart = createPartFromString(data.estadoConservacao.name)

            // Campos mockado enquanto nao colocam como opcional.
            val logradouroPart = createPartFromString("Rua Mockada")
            val bairroPart = createPartFromString("Bairro Mockado")
            val cepPart = createPartFromString("63900-000")

            val afastamentoPart = data.afastamento?.let { createPartFromString(it.toString()) }
            val tipoPeliculaPart = data.tipoPelicula?.let { createPartFromString(it.name) }
            val tipoSubstratoPart = data.tipoSubstrato?.let { createPartFromString(it.name) }
            val tipoSuportePart = data.tipoSuporte?.let { createPartFromString(it.name) }
            val estadoPeliculaPart = data.estadoPelicula?.let { createPartFromString(it.name) }
            val estadoSubstratoPart = data.estadoSubstrato?.let { createPartFromString(it.name) }
            val estadoSuportePart = data.estadoSuporte?.let { createPartFromString(it.name) }

            val response = api.createPlaca(
                codigoContran = codigoContranPart,
                latitude = latitudePart,
                longitude = longitudePart,
                logradouro = logradouroPart,
                bairro = bairroPart,
                cep = cepPart,
                estadoConservacao = estadoConservacaoPart,
                foto = fotoPart,
                afastamento = afastamentoPart,
                tipoPelicula = tipoPeliculaPart,
                tipoSubstrato = tipoSubstratoPart,
                tipoSuporte = tipoSuportePart,
                estadoPelicula = estadoPeliculaPart,
                estadoSubstrato = estadoSubstratoPart,
                estadoSuporte = estadoSuportePart
            )

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                val errorMsg = extractErrorDetail(response)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: ConnectException) {
            Result.failure(Exception("Sem conexão"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Tempo limite esgotado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractErrorDetail(response: Response<*>): String {
        val rawError = response.errorBody()?.string()
        return try {
            if (rawError != null) {
                val jsonObject = JSONObject(rawError)
                if (jsonObject.has("detail")) {
                    jsonObject.getString("detail")
                } else {
                    "Erro: ${response.code()}"
                }
            } else {
                "Erro: ${response.code()}"
            }
        } catch (e: Exception) {
            "Erro: ${response.code()}"
        }
    }
}