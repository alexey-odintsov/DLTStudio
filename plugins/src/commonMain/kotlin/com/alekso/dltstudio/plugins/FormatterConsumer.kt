package com.alekso.dltstudio.plugins

import com.alekso.dltstudio.model.contract.Formatter

interface FormatterConsumer {
    fun initFormatter(formatter: Formatter)
}