package com.example.speedrun_compose.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface GameSectionDao {
    @Query("SELECT * FROM game_sections")
    suspend fun getAllSections(): List<GameSection>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(section: GameSection)

    @Delete
    suspend fun deleteSection(section: GameSection)

    @Query("SELECT * FROM game_sections WHERE game_name = :gameName AND section_name = :sectionName")
    suspend fun getSection(gameName: String, sectionName: String): GameSection?

    @Update
    suspend fun updateSection(section: GameSection) // Método para actualizar una sección
}
