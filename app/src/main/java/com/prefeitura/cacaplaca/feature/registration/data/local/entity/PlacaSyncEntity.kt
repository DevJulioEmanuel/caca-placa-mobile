package com.prefeitura.cacaplaca.feature.registration.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "placas_sync")
data class PlacaSyncEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val localPhotoPath: String,
    val codigoContran: String,
    val latitude: Double,
    val longitude: Double,
    val estadoConservacao: String,
    val afastamento: Float?,
    val tipoPelicula: String?,
    val tipoSubstrato: String?,
    val tipoSuporte: String?,
    val estadoPelicula: String?,
    val estadoSubstrato: String?,
    val estadoSuporte: String?,
    val observacao: String?,
    val createdAt: Long = System.currentTimeMillis()
)
