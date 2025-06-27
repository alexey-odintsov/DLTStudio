package com.alekso.dltstudio.model.contract.filtering

enum class TextCriteria {
    PlainText,
    LowerCase,
    Regex
}

fun checkTextCriteria(criteria: FilterCriteria, message: String?): Boolean {
    if (criteria.value == "") return true
    return when (criteria.textCriteria) {
        TextCriteria.PlainText -> message?.contains(criteria.value) ?: false
        TextCriteria.LowerCase -> message?.lowercase()?.contains(criteria.value) ?: false
        TextCriteria.Regex -> try {
            message?.contains(Regex(criteria.value))
        } catch (e: Exception) {
            false
        } ?: false
    }
}
