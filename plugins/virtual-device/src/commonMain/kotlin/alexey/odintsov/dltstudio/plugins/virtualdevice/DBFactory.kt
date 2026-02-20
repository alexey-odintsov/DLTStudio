package alexey.odintsov.dltstudio.plugins.virtualdevice

expect class DBFactory {
    fun createDatabase(path: String): VirtualDeviceDatabase
}