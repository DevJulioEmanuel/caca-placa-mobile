package com.prefeitura.cacaplaca.feature.update.data.worker

import android.content.Context
import android.webkit.MimeTypeMap
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.prefeitura.cacaplaca.feature.registration.data.local.dao.PlacaDao
import com.prefeitura.cacaplaca.feature.update.data.api.UpdatePlacaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class SyncUpdatePlacaWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val placaDao: PlacaDao,
    private val api: UpdatePlacaApi
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): ListenableWorker.Result = withContext(Dispatchers.IO) {
        try {
            val pendingUpdates = placaDao.getPendingUpdatePlacas()

            if (pendingUpdates.isEmpty()) {
                return@withContext ListenableWorker.Result.success()
            }

            var allSuccess = true

            for (entity in pendingUpdates) {
                val codigoBody = entity.codigoContran?.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
                val peliculaBody = entity.tipoPelicula?.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
                val substratoBody = entity.tipoSubstrato?.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
                val suporteBody = entity.tipoSuporte?.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())

                var fotoPart: MultipartBody.Part? = null
                var file: File? = null

                if (entity.localPhotoPath != null) {
                    file = File(entity.localPhotoPath)
                    if (!file.exists()) {
                        placaDao.deletePlacaUpdateSync(entity.id)
                        continue
                    }
                    val extension = file.extension
                    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/jpeg"
                    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                    fotoPart = MultipartBody.Part.createFormData("foto", file.name, requestFile)
                }

                val response = api.updatePlaca(
                    placaId = entity.placaId,
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
                    placaDao.deletePlacaUpdateSync(entity.id)
                    file?.delete()
                } else {
                    allSuccess = false
                }
            }

            if (allSuccess) {
                ListenableWorker.Result.success()
            } else {
                ListenableWorker.Result.retry()
            }
        } catch (e: Exception) {
            ListenableWorker.Result.retry()
        }
    }
}
