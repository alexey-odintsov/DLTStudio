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
    val timestamp2: Long,
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