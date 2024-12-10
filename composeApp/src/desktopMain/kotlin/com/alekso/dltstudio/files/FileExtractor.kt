package com.alekso.dltstudio.files

import androidx.compose.runtime.mutableStateMapOf

/**
 * The base file structure is as follow:
 * ```
 * FLIF file serialnumber {serial_number} filename {file_name} file size in byte {file_size} file creation date {date_string} number of packages {packages_num} FLIF
 *    FLTS {serial_number} {file_name} {file_size} {date_string} {packages_num} {buffer_size} FLST
 *    FLDA {serial_number} {package_num} {buffer_bytes} FLDA
 *    FLDA {serial_number} {package_num} {buffer_bytes} FLDA
 *    ..
 *    FLDA {serial_number} {package_num} {buffer_bytes} FLDA
 * FLFI
 * ```
 */
class FileExtractor {
    var filesEntriesMap = mutableStateMapOf<String, FileEntry>()
    var filesFLSTEntriesMap = mutableStateMapOf<String, FileEntryFLST>()

    private val flifRegexp =
        """FLIF file serialnumber (?<serial>\d+)\sfilename (?<name>.*?) file size in bytes (?<size>\d+).*creation date (?<creationdate>.*?) number of packages (?<numpackages>\d+)\sFLIF""".toRegex()

    /**
     * Edge case
     * ```FLST 3469424155 /tmp/dri-state-Fri Aug 23 06:28:53 UTC 2024.txt 8216 Fri Aug 23 06:28:53 2024 9 1024 FLST```
     * It is impossible to find `8216` between file name and date without accepting date format as `"`Fri Aug 23 06:28:53 2024`
     */
    private val flstRegexp =
        """FLST\s(?<serial>\d+)\s(?<name>.*\.(\w|\d)+)\s(?<size>\d+)\s(?<creationdate>.*)\s(?<numpackages>\d+)\s(?<bufsize>\d+).*FLST""".toRegex()

    fun searchForFiles(payload: String) {
        if (payload.startsWith("FLIF")) {
            val matches = flifRegexp.find(payload)
            if (matches != null) {
                val serialNumber: Long = matches.groups["serial"]?.value?.toLong() ?: -1L
                val fileName: String = matches.groups["name"]?.value ?: ""
                val fileSize: Long = matches.groups["size"]?.value?.toLong() ?: -1
                val creationDate: String = matches.groups["creationdate"]?.value ?: ""
                val numberOfPackages: Int = matches.groups["numpackages"]?.value?.toInt() ?: -1
                filesEntriesMap[fileName] = FileEntry(
                    serialNumber = serialNumber,
                    name = fileName,
                    size = fileSize,
                    creationDate = creationDate,
                    numberOfPackages = numberOfPackages,
                )
            }
        } else if (payload.startsWith("FLST")) {
            val matches = flstRegexp.find(payload)
            if (matches != null) {
                val serialNumber: Long = matches.groups["serial"]?.value?.toLong() ?: -1L
                val fileName: String = matches.groups["name"]?.value ?: ""
                val fileSize: Long = matches.groups["size"]?.value?.toLong() ?: -1
                val creationDate: String = matches.groups["creationdate"]?.value ?: ""
                val numberOfPackages: Int = matches.groups["numpackages"]?.value?.toInt() ?: -1
                val bufferSize: Int = matches.groups["bufsize"]?.value?.toInt() ?: -1
                filesFLSTEntriesMap[fileName] = FileEntryFLST(
                    serialNumber = serialNumber,
                    name = fileName,
                    size = fileSize,
                    creationDate = creationDate,
                    numberOfPackages = numberOfPackages,
                    bufferSize = bufferSize,
                )
            }

        } else if (payload.startsWith("FLDA")) {
            // todo: Parse bytes
        }
    }
}

