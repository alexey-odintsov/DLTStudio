package com.alekso.dltstudio.plugins.diagramtimeline.filters

import com.alekso.dltstudio.plugins.diagramtimeline.DiagramType
import com.alekso.dltstudio.plugins.diagramtimeline.filters.extractors.EntriesExtractor
import com.alekso.dltmessage.SampleData

object ExtractorChecker {
    fun testRegex(
        extractPattern: String?,
        testPayload: String?,
        extractorType: EntriesExtractor.ExtractionType,
        diagramType: DiagramType,
    ): String {
        try {
            if (extractPattern == null) {
                return "Empty extractor pattern"
            }
            val testMessage = SampleData.create(payloadText = testPayload)

            val entries = diagramType.createEntries()
            EntriesExtractor.analyzeEntriesRegex(
                testMessage,
                diagramType,
                extractorType,
                Regex(extractPattern),
                entries
            )

            return entries.toString()
        } catch (e: Exception) {
            return "Can't extract entry"
        }
    }
}