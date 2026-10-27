package com.prefeitura.cacaplaca.feature.registration.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result as WorkResult
import androidx.work.WorkerParameters
import com.prefeitura.cacaplaca.feature.registration.data.api.RegistrationApi
import com.prefeitura.cacaplaca.feature.registration.data.local.dao.PlacaDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class SyncPlacaWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val placaDao: PlacaDao,
    private val api: RegistrationApi
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): WorkResult = withContext(Dispatchers.IO) {
        try {
            val pendingPlacas = placaDao.getPendingPlacas()

            if (pendingPlacas.isEmpty()) {
                return@withContext WorkResult.success()
            }

            var allSuccess = true

            for (entity in pendingPlacas) {
                val file = File(entity.localPhotoPath)
                if (!file.exists()) {
                    placaDao.deletePlacaSync(entity.id)
                    continue
                }

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val fotoPart = MultipartBody.Part.createFormData("foto", file.name, requestFile)

                val response = api.createPlaca(
                    codigoContran = createPartFromString(entity.codigoContran),
                    latitude = createPartFromString(entity.latitude.toString()),
                    longitude = createPartFromString(entity.longitude.toString()),
                    estadoConservacao = createPartFromString(entity.estadoConservacao),
                    logradouro = createPartFromString("Rua Mockada"),
                    bairro = createPartFromString("Bairro Mockado"),
                    cep = createPartFromString("63900-000"),
                    foto = fotoPart,
                    afastamento = entity.afastamento?.let { createPartFromString(it.toString()) },
                    tipoPelicula = entity.tipoPelicula?.let { createPartFromString(it) },
                    tipoSubstrato = entity.tipoSubstrato?.let { createPartFromString(it) },
                    tipoSuporte = entity.tipoSuporte?.let { createPartFromString(it) },
                    estadoPelicula = entity.estadoPelicula?.let { createPartFromString(it) },
                    estadoSubstrato = entity.estadoSubstrato?.let { createPartFromString(it) },
                    estadoSuporte = entity.estadoSuporte?.let { createPartFromString(it) },
                    observacao = entity.observacao?.let { createPartFromString(it) }
                )

                if (response.isSuccessful) {
                    placaDao.deletePlacaSync(entity.id)
                    file.delete()
                } else {
                    allSuccess = false
                }
            }

            if (allSuccess) WorkResult.success() else WorkResult.retry()
        } catch (e: Exception) {
            WorkResult.retry()
        }
    }

    private fun createPartFromString(stringData: String): RequestBody {
        return stringData.toRequestBody("text/plain".toMediaTypeOrNull())
    }
}