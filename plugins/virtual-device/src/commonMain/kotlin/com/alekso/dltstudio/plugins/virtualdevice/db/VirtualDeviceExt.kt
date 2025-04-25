package com.alekso.dltstudio.plugins.virtualdevice.db

import com.alekso.dltstudio.plugins.virtualdevice.model.VirtualDevice


fun VirtualDeviceEntity.toVirtualDevice() = VirtualDevice(id, title, width, height)

fun VirtualDevice.toVirtualDeviceEntity() = VirtualDeviceEntity(id, name, width, height)