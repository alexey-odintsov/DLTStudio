package com.alekso.dltstudio.logs.filtering

import kotlinx.serialization.Serializable

@Serializable
data class FilterCriteria(
    val value: String,
    val textCriteria: TextCriteria = TextCriteria.PlainText
)