package com.alekso.dltstudio.logs.filtering

enum class TextCriteria {
    PlainText,
    LowerCase,
    Regex
}

fun checkTextCriteria(criteria: FilterCriteria, message: String?) =
    when (criteria.textCriteria) {
        TextCriteria.PlainText -> message?.contains(criteria.value) ?: false
        TextCriteria.LowerCase -> message?.lowercase()?.contains(criteria.value) ?: false
        TextCriteria.Regex -> message?.contains(criteria.value.toRegex()) ?: false
    }
