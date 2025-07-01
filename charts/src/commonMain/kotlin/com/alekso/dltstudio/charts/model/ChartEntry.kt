package com.alekso.dltstudio.charts.model

interface ChartEntry<T> {
    val timestamp: Long
    val data: T?
}

data class PercentageEntry<T>(
    override val timestamp: Long,
    val value: Float,
    override val data: T? = null
) : ChartEntry<T>

data class MinMaxEntry<T>(
    override val timestamp: Long,
    val value: Float,
    override val data: T? = null
) : ChartEntry<T>

data class DurationEntry<T>(
    override val timestamp: Long,
    val begin: String?,
    val end: String?,
    override val data: T? = null
) : ChartEntry<T>

data class EventEntry<T>(
    override val timestamp: Long,
    val event: String,
    override val data: T? = null
) : ChartEntry<T>

data class StateEntry<T>(
    override val timestamp: Long,
    val oldState: String,
    val newState: String,
    override val data: T? = null
) : ChartEntry<T>

data class SingleStateEntry<T>(
    override val timestamp: Long,
    val state: String,
    override val data: T? = null
) : ChartEntry<T>