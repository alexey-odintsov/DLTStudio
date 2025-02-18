package com.alekso.dltstudio.timeline

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.timeline.filters.AnalyzeState
import com.alekso.dltstudio.ui.ImageButton
import com.alekso.dltstudio.uicomponents.HorizontalDivider
import com.alekso.dltstudio.uicomponents.Tooltip
import dltstudio.desktop_app.generated.resources.Res
import dltstudio.desktop_app.generated.resources.icon_color_filters
import dltstudio.desktop_app.generated.resources.icon_fit
import dltstudio.desktop_app.generated.resources.icon_left
import dltstudio.desktop_app.generated.resources.icon_right
import dltstudio.desktop_app.generated.resources.icon_run
import dltstudio.desktop_app.generated.resources.icon_stop
import dltstudio.desktop_app.generated.resources.icon_zoom_in
import dltstudio.desktop_app.generated.resources.icon_zoom_out

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
        Tooltip(text = "Manage timeline filters") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_color_filters,
                title = "Timeline filters",
                onClick = onTimelineFiltersClicked
            )
        }
        Tooltip(text = "Start/Stop timeline analyzing") {
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
        }
        HorizontalDivider(modifier = Modifier.height(32.dp))

        Tooltip(text = "Move offset to the left") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_left,
                title = "Move left",
                onClick = leftClick
            )
        }
        Tooltip(text = "Move offset to the right") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_right,
                title = "Move right",
                onClick = rightClick
            )
        }

        Tooltip(text = "Zoom in") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_zoom_in,
                title = "Zoom in",
                onClick = zoomInClick
            )
        }
        Tooltip(text = "Zoom out") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_zoom_out,
                title = "Zoom out",
                onClick = zoomOutClick
            )
        }
        Tooltip(text = "Fit timeline") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_fit,
                title = "Fit timeline",
                onClick = zoomFitClick
            )
        }
    }
}