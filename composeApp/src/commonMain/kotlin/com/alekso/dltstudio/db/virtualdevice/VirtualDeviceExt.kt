package com.alekso.dltstudio.db.virtualdevice

import androidx.compose.ui.geometry.Size
import com.alekso.dltstudio.model.VirtualDevice

fun VirtualDeviceEntity.toVirtualDevice() = VirtualDevice(id, title, Size(width, height))

fun VirtualDevice.toVirtualDeviceEntity() = VirtualDeviceEntity(id, name, size.width, size.height)