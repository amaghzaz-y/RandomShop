package com.amaghzaz.randomshop.services

import com.amaghzaz.randomshop.viewModels.Product
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("products/categories")
    suspend fun getCategories(): List<String>
}

object FakeStoreApiService {
    private const val BASE_URL = "https://fakestoreapi.com/"

    private val retrofit =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build()

    val api: ApiService = retrofit.create(ApiService::class.java)
}