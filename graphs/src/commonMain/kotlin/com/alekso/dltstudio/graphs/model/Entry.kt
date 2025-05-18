package com.alekso.dltstudio.graphs.model

interface Key {
    val key: String
}

interface Value {
    val timestamp: Long
}

interface PercentageValue : Value {
    val value: Float
}

interface NumericalValue : Value {
    val value: Number
}

