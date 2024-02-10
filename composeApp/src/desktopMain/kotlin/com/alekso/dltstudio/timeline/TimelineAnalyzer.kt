package com.alekso.dltstudio.timeline

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.VerbosePayload
import com.alekso.dltstudio.timeline.TimelineEntry


object TimelineAnalyzer {
    fun analyzeEntries(
        message: DLTMessage,
        appId: String? = null,
        contextId: String? = null,
        regex: Regex,
        map: MutableMap<String, MutableList<TimelineEntry>>
    ) {
        if (message.payload !is VerbosePayload) return
        val payload = (message.payload as VerbosePayload).asText()

        try {
            if (message.extendedHeader?.applicationId == appId && message.extendedHeader?.contextId == contextId) {
                val matches = regex.find(payload)!!
                val key: String? = matches.groups["key"]?.value
                val value: String? = matches.groups["value"]?.value

                if (key != null && value != null) {
                    val entry = TimelineEntry(message.getTimeStamp(), key, value)
                    if (!map.containsKey(key)) {
                        map[key] = mutableListOf()
                    }
                    (map[key] as MutableList).add(entry)
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }
}