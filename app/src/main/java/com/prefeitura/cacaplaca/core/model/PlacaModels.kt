package com.prefeitura.cacaplaca.core.model

enum class CodigoContran(val code: String, val description: String) {
    // Regulamentação (Série R)
    R_1("R-1", "Parada Obrigatória (PARE)"),
    R_2("R-2", "Dê a Preferência"),
    R_3("R-3", "Sentido Proibido"),
    R_4A("R-4a", "Proibido Virar à Esquerda"),
    R_4B("R-4b", "Proibido Virar à Direita"),
    R_5A("R-5a", "Proibido Retornar à Esquerda"),
    R_5B("R-5b", "Proibido Retornar à Direita"),
    R_6A("R-6a", "Proibido Estacionar"),
    R_6B("R-6b", "Estacionamento Regulamentado"),
    R_6C("R-6c", "Proibido Parar e Estacionar"),
    R_7("R-7", "Proibido Ultrapassar"),
    R_8A("R-8a", "Proibido Mudar de Faixa da Esquerda para Direita"),
    R_8B("R-8b", "Proibido Mudar de Faixa da Direita para Esquerda"),
    R_9("R-9", "Proibido Trânsito de Caminhões"),
    R_10("R-10", "Proibido Trânsito de Veículos Automotores"),
    R_11("R-11", "Proibido Trânsito de Veículos de Tração Animal"),
    R_12("R-12", "Proibido Trânsito de Bicicletas"),
    R_13("R-13", "Proibido Trânsito de Tratores e Máquinas de Obras"),
    R_14("R-14", "Peso Bruto Total Máximo Permitido"),
    R_15("R-15", "Altura Máxima Permitida"),
    R_16("R-16", "Largura Máxima Permitida"),
    R_17("R-17", "Peso Máximo Permitido por Eixo"),
    R_18("R-18", "Comprimento Máximo Permitido"),
    R_19("R-19", "Velocidade Máxima Permitida"),
    R_20("R-20", "Proibido Acionar Buzina ou Sinal Sonoro"),
    R_21("R-21", "Alfândega"),
    R_22("R-22", "Uso Obrigatório de Corrente"),
    R_23("R-23", "Conserve-se à Direita"),
    R_24A("R-24a", "Sentido de Circulação da Via/Pista"),
    R_24B("R-24b", "Passagem Obrigatória"),
    R_25A("R-25a", "Vire à Esquerda"),
    R_25B("R-25b", "Vire à Direita"),
    R_25C("R-25c", "Siga em Frente ou à Esquerda"),
    R_25D("R-25d", "Siga em Frente ou à Direita"),
    R_26("R-26", "Siga em Frente"),
    R_27("R-27", "Ônibus, Caminhões e Veículos de Grande Porte Mantenham-se à Direita"),
    R_28("R-28", "Duplo Sentido de Circulação"),
    R_29("R-29", "Proibido Trânsito de Pedestres"),
    R_30("R-30", "Pedestre, Ande pela Esquerda"),
    R_31("R-31", "Pedestre, Ande pela Direita"),
    R_32("R-32", "Circulação Exclusiva de Ônibus"),
    R_33("R-33", "Sentido de Circulação na Rotatória"),
    R_34("R-34", "Circulação Exclusiva de Bicicletas"),
    R_35A("R-35a", "Ciclista, Transite à Esquerda"),
    R_35B("R-35b", "Ciclista, Transite à Direita"),
    R_36A("R-36a", "Ciclistas à Esquerda, Pedestres à Direita"),
    R_36B("R-36b", "Pedestres à Esquerda, Ciclistas à Direita"),
    R_37("R-37", "Proibido Trânsito de Motocicletas, Motonetas e Ciclomotores"),
    R_38("R-38", "Proibido Trânsito de Ônibus"),
    R_39("R-39", "Circulação Exclusiva de Caminhão"),
    R_40("R-40", "Trânsito Proibido a Carros de Mão"),

    // Advertência (Principais da Série A)
    A_1A("A-1a", "Curva Acentuada à Esquerda"),
    A_1B("A-1b", "Curva Acentuada à Direita"),
    A_2A("A-2a", "Curva à Esquerda"),
    A_2B("A-2b", "Curva à Direita"),
    A_14("A-14", "Semáforo à Frente"),
    A_18("A-18", "Saliência ou Lombada"),
    A_24("A-24", "Obras"),
    A_32A("A-32a", "Trânsito de Pedestres"),
    A_32B("A-32b", "Passagem Sinalizada de Pedestres"),
    A_33A("A-33a", "Área Escolar"),
    A_33B("A-33b", "Passagem Sinalizada de Escolares"),

    OUTRO("OUTRO", "Outro")
}

enum class TipoPelicula { TIPO_I, TIPO_II, TIPO_III, TIPO_IV, TIPO_X, NAO_REFLETIVA, OUTRO }
enum class TipoSubstrato { ACO, ALUMINIO, MADEIRA, FIBRA, PLASTICO, OUTRO }
enum class TipoSuporte { POSTE_ACO, POSTE_MADEIRA, POSTE_CONCRETO, PORTICO, COLUNA_SEMAFORICA, PAREDE_MURO, OUTRO }
enum class EstadoConservacao { BOM, REGULAR, RUIM }

data class PlateTypeOption(val code: String, val description: String) {
    val displayLabel: String get() = "$code - $description"
}

val availablePlatesList = CodigoContran.values().map { contran ->
    PlateTypeOption(
        code = contran.code,
        description = contran.description
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
