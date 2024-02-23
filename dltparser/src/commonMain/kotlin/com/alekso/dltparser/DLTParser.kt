package com.alekso.dltparser

import com.alekso.dltparser.dlt.DLTMessage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


interface DLTParser {
    companion object {
        const val DEBUG_LOG = true // WARNING: Logging drastically slow down parsing!!!
        const val MAX_BYTES_TO_READ_DEBUG = -1 // put -1 to ignore
        const val DLT_HEADER_SIZE_BYTES = 16
        const val STRING_CODING_MASK = 0b00000000000000000000000000000111
        val STANDARD_HEADER_ENDIAN = Endian.BIG
        val EXTENDED_HEADER_ENDIAN = Endian.BIG
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
    }

    suspend fun read(
        progressCallback: (Float) -> Unit, files: List<File>
    ): List<DLTMessage>

}