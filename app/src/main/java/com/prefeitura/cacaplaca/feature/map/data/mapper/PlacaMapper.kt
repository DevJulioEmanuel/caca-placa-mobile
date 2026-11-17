package com.prefeitura.cacaplaca.feature.map.data.mapper

import com.prefeitura.cacaplaca.feature.map.data.model.EnderecoUiModel
import com.prefeitura.cacaplaca.feature.map.data.model.EventoPlacaUiModel
import com.prefeitura.cacaplaca.feature.map.data.model.PlacaUiModel
import com.prefeitura.cacaplaca.feature.registration.data.model.EnderecoResponse
import com.prefeitura.cacaplaca.feature.registration.data.model.EventoPlacaResponse
import com.prefeitura.cacaplaca.feature.registration.data.model.PlacaItemResponse

fun PlacaItemResponse.toUiModel(): PlacaUiModel {
    return PlacaUiModel(
        id = id,
        codigoContran = codigoContran,
        afastamento = afastamento,
        tipoPelicula = tipoPelicula,
        tipoSubstrato = tipoSubstrato,
        tipoSuporte = tipoSuporte,
        latitude = latitude,
        longitude = longitude,
        createdAt = createdAt,
        endereco = endereco?.toUiModel(),
        ultimoEvento = ultimoEvento?.toUiModel()
    )
}

fun EnderecoResponse.toUiModel(): EnderecoUiModel {
    return EnderecoUiModel(
        id = id,
        logradouro = logradouro,
        bairro = bairro,
        cep = cep,
        referencia = referencia
    )
}

fun EventoPlacaResponse.toUiModel(): EventoPlacaUiModel {
    return EventoPlacaUiModel(
        id = id,
        createdBy = createdBy,
        tipoEvento = tipoEvento,
        estadoConservacao = estadoConservacao,
        estadoPelicula = estadoPelicula,
        estadoSubstrato = estadoSubstrato,
        estadoSuporte = estadoSuporte,
        fotoUrl = fotoUrl,
        observacao = observacao,
        timestamp = timestamp
    )
}

fun PlacaUiModel.toResponse(): PlacaItemResponse {
    return PlacaItemResponse(
        id = id,
        codigoContran = codigoContran,
        afastamento = afastamento,
        tipoPelicula = tipoPelicula,
        tipoSubstrato = tipoSubstrato,
        tipoSuporte = tipoSuporte,
        latitude = latitude,
        longitude = longitude,
        createdAt = createdAt,
        endereco = endereco?.toResponse(),
        ultimoEvento = ultimoEvento?.toResponse()
    )
}

fun EnderecoUiModel.toResponse(): EnderecoResponse {
    return EnderecoResponse(
        id = id,
        logradouro = logradouro,
        bairro = bairro,
        cep = cep,
        referencia = referencia
    )
}

fun EventoPlacaUiModel.toResponse(): EventoPlacaResponse {
    return EventoPlacaResponse(
        id = id,
        createdBy = createdBy,
        tipoEvento = tipoEvento,
        estadoConservacao = estadoConservacao,
        estadoPelicula = estadoPelicula,
        estadoSubstrato = estadoSubstrato,
        estadoSuporte = estadoSuporte,
        fotoUrl = fotoUrl,
        observacao = observacao,
        timestamp = timestamp
    )
}
