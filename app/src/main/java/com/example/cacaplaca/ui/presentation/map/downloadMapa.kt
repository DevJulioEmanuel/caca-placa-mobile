package com.example.cacaplaca.ui.presentation.map
import android.content.Context
import android.util.Log
import android.widget.Toast
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.offline.OfflineManager
import org.maplibre.android.offline.OfflineRegion
import org.maplibre.android.offline.OfflineRegionError
import org.maplibre.android.offline.OfflineRegionStatus
import org.maplibre.android.offline.OfflineTilePyramidRegionDefinition

fun verificarEBaixarMapaOffline(context: Context) {
    val offlineManager = OfflineManager.getInstance(context)

    offlineManager.listOfflineRegions(object : OfflineManager.ListOfflineRegionsCallback {

        override fun onList(offlineRegions: Array<OfflineRegion>?) {
            if (offlineRegions.isNullOrEmpty()) {
                Log.d("MapOffline", "Nenhum mapa offline encontrado. Iniciando o primeiro download...")
                iniciarDownloadQuixada(context, offlineManager)
            } else {
                Log.d("MapOffline", "O mapa de Quixadá já está na memória! Pronto para o trabalho offline.")
            }
        }

        override fun onError(error: String) {
            Log.e("MapOffline", "Erro ao verificar mapas offline: $error")
        }
    })
}

private fun iniciarDownloadQuixada(context: Context, offlineManager: OfflineManager) {
    Toast.makeText(context, "Baixando mapa base para uso offline...", Toast.LENGTH_SHORT).show()

    //AREA DE QUIXADA
    val bounds = LatLngBounds.Builder()
        .include(LatLng(-4.9000, -39.0800))
        .include(LatLng(-5.0400, -38.9300))
        .build()

    val definition = OfflineTilePyramidRegionDefinition(
        "https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json",
        bounds,
        10.0,
        18.0,
        context.resources.displayMetrics.density
    )

    val metadata = "QuixadaOffline".toByteArray()

    offlineManager.createOfflineRegion(
        definition,
        metadata,
        object : OfflineManager.CreateOfflineRegionCallback {
            override fun onCreate(offlineRegion: OfflineRegion) {
                offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE)

                offlineRegion.setObserver(object : OfflineRegion.OfflineRegionObserver {
                    override fun onStatusChanged(status: OfflineRegionStatus) {
                        if (status.isComplete) {
                            Log.d("MapOffline", "Download 100% concluído!")
                            Toast.makeText(context, "Mapa offline de Quixadá atualizado!", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(error: OfflineRegionError) {
                        Log.e("MapOffline", "Erro no download: ${error.reason}")
                    }

                    override fun mapboxTileCountLimitExceeded(limit: Long) {
                        Log.e("MapOffline", "Limite de tiles excedido: $limit")
                    }
                })
            }

            override fun onError(error: String) {
                Log.e("MapOffline", "Erro ao criar região offline: $error")
            }
        }
    )
}