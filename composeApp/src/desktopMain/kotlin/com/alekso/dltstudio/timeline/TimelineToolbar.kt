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
import dtlstudio.composeapp.generated.resources.Res
import dtlstudio.composeapp.generated.resources.icon_color_filters
import dtlstudio.composeapp.generated.resources.icon_fit
import dtlstudio.composeapp.generated.resources.icon_left
import dtlstudio.composeapp.generated.resources.icon_right
import dtlstudio.composeapp.generated.resources.icon_run
import dtlstudio.composeapp.generated.resources.icon_stop
import dtlstudio.composeapp.generated.resources.icon_zoom_in
import dtlstudio.composeapp.generated.resources.icon_zoom_out

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
            icon = Res.drawable.icon_color_filters,
            title = "Timeline filters",
            onClick = onTimelineFiltersClicked
        )

        ImageButton(
            modifier = Modifier.size(32.dp),
            icon = if (analyzeState == AnalyzeState.IDLE) {
                Res.drawable.icon_run
            } else {
                Res.drawable.icon_stop
            },
            title = "Analyze timeline",
            onClick = onAnalyzeClick
        )
        HorizontalDivider(modifier = Modifier.height(32.dp))

        ImageButton(
            modifier = Modifier.size(32.dp),
            icon = Res.drawable.icon_left,
            title = "Move left",
            onClick = leftClick
        )

        ImageButton(
            modifier = Modifier.size(32.dp),
            icon = Res.drawable.icon_right,
            title = "Move right",
            onClick = rightClick
        )

        ImageButton(
            modifier = Modifier.size(32.dp),
            icon = Res.drawable.icon_zoom_in,
            title = "Zoom in",
            onClick = zoomInClick
        )

        ImageButton(
            modifier = Modifier.size(32.dp),
            icon = Res.drawable.icon_zoom_out,
            title = "Zoom out",
            onClick = zoomOutClick
        )

        ImageButton(
            modifier = Modifier.size(32.dp),
            icon = Res.drawable.icon_fit,
            title = "Fit timeline",
            onClick = zoomFitClick
        )

    }
}