package com.prefeitura.cacaplaca.feature.registration.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RegistrationApi {
    @Multipart
    @POST("placas/")
    suspend fun createPlaca(
        @Part("codigo_contran") codigoContran: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("logradouro") logradouro: RequestBody,
        @Part("bairro") bairro: RequestBody,
        @Part("cep") cep: RequestBody,
        @Part("estado_conservacao") estadoConservacao: RequestBody,
        @Part foto: MultipartBody.Part,
        @Part("referencia") referencia: RequestBody? = null,
        @Part("afastamento") afastamento: RequestBody? = null,
        @Part("tipo_pelicula") tipoPelicula: RequestBody? = null,
        @Part("tipo_substrato") tipoSubstrato: RequestBody? = null,
        @Part("tipo_suporte") tipoSuporte: RequestBody? = null,
        @Part("estado_pelicula") estadoPelicula: RequestBody? = null,
        @Part("estado_substrato") estadoSubstrato: RequestBody? = null,
        @Part("estado_suporte") estadoSuporte: RequestBody? = null,
        @Part("observacao") observacao: RequestBody? = null
    ): Response<PlacaResponse>
}