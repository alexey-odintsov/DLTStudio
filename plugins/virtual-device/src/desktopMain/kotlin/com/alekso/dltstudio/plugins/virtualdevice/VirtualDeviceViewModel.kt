package com.alekso.dltstudio.plugins.virtualdevice

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.plugins.virtualdevice.db.VirtualDeviceEntity
import com.alekso.dltstudio.plugins.virtualdevice.db.VirtualDeviceRepository
import com.alekso.dltstudio.plugins.virtualdevice.db.toVirtualDevice
import com.alekso.dltstudio.plugins.virtualdevice.db.toVirtualDeviceEntity
import com.alekso.dltstudio.plugins.virtualdevice.model.VirtualDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VirtualDeviceViewModel(
    private val virtualDeviceRepository: VirtualDeviceRepository,
) {
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)

    var devicePreviewsDialogState by mutableStateOf(false)

    private val _virtualDevices = mutableStateListOf<VirtualDevice>()
    val virtualDevices: SnapshotStateList<VirtualDevice>
        get() = _virtualDevices

    init {
        viewModelScope.launch {
            virtualDeviceRepository.getAllAsFlow().collectLatest {
                _virtualDevices.clear()
                _virtualDevices.addAll(it.map(VirtualDeviceEntity::toVirtualDevice))
            }
        }
    }

    fun onVirtualDeviceUpdate(device: VirtualDevice) {
        CoroutineScope(IO).launch {
            virtualDeviceRepository.insert(
                if (device.id >= 0) {
                    VirtualDeviceEntity(
                        id = device.id,
                        title = device.name,
                        width = device.width,
                        height = device.height
                    )
                } else {
                    VirtualDeviceEntity(
                        title = device.name, width = device.width, height = device.height
                    )
                }
            )
        }
    }

    fun onVirtualDeviceDelete(device: VirtualDevice) {
        CoroutineScope(IO).launch {
            virtualDeviceRepository.delete(device.toVirtualDeviceEntity())
        }
    }
}