package com.alekso.dltstudio.ui

import com.alekso.dltparser.DLTParser
import com.alekso.dltparser.dlt.DLTMessage
import java.io.File

class ParseSession(val progressCallback: (Float) -> Unit, val file: File) {
    val dltMessages = mutableListOf<DLTMessage>()

    suspend fun start() {
        dltMessages.addAll(DLTParser.read(progressCallback, file.inputStream()))
    }
}