package com.alekso.dltstudio.files

data class FileEntry(
    val serialNumber: Long,
    val name: String,
    val size: Long,
    val creationDate: String,
    val numberOfPackages: Int,
)

data class FileEntryFLST(
    val serialNumber: Long,
    val name: String,
    val size: Long,
    val creationDate: String,
    val numberOfPackages: Int,
    val bufferSize: Int,
)