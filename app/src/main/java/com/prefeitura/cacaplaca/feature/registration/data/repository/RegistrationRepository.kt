package com.prefeitura.cacaplaca.feature.registration.data.repository

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.prefeitura.cacaplaca.feature.registration.data.api.RegistrationApi
import com.prefeitura.cacaplaca.feature.registration.data.local.dao.PlacaDao
import com.prefeitura.cacaplaca.feature.registration.data.local.entity.PlacaSyncEntity
import com.prefeitura.cacaplaca.feature.registration.data.model.CapturedPhoto
import com.prefeitura.cacaplaca.feature.registration.data.model.PlacaCreateData
import com.prefeitura.cacaplaca.feature.registration.data.worker.SyncPlacaWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.io.IOException

enum class SubmissionStatus {
    SENT_TO_API,
    SAVED_LOCALLY
}

class RegistrationRepository(
    private val api: RegistrationApi,
    private val placaDao: PlacaDao,
    private val workManager: WorkManager
) {
    private fun createPartFromString(stringData: String): RequestBody {
        return stringData.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    suspend fun submitRegistration(
        data: PlacaCreateData,
        photo: CapturedPhoto
    ): Result<SubmissionStatus> = withContext(Dispatchers.IO) {
        try {
            val file = File(photo.localPath)
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val fotoPart = MultipartBody.Part.createFormData("foto", file.name, requestFile)

            val response = api.createPlaca(
                codigoContran = createPartFromString(data.codigoContran),
                latitude = createPartFromString(data.latitude.toString()),
                longitude = createPartFromString(data.longitude.toString()),
                estadoConservacao = createPartFromString(data.estadoConservacao.name),
                logradouro = createPartFromString("Rua Mockada"),
                bairro = createPartFromString("Bairro Mockado"),
                cep = createPartFromString("63900-000"),
                foto = fotoPart,
                afastamento = data.afastamento?.let { createPartFromString(it.toString()) },
                tipoPelicula = data.tipoPelicula?.let { createPartFromString(it.name) },
                tipoSubstrato = data.tipoSubstrato?.let { createPartFromString(it.name) },
                tipoSuporte = data.tipoSuporte?.let { createPartFromString(it.name) },
                estadoPelicula = data.estadoPelicula?.let { createPartFromString(it.name) },
                estadoSubstrato = data.estadoSubstrato?.let { createPartFromString(it.name) },
                estadoSuporte = data.estadoSuporte?.let { createPartFromString(it.name) },
                observacao = data.observacao?.ifBlank { null }?.let { createPartFromString(it) }
            )

            if (response.isSuccessful) {
                if (file.exists()) file.delete()
                Result.success(SubmissionStatus.SENT_TO_API)
            } else {
                val errorMsg = extractErrorDetail(response)
                Result.failure(Exception(errorMsg))
            }

        } catch (e: IOException) {
            saveLocallyAndEnqueueWork(data, photo.localPath)
            Result.success(SubmissionStatus.SAVED_LOCALLY)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveLocallyAndEnqueueWork(data: PlacaCreateData, localPhotoPath: String) {
        val entity = PlacaSyncEntity(
            localPhotoPath = localPhotoPath,
            codigoContran = data.codigoContran,
            latitude = data.latitude,
            longitude = data.longitude,
            estadoConservacao = data.estadoConservacao.name,
            afastamento = data.afastamento,
            tipoPelicula = data.tipoPelicula?.name,
            tipoSubstrato = data.tipoSubstrato?.name,
            tipoSuporte = data.tipoSuporte?.name,
            estadoPelicula = data.estadoPelicula?.name,
            estadoSubstrato = data.estadoSubstrato?.name,
            estadoSuporte = data.estadoSuporte?.name,
            observacao = data.observacao?.ifBlank { null }
        )

        placaDao.insertPlacaSync(entity)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Só roda quando tiver internet
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncPlacaWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(syncWorkRequest)
    }

    private fun extractErrorDetail(response: Response<*>): String {
        val rawError = response.errorBody()?.string()
        return try {
            if (rawError != null) {
                val jsonObject = JSONObject(rawError)
                if (jsonObject.has("detail")) jsonObject.getString("detail") else "Erro: ${response.code()}"
            } else {
                "Erro: ${response.code()}"
            }
        } catch (e: Exception) {
            "Erro: ${response.code()}"
        }
    }
}
