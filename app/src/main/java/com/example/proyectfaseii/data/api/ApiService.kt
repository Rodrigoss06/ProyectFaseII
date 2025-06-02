package com.example.proyectfaseii.data.api

import com.example.proyectfaseii.data.models.Habito
import com.example.proyectfaseii.data.models.Usuario
import retrofit2.Response
import retrofit2.http.*

/**
 * Definición de endpoints REST para Retrofit
 */
interface ApiService {

    @POST("user")
    suspend fun crearUsuario(@Body usuario: Usuario): Response<Void>

    @POST("habit")
    suspend fun crearHabito(@Body habito: Habito): Response<Void>

    @POST("follow")
    suspend fun vincularHabito(@Body datos: Map<String, String>): Response<Void>

    @GET("user/{id}/habits")
    suspend fun obtenerHabitos(@Path("id") userId: String): Response<List<Habito>>

    @GET("suggestions/{id}")
    suspend fun obtenerSugerencias(@Path("id") userId: String): Response<List<Habito>>
}
