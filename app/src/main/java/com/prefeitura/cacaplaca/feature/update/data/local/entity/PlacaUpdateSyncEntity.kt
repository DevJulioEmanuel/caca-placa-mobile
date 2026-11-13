package com.prefeitura.cacaplaca.feature.update.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "placas_update_sync")
data class PlacaUpdateSyncEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val placaId: String,
    val localPhotoPath: String?,
    val codigoContran: String?,
    val tipoPelicula: String?,
    val tipoSubstrato: String?,
    val tipoSuporte: String?,
    val createdAt: Long = System.currentTimeMillis()
)
