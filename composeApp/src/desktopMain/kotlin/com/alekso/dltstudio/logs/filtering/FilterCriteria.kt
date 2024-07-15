package com.alekso.dltstudio.logs.filtering

data class FilterCriteria(
    val value: String,
    val textCriteria: TextCriteria = TextCriteria.PlainText
)