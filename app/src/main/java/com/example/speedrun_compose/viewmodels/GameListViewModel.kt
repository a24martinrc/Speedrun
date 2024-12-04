package com.example.speedrun_compose.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speedrun_compose.Game
import com.example.speedrun_compose.RetrofitInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameListViewModel : ViewModel() {
    // Usamos MutableStateFlow para la lista de juegos
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games

    fun addGame(game: Game) {
        // Añadimos el nuevo juego a la lista de manera reactiva
        _games.value = _games.value + game
    }

    init {
        fetchGames()
    }

    private fun fetchGames() {
        // Llamada a la API para obtener los juegos
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getGames()  // Llamada a la API
                _games.value = response.results  // Actualizamos el estado con los juegos obtenidos
            } catch (e: Exception) {
                Log.e("GameListViewModel", "Error fetching games: ${e.message}")
            }
        }
    }

    fun removeGame(game: Game) {
        _games.value = _games.value.filter { it != game }
    }

}
