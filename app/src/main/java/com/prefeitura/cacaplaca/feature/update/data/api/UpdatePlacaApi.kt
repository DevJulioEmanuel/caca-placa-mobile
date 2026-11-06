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
    @PATCH("/placas/{placa_id}/eventos/{evento_id}")
    suspend fun updateEventoPlaca(
        @Path("placa_id") placaId: String,
        @Path("evento_id") eventoId: String,
        @Part("estado_conservacao") estadoConservacao: RequestBody?,
        @Part("estado_pelicula") estadoPelicula: RequestBody?,
        @Part("estado_substrato") estadoSubstrato: RequestBody?,
        @Part("estado_suporte") estadoSuporte: RequestBody?,
        @Part("observacao") observacao: RequestBody?,
        @Part foto: MultipartBody.Part?
    ): Response<PlacaItemResponse>
}