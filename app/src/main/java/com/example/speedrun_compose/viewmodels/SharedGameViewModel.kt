package com.example.speedrun_compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speedrun_compose.repository.GameSectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedGameViewModel @Inject constructor(
    private val repository: GameSectionRepository // Inyecta un repositorio
) : ViewModel() {

    private val _sections = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val sections: StateFlow<Map<String, List<String>>> = _sections

    init {
        loadSectionsFromRepository()
    }

    suspend fun addSection(gameName: String, section: String) {
        val currentSections = _sections.value[gameName] ?: emptyList()
        val updatedSections = _sections.value.toMutableMap().apply {
            this[gameName] = currentSections + section
        }
        _sections.value = updatedSections
        repository.addSection(gameName, section) // Guarda en persistencia
    }

    fun getSections(gameName: String): List<String> {
        return _sections.value[gameName] ?: emptyList()
    }

    suspend fun removeSection(gameName: String, section: String) {
        val currentSections = _sections.value[gameName] ?: return
        val updatedSections = _sections.value.toMutableMap().apply {
            this[gameName] = currentSections - section
        }
        _sections.value = updatedSections
        repository.removeSection(gameName, section) // Actualiza persistencia
    }

    private fun loadSectionsFromRepository() {
        viewModelScope.launch {
            _sections.value = repository.getAllSections()
        }
    }
}
