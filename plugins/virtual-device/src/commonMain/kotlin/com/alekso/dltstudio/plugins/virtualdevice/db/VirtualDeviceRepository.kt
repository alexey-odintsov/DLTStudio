package com.alekso.dltstudio.plugins.virtualdevice.db

import com.alekso.dltstudio.plugins.virtualdevice.VirtualDeviceDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


interface VirtualDeviceRepository {
    suspend fun insert(item: VirtualDeviceEntity)

    suspend fun delete(item: VirtualDeviceEntity)

    suspend fun count(): Int

    fun getAllAsFlow(): Flow<List<VirtualDeviceEntity>>

}

class VirtualDeviceMock : VirtualDeviceRepository {
    private val virtualDevicesFlow = MutableStateFlow(mutableListOf<VirtualDeviceEntity>())

    override suspend fun insert(item: VirtualDeviceEntity) {
        virtualDevicesFlow.value.add(item)
    }

    override suspend fun delete(item: VirtualDeviceEntity) {
        virtualDevicesFlow.value.remove(item)
    }

    override suspend fun count(): Int {
        return virtualDevicesFlow.value.size
    }

    override fun getAllAsFlow(): Flow<List<VirtualDeviceEntity>> {
        return virtualDevicesFlow
    }

}

class VirtualDeviceRepositoryImpl(
    private val database: VirtualDeviceDatabase,
    private val scope: CoroutineScope
) : VirtualDeviceRepository {

    override suspend fun insert(item: VirtualDeviceEntity) {
        database.getVirtualDeviceDao().insert(item)
    }

    override suspend fun delete(item: VirtualDeviceEntity) {
        database.getVirtualDeviceDao().delete(item)
    }

    override suspend fun count(): Int {
        return database.getVirtualDeviceDao().count()
    }

    override fun getAllAsFlow(): Flow<List<VirtualDeviceEntity>> {
        return database.getVirtualDeviceDao().getAllAsFlow()
    }
}