package com.example.speedrun_compose.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speedrun_compose.Game
import com.example.speedrun_compose.RetrofitInstance
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf


class GameListViewModel : ViewModel() {
    private val _games = mutableStateOf<List<Game>>(emptyList())
    val games: State<List<Game>> = _games

    fun addGame(game: Game) {
        _games.value += game  // Añade el nuevo juego a la lista
    }

    init {
        fetchGames()
    }

    private fun fetchGames() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getGames(apiKey = "TU_API_KEY")
                _games.value = response.results
            } catch (e: Exception) {
                Log.e("GameListViewModel", "Error fetching games: ${e.message}")
            }
        }
    }
}
