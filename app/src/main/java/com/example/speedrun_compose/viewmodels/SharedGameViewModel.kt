package com.example.speedrun_compose.viewmodels

import androidx.compose.runtime.mutableStateMapOf
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
    private val repository: GameSectionRepository
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
        repository.addSection(gameName, section)
    }

    suspend fun editSection(gameName: String, oldSectionName: String, newSectionName: String) {
        val currentSections = _sections.value[gameName] ?: return
        val updatedSections = _sections.value.toMutableMap().apply {
            this[gameName] = currentSections.map {
                if (it == oldSectionName) newSectionName else it
            }
        }
        _sections.value = updatedSections
        repository.editSection(gameName, oldSectionName, newSectionName)
    }

    suspend fun deleteSection(gameName: String, section: String) {
        val currentSections = _sections.value[gameName] ?: return
        val updatedSections = _sections.value.toMutableMap().apply {
            this[gameName] = currentSections - section // Eliminar sección de la lista
        }
        _sections.value = updatedSections
        repository.removeSection(gameName, section) // Eliminar la sección del repositorio
        loadSectionsFromRepository() // Recargar las secciones desde la base de datos
    }

    private fun loadSectionsFromRepository() {
        viewModelScope.launch {
            _sections.value = repository.getAllSections() // Cargar las secciones más actualizadas desde la base de datos
        }
    }

    val sectionTimers = mutableStateMapOf<String, Pair<Long, Boolean>>()

    fun startTimer(section: String) {
        sectionTimers[section] = sectionTimers[section]?.copy(second = true) ?: 0L to true
    }

    fun stopTimer(section: String) {
        sectionTimers[section] = sectionTimers[section]?.copy(second = false) ?: 0L to false
    }
}