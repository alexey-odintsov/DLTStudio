package com.alekso.dltstudio.timeline

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.timeline.filters.AnalyzeState
import com.alekso.dltstudio.ui.HorizontalDivider
import com.alekso.dltstudio.ui.ImageButton

@Composable
fun TimelineToolbar(
    analyzeState: AnalyzeState,
    onAnalyzeClick: () -> Unit,
    leftClick: () -> Unit,
    rightClick: () -> Unit,
    zoomInClick: () -> Unit,
    zoomOutClick: () -> Unit,
    zoomFitClick: () -> Unit,
    onTimelineFiltersClicked: () -> Unit,
) {

    Row {

        ImageButton(
            modifier = Modifier.size(32.dp),
            iconName = "icon_color_filters.xml",
            title = "Timeline filters",
            onClick = onTimelineFiltersClicked
        )

        ImageButton(
            modifier = Modifier.size(32.dp),
            iconName = if (analyzeState == AnalyzeState.IDLE) {
                "icon_run.xml"
            } else {
                "icon_stop.xml"
            },
            title = "Analyze timeline",
            onClick = onAnalyzeClick
        )
        HorizontalDivider(modifier = Modifier.height(32.dp))

        ImageButton(
            modifier = Modifier.size(32.dp),
            iconName = "icon_left.xml",
            title = "Move left",
            onClick = leftClick
        )

        ImageButton(
            modifier = Modifier.size(32.dp),
            iconName = "icon_right.xml",
            title = "Move right",
            onClick = rightClick
        )

        ImageButton(
            modifier = Modifier.size(32.dp),
            iconName = "icon_zoom_in.xml",
            title = "Zoom in",
            onClick = zoomInClick
        )

        ImageButton(
            modifier = Modifier.size(32.dp),
            iconName = "icon_zoom_out.xml",
            title = "Zoom out",
            onClick = zoomOutClick
        )

        ImageButton(
            modifier = Modifier.size(32.dp),
            iconName = "icon_fit.xml",
            title = "Fit timeline",
            onClick = zoomFitClick
        )

    }
}