package com.example.speedrun_compose

import retrofit2.http.GET
import retrofit2.http.Query

interface GameApiService {
    @GET("games/")
    suspend fun getGames(    @Query("api_key") apiKey: String,
                             @Query("format") format: String = "json"
    ): ApiResponse
}

data class ApiResponse(
    val results: List<Game>
)

data class Game(
    val id: Int,
    val name: String
)
