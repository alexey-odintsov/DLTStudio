package com.alekso.dltstudio.db.preferences

import com.alekso.dltstudio.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun addNewSearch(item: SearchEntity)
    fun getRecentSearch(): Flow<List<SearchEntity>>
}

class PreferencesRepositoryImpl(
    private val database: AppDatabase,
    private val scope: CoroutineScope
) : PreferencesRepository {
    override suspend fun addNewSearch(item: SearchEntity) {
        database.getPreferencesDao().addNewSearch(item)
    }

    override fun getRecentSearch(): Flow<List<SearchEntity>> {
        return database.getPreferencesDao().getRecentSearchFlow()
    }

}