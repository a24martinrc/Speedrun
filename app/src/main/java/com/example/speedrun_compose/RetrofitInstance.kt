package com.example.speedrun_compose

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

    object RetrofitInstance {
        private const val BASE_URL = "https://www.giantbomb.com/api/"

        val api: GameApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GameApiService::class.java)
        }
    }