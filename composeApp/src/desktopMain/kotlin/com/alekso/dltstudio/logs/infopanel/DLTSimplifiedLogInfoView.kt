package com.alekso.dltstudio.logs.infopanel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltstudio.TimeFormatter

@Composable
fun DLTSimplifiedInfoView(
    modifier: Modifier = Modifier,
    dltMessage: DLTMessage?,
    messageIndex: Int
) {
    val paddingModifier = Modifier.padding(start = 4.dp, end = 4.dp)

    SelectionContainer {
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            if (dltMessage != null) {
                Header(
                    modifier = paddingModifier,
                    text = "DLT Message #$messageIndex:"
                )
                val headerText = "${TimeFormatter.formatDateTime(dltMessage.timeStampNano)} " +
                        "${dltMessage.extendedHeader?.applicationId} " +
                        "${dltMessage.extendedHeader?.contextId} "
                TableRow(0, "", headerText)
                TableRow(0, "", dltMessage.payload)
            }
        }
    }
}