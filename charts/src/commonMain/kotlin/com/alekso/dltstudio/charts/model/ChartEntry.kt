package com.alekso.dltstudio.charts.model

interface ChartEntry {
    val timestamp: Long
    val data: Any?
}

data class PercentageEntry(
    override val timestamp: Long,
    val value: Float,
    override val data: Any?
) : ChartEntry

data class MinMaxEntry(
    override val timestamp: Long,
    val value: Float,
    override val data: Any?
) : ChartEntry

data class DurationEntry(
    override val timestamp: Long,
    val begin: String?,
    val end: String?,
    override val data: Any?
) : ChartEntry

data class EventEntry(
    override val timestamp: Long,
    val event: String,
    override val data: Any?,
) : ChartEntry

data class StateEntry(
    override val timestamp: Long,
    val oldState: String,
    val newState: String,
    override val data: Any?,
) : ChartEntry

data class SingleStateEntry(
    override val timestamp: Long,
    val state: String,
    override val data: Any?,
) : ChartEntry