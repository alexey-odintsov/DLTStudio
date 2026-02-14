package com.alekso.dltstudio.plugins.loginsights

import com.alekso.dltstudio.model.contract.LogMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal data class CompiledRule(
    val regex: Regex,
    val groupNames: List<String>,
)

class InsightsRepository {
    private val insightRules = MutableStateFlow<List<LogInsightRule>>(emptyList())
    private val compiledRules = MutableStateFlow<Map<String, CompiledRule>>(emptyMap())
    private val extractGroupRegex = "\\?<(.*?)>".toRegex()

    init {
        loadInsights()
    }

    private fun loadInsights() {
        compiledRules.value = emptyMap()

        // todo: load insights from persistent storage
        insightRules.value =
            listOf(
                LogInsightRule(
                    "TimeStamp",
                    "Possible TimeStamp found: {ts}",
                    """.*(?<ts>[0-9]{10}).*"""
                ),
                LogInsightRule(
                    "Choreographer: Skipped frames",
                    "Process {pid} was doing heavy work on the main thread. This led to skipped {frames} frames.\rRecommendation is to check whether the app is doing some heavy operations on the main thread and offload them",
                    "Choreographer\\[(?<pid>\\d+)\\].*kipped (?<frames>\\d+) frames.*main thread"
                ),
                LogInsightRule(
                    "Garbage Collector",
                    "GC was triggered by {gccause} for application {app} resulting freeing {freed} (large objects {losobj}). Heap is {freepercent}% free - current {currheap}/total {total}. Paused {paused}. Total GC duration {gcduration}.",
                    "(?<app>.*\\[\\d+\\]):(?<gccause>.*) GC freed (?<freed>.*) AllocSpace objects, (?<losobj>.*) LOS objects, (?<freepercent>.*)% free, (?<currheap>.*)/(?<total>.*), paused (?<paused>.*) total (?<gcduration>.*)"
                ),
                LogInsightRule(
                    "System: Resource leak",
                    "Process {pid} was working with a resource and didn't call '{info}' afterward. It's recommended to enable StrictMode in the application and check all the errors.",
                    "System\\[(?<pid>\\d+)\\]:\\s?A resource failed to call (?<info>.*)\\. "
                ),
            )

        // Compile rules
        insightRules.value.forEach {rule ->
            val groupsNames =
                extractGroupRegex.findAll(rule.pattern).map { it.groups[1]?.value }.filterNotNull()
                    .toList()
            compiledRules.update {
                it.toMutableMap().apply {
                    this[rule.name] = CompiledRule(rule.pattern.toRegex(), groupsNames)
                }
            }
        }
    }

    fun findInsight(logMessage: LogMessage): List<LogInsight> {
        val insights = mutableListOf<LogInsight>()
        insightRules.value.forEach { rule ->
            val compiledRule = compiledRules.value[rule.name]
            val text = logMessage.dltMessage.payloadText()
            if (compiledRule != null && text.contains(compiledRule.regex)) {
                val matches = compiledRule.regex.find(text)!!
                insights.add(LogInsight(name = rule.name, text = fillInsightText(rule, matches)))
            }
        }
        return insights
    }

    private fun fillInsightText(rule: LogInsightRule, matches: MatchResult): String {
        var result = rule.template
        compiledRules.value[rule.name]?.groupNames?.forEach { groupName ->
            result = result.replace("{$groupName}", matches.groups[groupName]?.value ?: "")
        }
        return result
    }

}
