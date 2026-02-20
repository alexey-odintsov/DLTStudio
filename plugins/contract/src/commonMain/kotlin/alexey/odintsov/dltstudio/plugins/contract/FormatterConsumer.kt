package alexey.odintsov.dltstudio.plugins.contract

import alexey.odintsov.dltstudio.model.contract.Formatter

interface FormatterConsumer {
    fun initFormatter(formatter: Formatter)
}