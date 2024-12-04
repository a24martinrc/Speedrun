package com.example.speedrun_compose

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://www.giantbomb.com/api/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor())  // Asegúrate de que esto esté configurado correctamente
        .build()

    val api: GameApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)  // Utilizar el cliente con interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameApiService::class.java)
    }
}
