package com.alekso.dltstudio.timeline.filters

object ExtractorChecker {
    fun testRegex(
        extractPattern: String?,
        testPayload: String?,
        extractorType: TimelineFilter.ExtractorType,
        global: Boolean = false
    ): String {
        var groupsTestValue = ""
        if (extractPattern != null && testPayload != null) {
            try {
                when (extractorType) {
                    TimelineFilter.ExtractorType.KeyValueNamed -> {

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