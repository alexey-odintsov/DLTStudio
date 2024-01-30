package com.alekso.dltstudio.ui.logs

import com.alekso.dltparser.dlt.DLTMessage

data class CellColorFilter(
    val condition: (DLTMessage) -> Boolean,
    val cellStyle: CellStyle
)