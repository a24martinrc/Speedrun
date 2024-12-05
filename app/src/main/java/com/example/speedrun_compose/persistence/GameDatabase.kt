package com.example.speedrun_compose.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GameSection::class], version = 1)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameSectionDao(): GameSectionDao
}
