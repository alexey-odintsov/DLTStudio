package com.alekso.dltstudio.model.contract.filtering

import kotlinx.serialization.Serializable

@Serializable
data class FilterCriteria(
    val value: String,
    val textCriteria: TextCriteria = TextCriteria.PlainText
)