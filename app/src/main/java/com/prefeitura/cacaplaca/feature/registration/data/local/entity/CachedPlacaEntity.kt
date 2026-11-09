package com.prefeitura.cacaplaca.feature.registration.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_placas")
data class CachedPlacaEntity(
    @PrimaryKey val id: String,
    val codigoContran: String,
    val afastamento: Float?,
    val tipoPelicula: String?,
    val tipoSubstrato: String?,
    val tipoSuporte: String?,
    val latitude: Double,
    val longitude: Double,
    val createdAt: String?,
    val enderecoId: String?,
    val logradouro: String?,
    val bairro: String?,
    val cep: String?,
    val referencia: String?,
    val eventoId: String?,
    val createdBy: Int?,
    val tipoEvento: String?,
    val estadoConservacao: String?,
    val estadoPelicula: String?,
    val estadoSubstrato: String?,
    val estadoSuporte: String?,
    val fotoUrl: String?,
    val observacao: String?,
    val timestamp: String?
)
