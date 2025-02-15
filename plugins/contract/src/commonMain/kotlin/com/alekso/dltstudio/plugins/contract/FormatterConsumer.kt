package com.alekso.dltstudio.plugins.contract

import com.alekso.dltstudio.model.contract.Formatter

interface FormatterConsumer {
    fun initFormatter(formatter: Formatter)
}