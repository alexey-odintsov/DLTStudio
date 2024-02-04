package com.alekso.dltstudio.cpu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.colors.ColorPalette

@Composable
fun CPUSLegend(modifier: Modifier, items: List<CPUSEntry>) {
    Column(modifier = modifier.padding(start = 4.dp, end = 4.dp)) {
        Text("CPU usage", fontWeight = FontWeight(600), modifier = Modifier.padding(bottom = 4.dp))
        if (items.isNotEmpty()) {
            items[0].entry.forEachIndexed { i, _ ->
                Row {
                    Box(
                        modifier = Modifier.width(30.dp).height(6.dp).padding(end = 4.dp).align(
                            Alignment.CenterVertically
                        )
                            .background(ColorPalette.getColor(i))
                    )
                    Text(
                        text = when (i) {
                            CPUS_ENTRY.CPU.ordinal -> "CPU usage"
                            CPUS_ENTRY.USER.ordinal -> "User"
                            CPUS_ENTRY.SYSTEM.ordinal -> "System"
                            CPUS_ENTRY.IO.ordinal -> "I/O"
                            CPUS_ENTRY.IRQ.ordinal -> "IRQ"
                            CPUS_ENTRY.SOFT_IRQ.ordinal -> "Soft IRQ"
                            CPUS_ENTRY.NI.ordinal -> "NI"
                            CPUS_ENTRY.ST.ordinal -> "ST"
                            CPUS_ENTRY.G.ordinal -> "G"
                            CPUS_ENTRY.GN.ordinal -> "GN"
                            CPUS_ENTRY.AVG_CPU.ordinal -> "Avg CPU"
                            CPUS_ENTRY.THREAD.ordinal -> "Thread"
                            CPUS_ENTRY.KERNEL_THREAD.ordinal -> "Kernel thread"
                            else -> "Unknown"
                        }
                    )
                }
            }
        }
    }
}