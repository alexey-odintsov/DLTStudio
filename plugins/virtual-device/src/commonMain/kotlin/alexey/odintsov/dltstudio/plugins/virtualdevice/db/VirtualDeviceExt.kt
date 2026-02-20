package alexey.odintsov.dltstudio.plugins.virtualdevice.db

import alexey.odintsov.dltstudio.plugins.virtualdevice.model.VirtualDevice


fun VirtualDeviceEntity.toVirtualDevice() = VirtualDevice(id, title, width, height)

fun VirtualDevice.toVirtualDeviceEntity() = VirtualDeviceEntity(id, name, width, height)