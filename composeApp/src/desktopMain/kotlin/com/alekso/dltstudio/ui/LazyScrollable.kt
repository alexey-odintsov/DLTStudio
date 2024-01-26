package com.alekso.dltstudio.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)

@Composable
fun LazyScrollable(dltSession: ParseSession) {
    Box(
        modifier = Modifier.fillMaxSize()
            //.background(color = Color(180, 180, 180))
            .padding(10.dp)
    ) {

        val state = rememberLazyListState()

        LogRow(
            "#",
            "DateTime",
            "Time",
            "ecu",
            "ecuId",
            "sessId",
            "appId",
            "ctxId",
            "content", true
        )

        LazyColumn(Modifier.fillMaxSize().padding(top = 20.dp, end = 12.dp), state) {
            items(dltSession.dltMessages.size) { i ->
                val message = dltSession.dltMessages[i]
                LogRow(
                    i.toString(),
                    simpleDateFormat.format(message.timeStampSec * 1000L + message.timeStampUs / 1000),
                    if (message.standardHeader.timeStamp != null) "%.4f".format(message.standardHeader.timeStamp!! / 10000f) else "-",
                    message.ecuId,
                    "${message.standardHeader.ecuId}",
                    "${message.standardHeader.sessionId}",
                    "${message.extendedHeader?.applicationId}",
                    "${message.extendedHeader?.contextId}",
                    "${message.payload?.asText()}"
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}