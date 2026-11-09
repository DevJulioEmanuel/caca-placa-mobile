package com.prefeitura.cacaplaca.feature.map.data.model

data class PlacaUiModel(
    val id: String?,
    val codigoContran: String,
    val afastamento: Float?,
    val tipoPelicula: String?,
    val tipoSubstrato: String?,
    val tipoSuporte: String?,
    val latitude: Double,
    val longitude: Double,
    val createdAt: String?,
    val endereco: EnderecoUiModel?,
    val ultimoEvento: EventoPlacaUiModel?
)

data class EnderecoUiModel(
    val id: String?,
    val logradouro: String?,
    val bairro: String?,
    val cep: String?,
    val referencia: String?
)

data class EventoPlacaUiModel(
    val id: String?,
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
