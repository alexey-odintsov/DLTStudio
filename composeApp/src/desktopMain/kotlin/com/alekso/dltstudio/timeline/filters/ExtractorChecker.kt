package com.alekso.dltstudio.timeline.filters

object ExtractorChecker {
    fun testRegex(
        extractPattern: String?,
        testPayload: String?,
        extractorType: TimelineFilter.ExtractorType,
        diagramType: TimelineFilter.DiagramType,
        global: Boolean = false
    ): String {
        var groupsTestValue = ""
        if (extractPattern != null && testPayload != null) {
            try {
                when (extractorType) {
                    TimelineFilter.ExtractorType.KeyValueNamed -> {
                        when (diagramType) {
                            TimelineFilter.DiagramType.Percentage -> {
                                val matches = Regex(extractPattern).find(testPayload)
                                if (matches != null) {
                                    val key: String = matches.groups["key"]?.value ?: "key"
                                    val value: String? = matches.groups["value"]?.value
                                    groupsTestValue = "$key -> $value"
                                }
                            }

                            TimelineFilter.DiagramType.MinMaxValue -> {
                                val matches = Regex(extractPattern).find(testPayload)
                                if (matches != null) {
                                    val key: String = matches.groups["key"]?.value ?: "key"
                                    val value: String? = matches.groups["value"]?.value
                                    groupsTestValue = "$key -> $value"
                                }
                            }

                            TimelineFilter.DiagramType.State -> {
                                val matches = Regex(extractPattern).find(testPayload)
                                if (matches != null) {
                                    val key: String = matches.groups["key"]?.value ?: "key"
                                    val value: String? = matches.groups["value"]?.value
                                    val oldValue: String? = matches.groups["oldvalue"]?.value
                                    groupsTestValue = "$key -> $value / $oldValue"
                                }
                            }


                            TimelineFilter.DiagramType.SingleState -> {
                                val matches = Regex(extractPattern).find(testPayload)
                                if (matches != null) {
                                    val key: String = matches.groups["key"]?.value ?: "key"
                                    val value: String? = matches.groups["value"]?.value
                                    groupsTestValue = "$key -> $value"
                                }
                            }

                            TimelineFilter.DiagramType.Duration -> {
                                val matches = Regex(extractPattern).find(testPayload)
                                if (matches != null) {
                                    val key: String = matches.groups["key"]?.value ?: "key"
                                    val begin: String? = matches.groups["begin"]?.value
                                    val end: String? = matches.groups["end"]?.value
                                    groupsTestValue = "$key -> $begin / $end"
                                }
                            }

                            TimelineFilter.DiagramType.Events -> {
                                val matches = Regex(extractPattern).find(testPayload)
                                if (matches != null) {
                                    val key: String = matches.groups["key"]?.value ?: "key"
                                    val value: String? = matches.groups["value"]?.value
                                    val info: String? = matches.groups["info"]?.value
                                    groupsTestValue = "$key -> $value / $info"
                                }
                            }
                        }
                    }

                    TimelineFilter.ExtractorType.KeyValueGroups -> {
                        if (global) {
                            val matches = Regex(extractPattern).findAll(testPayload)
                            groupsTestValue =
                                matches.map { "${it.groups[1]?.value} -> ${it.groups[2]?.value}" }
                                    .joinToString("\n")
                            println("Groups: '$groupsTestValue'")
                        } else {
                            val matches = Regex(extractPattern).find(testPayload)
                            if (matches?.groups == null) {
                                groupsTestValue = "Empty groups"
                            } else {

                                val matchesText = StringBuilder()
                                matches.groups.forEachIndexed { index, group ->
                                    if (index > 0 && group != null) {
                                        matchesText.append(group.value)
                                        if (index < matches.groups.size - 1) {
                                            if (index % 2 == 1) {
                                                matchesText.append(" -> ")
                                            } else {
                                                matchesText.append("\n")
                                            }
                                        }
                                    }
                                }
                                groupsTestValue = matchesText.toString()
                                println("Groups: '$groupsTestValue'")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                groupsTestValue = "Invalid regex ${e.printStackTrace()}"
            }
        }
        return groupsTestValue
    }
}