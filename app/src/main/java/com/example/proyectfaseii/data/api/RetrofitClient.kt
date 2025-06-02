package com.example.proyectfaseii.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton que expone una instancia de ApiService
 */
object RetrofitClient {
    // Reemplaza esta URL con la de tu backend real
    private const val BASE_URL = "https://habitos-backend-production.up.railway.app/api/habits/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
