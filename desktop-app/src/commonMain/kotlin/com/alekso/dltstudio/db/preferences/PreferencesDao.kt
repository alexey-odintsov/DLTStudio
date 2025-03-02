package com.alekso.dltstudio.db.preferences

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferencesDao {

    suspend fun addNewSearch(item: SearchEntity) {
        updateRecentSearch(item)
        removeOldSearch()
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateRecentSearch(item: SearchEntity)

    @Query("SELECT * FROM SearchEntity LIMIT 100")
    fun getRecentSearchFlow(): Flow<List<SearchEntity>>

    @Query("DELETE FROM SearchEntity WHERE id IN (SELECT id FROM SearchEntity ORDER BY id DESC LIMIT 100)")
    suspend fun removeOldSearch()
}