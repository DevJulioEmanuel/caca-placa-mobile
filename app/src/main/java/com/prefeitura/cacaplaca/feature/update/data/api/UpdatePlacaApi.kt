package com.prefeitura.cacaplaca.feature.update.data.api

import com.prefeitura.cacaplaca.feature.registration.data.model.PlacaItemResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path

interface UpdatePlacaApi {
    @Multipart
    @PATCH("/placas/{placa_id}")
    suspend fun updatePlaca(
        @Path("placa_id") placaId: String,
        @Part("codigo_contran") codigoContran: RequestBody?,
        @Part("afastamento") afastamento: RequestBody?,
        @Part("tipo_pelicula") tipoPelicula: RequestBody?,
        @Part("tipo_substrato") tipoSubstrato: RequestBody?,
        @Part("tipo_suporte") tipoSuporte: RequestBody?,
        @Part("logradouro") logradouro: RequestBody?,
        @Part("bairro") bairro: RequestBody?,
        @Part("cep") cep: RequestBody?,
        @Part("referencia") referencia: RequestBody?,
        @Part foto: MultipartBody.Part?
    ): Response<PlacaItemResponse>
}