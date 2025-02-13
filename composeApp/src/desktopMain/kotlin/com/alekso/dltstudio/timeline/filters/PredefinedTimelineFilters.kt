package com.alekso.dltstudio.timeline.filters

import com.alekso.dltstudio.logs.filtering.FilterCriteria
import com.alekso.dltstudio.logs.filtering.FilterParameter
import com.alekso.dltstudio.logs.filtering.TextCriteria
import com.alekso.dltstudio.timeline.DiagramType
import com.alekso.dltstudio.timeline.filters.extractors.EntriesExtractor

val predefinedTimelineFilters = listOf(
    TimelineFilter(
        name = "User state",
        enabled = true,
        extractPattern = """User\s(\d+)\sstate changed from (.*) to (.*)""",
        filters = mapOf(
            FilterParameter.AppId to FilterCriteria("ALD", TextCriteria.PlainText),
            FilterParameter.ContextId to FilterCriteria("SYST", TextCriteria.PlainText),
        ),
        diagramType = DiagramType.State,
        extractorType = EntriesExtractor.ExtractionType.GroupsManyEntries,
        testClause = "User 10 state changed from LOCKED to UNLOCKED"
    ),
    TimelineFilter(
        name = "Crashes",
        enabled = true,
        extractPattern = """Crash \((?<value>.*)\) detected.*Process:\s(?<key>.*). Exception: (?<info>.*) Crash ID:""",
        filters = mapOf(
            FilterParameter.AppId to FilterCriteria("RMAN", TextCriteria.PlainText),
            FilterParameter.ContextId to FilterCriteria("CRSH", TextCriteria.PlainText),
        ),
        diagramType = DiagramType.Events,
        extractorType = EntriesExtractor.ExtractionType.NamedGroupsOneEntry,
        testClause = "Crash (ANR) detected Process: myapp. Exception: NPE Crash ID:123"
    ),
    TimelineFilter(
        name = "CPUC",
        enabled = true,
        extractPattern = """(cpu0):\s*(\d+[.\d+]*)%.*(cpu1):\s*(\d+[.\d+]*)%.*(cpu2):\s*(\d+[.\d+]*)%.*(cpu3):\s*(\d+[.\d+]*)%.*(cpu4):\s*(\d+[.\d+]*)%.*(cpu5):\s*(\d+[.\d+]*)%.*(cpu6):\s*(\d+[.\d+]*)%.*(cpu7):\s*(\d+[.\d+]*)%.*""",
        filters = mapOf(
            FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
            FilterParameter.ContextId to FilterCriteria("CPUC", TextCriteria.PlainText),
        ),
        diagramType = DiagramType.Percentage,
        extractorType = EntriesExtractor.ExtractionType.GroupsManyEntries,
        testClause = "cpu0: 10% cpu1: 45% cpu2: 23% cpu3: 2% cpu4: 23% cpu5: 78% cpu6: 1% cpu7: 12%"
    ),
    TimelineFilter(
        name = "CPUS",
        enabled = false,
        extractPattern = """(cpu):(\d+[.\d+]*)%.*(us):\s(\d+[.\d+]*)%.*(sy):\s(\d+[.\d+]*)%.*(io):\s*(\d+[.\d+]*).*(irq):\s(\d+[.\d+]*)%.*(softirq):\s(\d+[.\d+]*)%.*(ni):\s(\d+[.\d+]*)%.*(st):\s(\d+[.\d+]*)%.*(g):\s(\d+[.\d+]*)%.*(gn):\s(\d+[.\d+]*)%.*(avgcpu):\s*(\d+[.\d+]*)%.*(thread):\s*(\d+[.\d+]*)%.*(kernelthread):\s*(\d+[.\d+]*)%""",
        filters = mapOf(
            FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
            FilterParameter.ContextId to FilterCriteria("CPUS", TextCriteria.PlainText),
        ),
        diagramType = DiagramType.Percentage,
        extractorType = EntriesExtractor.ExtractionType.GroupsManyEntries
    ),
    TimelineFilter(
        name = "CPUP",
        enabled = false,
        extractPattern = """(?<value>\d+.\d+)\s+%(?<key>(.*)pid\s*:\d+)\(""",
        filters = mapOf(
            FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
            FilterParameter.ContextId to FilterCriteria("CPUP", TextCriteria.PlainText),
        ),
        diagramType = DiagramType.Percentage,
        extractorType = EntriesExtractor.ExtractionType.NamedGroupsManyEntries
    ),
    TimelineFilter(
        name = "MEMT",
        enabled = false,
        extractPattern = """(.*)\(cpid.*MaxRSS\(MB\):\s(\d+).*increase""",
        filters = mapOf(
            FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
            FilterParameter.ContextId to FilterCriteria("MEMT", TextCriteria.PlainText),
        ),
        diagramType = DiagramType.MinMaxValue,
        extractorType = EntriesExtractor.ExtractionType.GroupsManyEntries
    ),
    TimelineFilter(
        name = "GPU Load",
        enabled = false,
        extractPattern = """(GPU Load:)\s+(?<value>\d+.\d+)%(?<key>)""", // we use empty 'key' group to ignore key
        filters = mapOf(
            FilterParameter.AppId to FilterCriteria("MON", TextCriteria.PlainText),
            FilterParameter.ContextId to FilterCriteria("GPU", TextCriteria.PlainText),
        ),
        diagramType = DiagramType.Percentage,
        extractorType = EntriesExtractor.ExtractionType.NamedGroupsManyEntries
    ),
)