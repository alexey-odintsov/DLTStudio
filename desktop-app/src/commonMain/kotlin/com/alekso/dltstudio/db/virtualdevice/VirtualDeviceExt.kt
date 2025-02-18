package com.alekso.dltstudio.db.virtualdevice

import com.alekso.dltstudio.model.VirtualDevice

fun VirtualDeviceEntity.toVirtualDevice() = VirtualDevice(id, title, width, height)

fun VirtualDevice.toVirtualDeviceEntity() = VirtualDeviceEntity(id, name, width, height)