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
import java.text.SimpleDateFormat
import java.util.Locale

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
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
                val headerText = "${simpleDateFormat.format(dltMessage.getTimeStamp())} " +
                        "${dltMessage.extendedHeader?.applicationId} " +
                        "${dltMessage.extendedHeader?.contextId} "
                TableRow(0, "", headerText)
                if (dltMessage.payload != null) {
                    val payloadText = dltMessage.payload!!.asText()
                    TableRow(0, "", payloadText)
                }
            }
        }
    }
}