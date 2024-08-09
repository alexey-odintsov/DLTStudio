package com.alekso.dltstudio.timeline.filters

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.timeline.TimeLineDurationEntries
import com.alekso.dltstudio.timeline.TimeLineDurationEntry
import com.alekso.dltstudio.timeline.TimeLineEntries
import com.alekso.dltstudio.timeline.TimeLineEntry
import com.alekso.dltstudio.timeline.TimeLineEvent
import com.alekso.dltstudio.timeline.TimeLineEventEntries
import com.alekso.dltstudio.timeline.TimeLineEventEntry
import com.alekso.dltstudio.timeline.TimeLineMinMaxEntries
import com.alekso.dltstudio.timeline.TimeLinePercentageEntries
import com.alekso.dltstudio.timeline.TimeLineSingleStateEntries
import com.alekso.dltstudio.timeline.TimeLineSingleStateEntry
import com.alekso.dltstudio.timeline.TimeLineStateEntries
import com.alekso.dltstudio.timeline.TimeLineStateEntry
import com.alekso.dltstudio.timeline.filters.TimelineFilter.DiagramType.DurationParams
import com.alekso.dltstudio.timeline.filters.TimelineFilter.DiagramType.EventParams
import com.alekso.dltstudio.timeline.filters.TimelineFilter.DiagramType.MinMaxParams
import com.alekso.dltstudio.timeline.filters.TimelineFilter.DiagramType.PercentageParams
import com.alekso.dltstudio.timeline.filters.TimelineFilter.DiagramType.SingleStateParams
import com.alekso.dltstudio.timeline.filters.TimelineFilter.DiagramType.StateParams

interface EntriesExtractor {
    fun extractEntry(
        message: DLTMessage,
        filter: TimelineFilter,
        regex: Regex,
        entries: TimeLineEntries<*>
    )
}

class NonNamedEntriesExtractor : EntriesExtractor {
    override fun extractEntry(
        message: DLTMessage,
        filter: TimelineFilter,
        regex: Regex,
        entries: TimeLineEntries<*>
    ) {
        val matches = regex.find(message.payload)!!

        when (filter.diagramType) {
            TimelineFilter.DiagramType.Percentage -> {
                if (matches.groups.size > 2) {
                    for (i in 1..<matches.groups.size step 2) {
                        val key = matches.groups[i]?.value
                        val value = matches.groups[i + 1]?.value
                        if (key != null && value != null) {
                            (entries as TimeLinePercentageEntries).addEntry(
                                TimeLineEntry(
                                    message.timeStampNano,
                                    key,
                                    value.toFloat()
                                )
                            )
                        }
                    }
                }
            }

            TimelineFilter.DiagramType.MinMaxValue -> {
                if (matches.groups.size > 2) {
                    for (i in 1..<matches.groups.size step 2) {
                        val key = matches.groups[i]?.value
                        val value = matches.groups[i + 1]?.value
                        if (key != null && value != null) {
                            (entries as TimeLineMinMaxEntries).addEntry(
                                TimeLineEntry(
                                    message.timeStampNano,
                                    key,
                                    value.toFloat()
                                )
                            )
                        }
                    }
                }
            }

            TimelineFilter.DiagramType.State -> {
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

            TimelineFilter.DiagramType.SingleState -> {
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

            TimelineFilter.DiagramType.Duration -> {
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

            TimelineFilter.DiagramType.Events -> Unit
        }
    }
}

class NamedEntriesExtractor : EntriesExtractor {
    override fun extractEntry(
        message: DLTMessage,
        filter: TimelineFilter,
        regex: Regex,
        entries: TimeLineEntries<*>
    ) {
        val matches = regex.find(message.payload)!!

        when (filter.diagramType) {
            TimelineFilter.DiagramType.Percentage -> {
                val key: String =
                    matches.groups[PercentageParams.KEY.param.key]?.value ?: NO_KEY
                val value: String? = matches.groups[PercentageParams.VALUE.param.key]?.value
                if (value != null) {
                    (entries as TimeLinePercentageEntries).addEntry(
                        TimeLineEntry(
                            message.timeStampNano,
                            key,
                            value.toFloat()
                        )
                    )
                }
            }

            TimelineFilter.DiagramType.MinMaxValue -> {
                val key: String =
                    matches.groups[MinMaxParams.KEY.param.key]?.value ?: NO_KEY
                val value: String? = matches.groups[MinMaxParams.VALUE.param.key]?.value
                if (value != null) {
                    (entries as TimeLineMinMaxEntries).addEntry(
                        TimeLineEntry(
                            message.timeStampNano,
                            key,
                            value.toFloat()
                        )
                    )
                }
            }

            TimelineFilter.DiagramType.State -> {
                val key: String = matches.groups[StateParams.KEY.param.key]?.value ?: NO_KEY
                val value: String? = matches.groups[StateParams.VALUE.param.key]?.value
                val oldValue: String? =
                    matches.groups[StateParams.OLD_VALUE.param.key]?.value
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

            TimelineFilter.DiagramType.SingleState -> {
                val key: String =
                    matches.groups[SingleStateParams.KEY.param.key]?.value ?: NO_KEY
                val value: String? =
                    matches.groups[SingleStateParams.VALUE.param.key]?.value

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

            TimelineFilter.DiagramType.Duration -> {
                val key: String =
                    matches.groups[DurationParams.KEY.param.key]?.value ?: NO_KEY
                val begin: String? = matches.groups[DurationParams.BEGIN.param.key]?.value
                val end: String? = matches.groups[DurationParams.END.param.key]?.value

                (entries as TimeLineDurationEntries).addEntry(
                    TimeLineDurationEntry(
                        message.timeStampNano,
                        key,
                        Pair(begin, end)
                    )
                )
            }


            TimelineFilter.DiagramType.Events -> {
                val key: String = matches.groups[EventParams.KEY.param.key]?.value ?: NO_KEY
                val value: String? = matches.groups[EventParams.VALUE.param.key]?.value
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
