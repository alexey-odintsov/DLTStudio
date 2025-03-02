package com.alekso.dltstudio.db.preferences

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferencesDao {
    /**
     * Stores search item in history
     * 1. Removes old record because we insert new one with new id to keep order
     * 2. Insert new record with new id
     * 3. Ensure MAX_SEARCH_SUGGESTIONS capacity
     */
    suspend fun addNewSearch(item: SearchEntity) {
        removeSearch(item.value)
        addSearch(item)
        ensureSearchCapacity()
    }

    @Upsert
    suspend fun addSearch(item: SearchEntity)

    @Query("SELECT * FROM SearchEntity LIMIT $MAX_SEARCH_SUGGESTIONS")
    fun getRecentSearchFlow(): Flow<List<SearchEntity>>

    @Query("DELETE FROM SearchEntity WHERE id NOT IN (SELECT id FROM SearchEntity ORDER BY id DESC LIMIT $MAX_SEARCH_SUGGESTIONS)")
    suspend fun ensureSearchCapacity()

    @Query("DELETE FROM SearchEntity WHERE value = :value")
    suspend fun removeSearch(value: String)

    companion object {
        const val MAX_SEARCH_SUGGESTIONS = 100
    }
}