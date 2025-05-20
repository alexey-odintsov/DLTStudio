package com.alekso.dltstudio.charts.model

interface ChartEntry {
    val timestamp: Long
    val data: Any?
}

data class NumericalEntry(
    val value: Float,
    override val timestamp: Long,
    override val data: Any?
) : ChartEntry

data class DurationEntry(
    override val timestamp: Long,
    override val data: Any?,
    val timestamp2: Long
) : ChartEntry

data class EventEntry(
    override val timestamp: Long,
    override val data: Any?,
    val event: String,
) : ChartEntry

data class StateEntry(
    override val timestamp: Long,
    override val data: Any?,
    val oldState: String,
    val newState: String,
) : ChartEntry

data class SingleStateEntry(
    override val timestamp: Long,
    override val data: Any?,
    val state: String,
) : ChartEntry