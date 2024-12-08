package com.alekso.dltstudio.timeline.filters.extractors

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.TimeLineEntry

interface EntriesExtractor {
    enum class ExtractionType(val description: String) {
        NamedGroupsOneEntry("One entry per line, key and value are extracted from string: '<key:X> <value:y>'"),
        NamedGroupsManyEntries("Many entries per line, keys are fixed and values are extracted from string: '<key1:value1> <key2:value2>'"),
        GroupsManyEntries("Many entries per line, no named groups: '(key1) (value1) (key2) (value2)'"),
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
