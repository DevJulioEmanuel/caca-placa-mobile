package com.prefeitura.cacaplaca.feature.update.data.repository

import android.webkit.MimeTypeMap
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.prefeitura.cacaplaca.feature.registration.data.local.dao.PlacaDao
import com.prefeitura.cacaplaca.feature.registration.data.repository.SubmissionStatus
import com.prefeitura.cacaplaca.feature.update.data.api.UpdatePlacaApi
import com.prefeitura.cacaplaca.feature.update.data.local.entity.PlacaUpdateSyncEntity
import com.prefeitura.cacaplaca.feature.update.data.worker.SyncUpdatePlacaWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class UpdatePlacaRepositoryImpl(
    private val api: UpdatePlacaApi,
    private val placaDao: PlacaDao,
    private val workManager: WorkManager
) : UpdatePlacaRepository {

    override suspend fun updatePlaca(
        placaId: String,
        codigoContran: String?,
        tipoPelicula: String?,
        tipoSubstrato: String?,
        tipoSuporte: String?,
        foto: File?
    ): Result<SubmissionStatus> = withContext(Dispatchers.IO) {
        try {
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

            if (response.isSuccessful) {
                foto?.let { if (it.exists()) it.delete() }
                Result.success(SubmissionStatus.SENT_TO_API)
            } else {
                val errorJson = response.errorBody()?.string() ?: "Erro desconhecido: ${response.code()}"
                Result.failure(Exception(errorJson))
            }
        } catch (e: IOException) {
            saveLocallyAndEnqueueWork(placaId, codigoContran, tipoPelicula, tipoSubstrato, tipoSuporte, foto?.absolutePath)
            Result.success(SubmissionStatus.SAVED_LOCALLY)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveLocallyAndEnqueueWork(
        placaId: String,
        codigoContran: String?,
        tipoPelicula: String?,
        tipoSubstrato: String?,
        tipoSuporte: String?,
        localPhotoPath: String?
    ) {
        val entity = PlacaUpdateSyncEntity(
            placaId = placaId,
            codigoContran = codigoContran,
            tipoPelicula = tipoPelicula,
            tipoSubstrato = tipoSubstrato,
            tipoSuporte = tipoSuporte,
            localPhotoPath = localPhotoPath
        )

        placaDao.insertPlacaUpdateSync(entity)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncUpdatePlacaWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(syncWorkRequest)
    }
}
