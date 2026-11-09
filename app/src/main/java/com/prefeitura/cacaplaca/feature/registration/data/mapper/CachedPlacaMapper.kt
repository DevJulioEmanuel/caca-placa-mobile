package com.prefeitura.cacaplaca.feature.registration.data.mapper

import com.prefeitura.cacaplaca.feature.registration.data.local.entity.CachedPlacaEntity
import com.prefeitura.cacaplaca.feature.registration.data.model.EnderecoResponse
import com.prefeitura.cacaplaca.feature.registration.data.model.EventoPlacaResponse
import com.prefeitura.cacaplaca.feature.registration.data.model.PlacaItemResponse
import java.util.UUID

fun CachedPlacaEntity.toItemResponse(): PlacaItemResponse {
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
        endereco = if (logradouro != null || bairro != null || cep != null || referencia != null || enderecoId != null) {
            EnderecoResponse(
                id = enderecoId,
                logradouro = logradouro,
                bairro = bairro,
                cep = cep,
                referencia = referencia
            )
        } else null,
        ultimoEvento = if (eventoId != null || estadoConservacao != null || tipoEvento != null) {
            EventoPlacaResponse(
                id = eventoId,
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
        } else null
    )
}

fun PlacaItemResponse.toEntity(): CachedPlacaEntity {
    return CachedPlacaEntity(
        id = id ?: UUID.randomUUID().toString(),
        codigoContran = codigoContran,
        afastamento = afastamento,
        tipoPelicula = tipoPelicula,
        tipoSubstrato = tipoSubstrato,
        tipoSuporte = tipoSuporte,
        latitude = latitude,
        longitude = longitude,
        createdAt = createdAt,
        enderecoId = endereco?.id,
        logradouro = endereco?.logradouro,
        bairro = endereco?.bairro,
        cep = endereco?.cep,
        referencia = endereco?.referencia,
        eventoId = ultimoEvento?.id,
        createdBy = ultimoEvento?.createdBy,
        tipoEvento = ultimoEvento?.tipoEvento,
        estadoConservacao = ultimoEvento?.estadoConservacao,
        estadoPelicula = ultimoEvento?.estadoPelicula,
        estadoSubstrato = ultimoEvento?.estadoSubstrato,
        estadoSuporte = ultimoEvento?.estadoSuporte,
        fotoUrl = ultimoEvento?.fotoUrl,
        observacao = ultimoEvento?.observacao,
        timestamp = ultimoEvento?.timestamp
    )
}
