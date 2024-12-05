package com.example.speedrun_compose.repository

import com.example.speedrun_compose.persistence.GameSection
import com.example.speedrun_compose.persistence.GameSectionDao
import javax.inject.Inject
class GameSectionRepository @Inject constructor(
    private val dao: GameSectionDao
) {
    suspend fun getAllSections(): Map<String, List<String>> {
        return dao.getAllSections().groupBy { it.gameName }.mapValues { entry ->
            entry.value.map { it.sectionName }
        }
    }

    suspend fun addSection(gameName: String, sectionName: String) {
        dao.insertSection(GameSection(gameName = gameName, sectionName = sectionName))
    }

    suspend fun removeSection(gameName: String, sectionName: String) {
        dao.deleteSection(GameSection(gameName = gameName, sectionName = sectionName))
    }
}
