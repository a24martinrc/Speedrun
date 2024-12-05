package com.example.speedrun_compose.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_sections")
data class GameSection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "game_name") val gameName: String,
    @ColumnInfo(name = "section_name") val sectionName: String
)

