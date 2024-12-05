package com.alekso.dltstudio.timeline.filters.extractors

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.DiagramType
import com.alekso.dltstudio.timeline.Param
import com.alekso.dltstudio.timeline.TimeLineDurationEntries
import com.alekso.dltstudio.timeline.TimeLineDurationEntry
import com.alekso.dltstudio.timeline.TimeLineEntries
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.TimeLineEvent
import com.alekso.dltstudio.timeline.TimeLineEventEntries
import com.alekso.dltstudio.timeline.TimeLineEventEntry
import com.alekso.dltstudio.timeline.TimeLineFloatEntry
import com.alekso.dltstudio.timeline.TimeLineMinMaxEntries
import com.alekso.dltstudio.timeline.TimeLinePercentageEntries
import com.alekso.dltstudio.timeline.TimeLineSingleStateEntries
import com.alekso.dltstudio.timeline.TimeLineSingleStateEntry
import com.alekso.dltstudio.timeline.TimeLineStateEntries
import com.alekso.dltstudio.timeline.TimeLineStateEntry
import com.alekso.dltstudio.timeline.filters.NO_KEY
import com.alekso.dltstudio.timeline.filters.TimelineFilter
import kotlin.text.get

interface EntriesExtractor {
    interface ExtractionType

    fun extractEntry(
        message: DLTMessage,
        regex: Regex,
        extractionType: ExtractionType,
    ): List<TimeLineEntry<*>>
}



@Deprecated("Use EntriesExtractor")
interface EntriesExtractorOld {
    fun extractEntry(
        message: DLTMessage,
        filter: TimelineFilter,
        regex: Regex,
        entries: TimeLineEntries<*>
    )
}

class NonNamedEntriesExtractor : EntriesExtractorOld {
    override fun extractEntry(
        message: DLTMessage,
        filter: TimelineFilter,
        regex: Regex,
        entries: TimeLineEntries<*>
    ) {
        val matches = regex.find(message.payload)!!

        when (filter.diagramType) {
            DiagramType.Percentage -> {// todo: Mode extraction to Diagram implementations
                if (matches.groups.size > 2) {
                    for (i in 1..<matches.groups.size step 2) {
                        val key = matches.groups[i]?.value
                        val value = matches.groups[i + 1]?.value
                        if (key != null && value != null) {
                            (entries as TimeLinePercentageEntries).addEntry(
                                TimeLineFloatEntry(
                                    message.timeStampNano,
                                    key,
                                    value.toFloat()
                                )
                            )
                        }
                    }
                }
            }

            DiagramType.MinMaxValue -> {
                if (matches.groups.size > 2) {
                    for (i in 1..<matches.groups.size step 2) {
                        val key = matches.groups[i]?.value
                        val value = matches.groups[i + 1]?.value
                        if (key != null && value != null) {
                            (entries as TimeLineMinMaxEntries).addEntry(
                                TimeLineFloatEntry(
                                    message.timeStampNano,
                                    key,
                                    value.toFloat()
                                )
                            )
                        }
                    }
                }
            }

            DiagramType.State -> {
                if (matches.groups.size > 2) {
                    for (i in 1..3) {
                        val key = matches.groups[i]?.value
                        val value = matches.groups[i + 1]?.value
                        val oldValue = matches.groups[i + 2]?.value
                        if (key != null && value != null && oldValue != null) {
                            (entries as TimeLineStateEntries).addEntry(
                                TimeLineStateEntry(
                                    message.timeStampNano,
                                    key,
                                    Pair(value, oldValue)
                                )
                            )
                        }
                    }
                }
            }

            DiagramType.SingleState -> {
                if (matches.groups.size > 2) {
                    for (i in 1..3) {
                        val key = matches.groups[i]?.value
                        val value = matches.groups[i + 1]?.value
                        if (key != null && value != null) {
                            (entries as TimeLineSingleStateEntries).addEntry(
                                TimeLineSingleStateEntry(
                                    message.timeStampNano,
                                    key,
                                    value
                                )
                            )
                        }
                    }
                }
            }

            DiagramType.Duration -> {
                if (matches.groups.size > 2) {
                    for (i in 1..3) {
                        val key = matches.groups[i]?.value
                        val begin = matches.groups[i + 1]?.value
                        val end = matches.groups[i + 2]?.value
                        if (key != null) {
                            (entries as TimeLineDurationEntries).addEntry(
                                TimeLineDurationEntry(
                                    message.timeStampNano,
                                    key,
                                    Pair(begin, end)
                                )
                            )
                        }
                    }
                }
            }

            DiagramType.Events -> Unit
        }
    }
}

class NamedEntriesExtractor : EntriesExtractorOld {
    override fun extractEntry(
        message: DLTMessage,
        filter: TimelineFilter,
        regex: Regex,
        entries: TimeLineEntries<*>
    ) {
        val matches = regex.find(message.payload)!!
        val diagramParams = filter.diagramType.params

        when (filter.diagramType) {
            DiagramType.Percentage -> {
                val key: String = matches.groups[diagramParams[Param.KEY]?.key!!]?.value ?: NO_KEY
                val value: String? = matches.groups[diagramParams[Param.VALUE]?.key!!]?.value

                if (value != null) {
                    (entries as TimeLinePercentageEntries).addEntry(
                        TimeLineFloatEntry(
                            message.timeStampNano,
                            key,
                            value.toFloat()
                        )
                    )
                }
            }

            DiagramType.MinMaxValue -> {
                val key: String = matches.groups[diagramParams[Param.KEY]?.key!!]?.value ?: NO_KEY
                val value: String? = matches.groups[diagramParams[Param.VALUE]?.key!!]?.value

                if (value != null) {
                    (entries as TimeLineMinMaxEntries).addEntry(
                        TimeLineFloatEntry(
                            message.timeStampNano,
                            key,
                            value.toFloat()
                        )
                    )
                }
            }

            DiagramType.State -> {
                val key: String = matches.groups[diagramParams[Param.KEY]?.key!!]?.value ?: NO_KEY
                val value: String? = matches.groups[diagramParams[Param.VALUE]?.key!!]?.value
                val oldValue: String? = matches.groups[diagramParams[Param.OLD_VALUE]?.key!!]?.value

                if (value != null && oldValue != null) {
                    (entries as TimeLineStateEntries).addEntry(
                        TimeLineStateEntry(
                            message.timeStampNano,
                            key,
                            Pair(value, oldValue)
                        )
                    )
                }
            }

            DiagramType.SingleState -> {
                val key: String = matches.groups[diagramParams[Param.KEY]?.key!!]?.value ?: NO_KEY
                val value: String? = matches.groups[diagramParams[Param.VALUE]?.key!!]?.value

                if (value != null) {
                    (entries as TimeLineSingleStateEntries).addEntry(
                        TimeLineSingleStateEntry(
                            message.timeStampNano,
                            key,
                            value
                        )
                    )
                }
            }

            DiagramType.Duration -> {
                val key: String = matches.groups[diagramParams[Param.KEY]?.key!!]?.value ?: NO_KEY
                val begin: String? = matches.groups[diagramParams[Param.BEGIN]?.key!!]?.value
                val end: String? = matches.groups[diagramParams[Param.END]?.key!!]?.value

                (entries as TimeLineDurationEntries).addEntry(
                    TimeLineDurationEntry(
                        message.timeStampNano,
                        key,
                        Pair(begin, end)
                    )
                )
            }


            DiagramType.Events -> {
                val key: String = matches.groups[diagramParams[Param.KEY]?.key!!]?.value ?: NO_KEY
                val value: String? = matches.groups[diagramParams[Param.VALUE]?.key!!]?.value

                if (value != null) {
                    (entries as TimeLineEventEntries).addEntry(
                        TimeLineEventEntry(
                            message.timeStampNano,
                            key,
                            TimeLineEvent(value, null) // todo: We don't use any info yet
                        )
                    )
                }
            }
        }
    }

}
