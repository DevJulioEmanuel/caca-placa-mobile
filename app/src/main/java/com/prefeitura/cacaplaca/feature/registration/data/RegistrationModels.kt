package com.prefeitura.cacaplaca.feature.registration.data

import com.google.gson.annotations.SerializedName

enum class EstadoConservacao(val label: String) {
    BOM("Bom"),
    REGULAR("Regular"),
    RUIM("Ruim")
}

enum class TipoPelicula(val label: String) {
    TIPO_I("Tipo I"),
    TIPO_II("Tipo II"),
    TIPO_III("Tipo III"),
    TIPO_IV("Tipo IV")
}

enum class TipoSubstrato(val label: String) {
    ACO("Aço"),
    ALUMINIO("Alumínio"),
    POLIESTER("Poliéster / Fibra")
}

enum class TipoSuporte(val label: String) {
    POSTE_ACO("Poste de Aço"),
    POSTE_MADEIRA("Poste de Madeira"),
    POSTE_CONCRETO("Poste de Concreto"),
    PORTICO("Pórtico / Semipórtico")
}

data class PlateTypeOption(
    val code: String,
    val description: String
) {
    val displayLabel: String get() = "$code — $description"
}

data class CapturedPhoto(
    val id: String,
    val localPath: String
)

// Modelo agrupado que representa o PlacaCreate enxuto (sem endereço e sem observação)
data class PlacaCreateData(
    val codigoContran: String,
    val latitude: Double,
    val longitude: Double,
    val estadoConservacao: EstadoConservacao = EstadoConservacao.BOM,
    // Informações adicionais (Opcionais)
    val afastamento: Float? = null,
    val tipoPelicula: TipoPelicula? = null,
    val tipoSubstrato: TipoSubstrato? = null,
    val tipoSuporte: TipoSuporte? = null,
    val estadoPelicula: EstadoConservacao? = null,
    val estadoSubstrato: EstadoConservacao? = null,
    val estadoSuporte: EstadoConservacao? = null
)

// Resposta espelhando EventoPlacaResponse e PlacaResponse (com foto_url do MinIO)
data class EventoPlacaResponse(
    @SerializedName("id") val id: String,
    @SerializedName("created_by") val createdBy: Int,
    @SerializedName("tipo_evento") val tipoEvento: String,
    @SerializedName("estado_conservacao") val estadoConservacao: EstadoConservacao,
    @SerializedName("estado_pelicula") val estadoPelicula: EstadoConservacao?,
    @SerializedName("estado_substrato") val estadoSubstrato: EstadoConservacao?,
    @SerializedName("estado_suporte") val estadoSuporte: EstadoConservacao?,
    @SerializedName("foto_url") val fotoUrl: String?, // URL vinda do MinIO
    @SerializedName("timestamp") val timestamp: String
)

data class PlacaResponse(
    @SerializedName("id") val id: String,
    @SerializedName("codigo_contran") val codigoContran: String,
    @SerializedName("afastamento") val afastamento: Float?,
    @SerializedName("tipo_pelicula") val tipoPelicula: TipoPelicula?,
    @SerializedName("tipo_substrato") val tipoSubstrato: TipoSubstrato?,
    @SerializedName("tipo_suporte") val tipoSuporte: TipoSuporte?,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("ultimo_evento") val ultimoEvento: EventoPlacaResponse? = null
)