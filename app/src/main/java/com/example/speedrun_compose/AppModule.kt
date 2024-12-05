package com.example.speedrun_compose

import android.app.Application
import com.example.speedrun_compose.persistence.GameDatabase
import com.example.speedrun_compose.persistence.GameSectionDao
import com.example.speedrun_compose.repository.GameSectionRepository
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(app: Application): Context {
        return app.applicationContext
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): GameDatabase {
        return Room.databaseBuilder(
            context,
            GameDatabase::class.java,
            "game_database"
        ).build()
    }

    @Provides
    fun provideGameSectionDao(database: GameDatabase): GameSectionDao {
        return database.gameSectionDao()
    }

    @Provides
    fun provideGameSectionRepository(dao: GameSectionDao): GameSectionRepository {
        return GameSectionRepository(dao)
    }
}
