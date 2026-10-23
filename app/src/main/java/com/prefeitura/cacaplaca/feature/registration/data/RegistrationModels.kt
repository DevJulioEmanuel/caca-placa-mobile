package com.prefeitura.cacaplaca.feature.registration.data

enum class CodigoContran(val code: String) {
    R_1("R-1"), R_2("R-2"), R_3("R-3"), R_4A("R-4a"), R_4B("R-4b"),
    R_5A("R-5a"), R_5B("R-5b"), R_6A("R-6a"), R_6B("R-6b"), R_6C("R-6c"),
    R_7("R-7"), R_8A("R-8a"), R_8B("R-8b"), R_9("R-9"), R_10("R-10"),
    R_11("R-11"), R_12("R-12"), R_13("R-13"), R_14("R-14"), R_15("R-15"),
    R_16("R-16"), R_17("R-17"), R_18("R-18"), R_19("R-19"), R_20("R-20"),
    R_21("R-21"), R_22("R-22"), R_23("R-23"), R_24A("R-24a"), R_24B("R-24b"),
    R_25A("R-25a"), R_25B("R-25b"), R_25C("R-25c"), R_25D("R-25d"),
    R_26("R-26"), R_27("R-27"), R_28("R-28"), R_29("R-29"), R_30("R-30"),
    R_31("R-31"), R_32("R-32"), R_33("R-33"), R_34("R-34"), R_35A("R-35a"),
    R_35B("R-35b"), R_36A("R-36a"), R_36B("R-36b"), R_37("R-37"),
    R_38("R-38"), R_39("R-39"), R_40("R-40"),
    A_1A("A-1a"), A_1B("A-1b"), A_2A("A-2a"), A_2B("A-2b"), A_14("A-14"),
    A_18("A-18"), A_24("A-24"), A_32A("A-32a"), A_32B("A-32b"),
    A_33A("A-33a"), A_33B("A-33b"),
    OUTRO("OUTRO")
}

enum class TipoPelicula { TIPO_I, TIPO_II, TIPO_III, TIPO_IV, TIPO_X, NAO_REFLETIVA, OUTRO }
enum class TipoSubstrato { ACO, ALUMINIO, MADEIRA, FIBRA, PLASTICO, OUTRO }
enum class TipoSuporte { POSTE_ACO, POSTE_MADEIRA, POSTE_CONCRETO, PORTICO, COLUNA_SEMAFORICA, PAREDE_MURO, OUTRO }
enum class EstadoConservacao { BOM, REGULAR, RUIM }

data class PlateTypeOption(val code: String, val description: String) {
    val displayLabel: String get() = "$code -$description"
}

val availablePlatesList = CodigoContran.values().map { contran ->
    PlateTypeOption(
        code = contran.code,
        description = "Sinalização ${contran.code}"
    )
}

data class PlacaCreateData(
    val codigoContran: String,
    val latitude: Double,
    val longitude: Double,
    val estadoConservacao: EstadoConservacao,
    val afastamento: Float? = null,
    val tipoPelicula: TipoPelicula? = null,
    val tipoSubstrato: TipoSubstrato? = null,
    val tipoSuporte: TipoSuporte? = null,
    val estadoPelicula: EstadoConservacao? = null,
    val estadoSubstrato: EstadoConservacao? = null,
    val estadoSuporte: EstadoConservacao? = null,
    val observacao: String? = null
)