package com.example.speedrun_compose

import retrofit2.http.GET
import retrofit2.http.Query

interface GameApiService {
    @GET("games/")
    suspend fun getGames(
        @Query("api_key") apiKey: String = "8dc4b2a2106922dfb271c362c1b71950bdbbf6e8",
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
