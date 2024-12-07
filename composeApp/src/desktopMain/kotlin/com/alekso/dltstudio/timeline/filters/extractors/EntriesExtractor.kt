package com.alekso.dltstudio.timeline.filters.extractors

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.TimeLineEntry

interface EntriesExtractor {
    enum class ExtractionType {
        KeyValueGroups,
        KeyValueNamed,
    }

    enum class Param(val value: String) {
        KEY("key"),
        VALUE("value"),
        INFO("info"),
        OLD_VALUE("oldvalue"),
        BEGIN("begin"),
        END("end"),
    }

    data class ExtractorParam(
        val key: String,
        val description: String,
        val required: Boolean = false,
    )

    fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
    ): List<TimeLineEntry<*>>
}
