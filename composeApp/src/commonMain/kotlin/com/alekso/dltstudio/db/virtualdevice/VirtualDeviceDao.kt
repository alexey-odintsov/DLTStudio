package com.alekso.dltstudio.db.virtualdevice

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VirtualDeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Use for insert and update
    suspend fun insert(item: VirtualDeviceEntity)

    @Query("SELECT count(*) FROM VirtualDeviceEntity")
    suspend fun count(): Int

    @Query("SELECT * FROM VirtualDeviceEntity")
    fun getAllAsFlow(): Flow<List<VirtualDeviceEntity>>

    @Delete
    suspend fun delete(item: VirtualDeviceEntity)
}