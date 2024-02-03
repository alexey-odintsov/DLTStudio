package com.alekso.dltstudio.ui.user

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
import com.alekso.dltstudio.ui.colors.ColorPalette

@Composable
fun UserStateLegend(modifier: Modifier, map: Map<Int, List<UserStateEntry>>) {
    Column(modifier = modifier.padding(start = 4.dp, end = 4.dp)) {
        Text(
            "User switch",
            fontWeight = FontWeight(600),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        if (map.isNotEmpty()) {
            map.entries.forEachIndexed { i, key ->
                Row {
                    Box(
                        modifier = Modifier.width(30.dp).height(6.dp).padding(end = 4.dp).align(
                            Alignment.CenterVertically
                        )
                            .background(ColorPalette.getColor(i))
                    )
                    Text(text = key.key.toString())
                }
            }
        }
    }
}