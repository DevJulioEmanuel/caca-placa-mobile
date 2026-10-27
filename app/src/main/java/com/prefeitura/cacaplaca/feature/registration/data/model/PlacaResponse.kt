package com.prefeitura.cacaplaca.feature.registration.data.model

import com.google.gson.annotations.SerializedName

data class PlacaResponse(
    val id: String?,
    val mensagem: String?
)

data class PlacaListResponse(
    val total: Int,
    val page: Int,
    @SerializedName("page_size")
    val pageSize: Int,
    val items: List<PlacaItemResponse>
)

data class PlacaItemResponse(
    val id: String?,
    @SerializedName("codigo_contran")
    val codigoContran: String,
    val afastamento: Float?,
    @SerializedName("tipo_pelicula")
    val tipoPelicula: String?,
    @SerializedName("tipo_substrato")
    val tipoSubstrato: String?,
    @SerializedName("tipo_suporte")
    val tipoSuporte: String?,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("created_at")
    val createdAt: String?,
    val endereco: EnderecoResponse?,
    @SerializedName("ultimo_evento")
    val ultimoEvento: EventoPlacaResponse?
)

data class EnderecoResponse(
    val id: String?,
    val logradouro: String?,
    val bairro: String?,
    val cep: String?,
    val referencia: String?
)

data class EventoPlacaResponse(
    val id: String?,
    @SerializedName("created_by")
    val createdBy: Int?,
    @SerializedName("tipo_evento")
    val tipoEvento: String?,
    @SerializedName("estado_conservacao")
    val estadoConservacao: String?,
    @SerializedName("estado_pelicula")
    val estadoPelicula: String?,
    @SerializedName("estado_substrato")
    val estadoSubstrato: String?,
    @SerializedName("estado_suporte")
    val estadoSuporte: String?,
    @SerializedName("foto_url")
    val fotoUrl: String?,
    val observacao: String?,
    val timestamp: String?
)
