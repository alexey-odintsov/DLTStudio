package com.alekso.dltstudio.graphs.model

interface Key {
    val key: String
}

interface Value {
    val timestamp: Long
    val data: Any?
}

data class StringKey(
    override val key: String
) : Key

data class NumericalValue(
    val value: Float,
    override val timestamp: Long,
    override val data: Any?
) : Value

data class EventValue(
    override val timestamp: Long,
    override val data: Any?
) : Value

data class SingleEventValue(
    override val timestamp: Long,
    override val data: Any?,
    val state: String,
) : Value

data class DurationValue(
    override val timestamp: Long,
    override val data: Any?,
    val timestamp2: Long
) : Value

