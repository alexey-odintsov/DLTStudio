package com.alekso.dltstudio.uicomponents

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.impl.Stats


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomEditText(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {

    val textAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top

    BasicTextField(
        modifier = modifier.padding(start = 4.dp, top = 0.dp, end = 2.dp, bottom = 0.dp),
        singleLine = singleLine,
        decorationBox = { innerTextField ->

            TextFieldDefaults.OutlinedTextFieldDecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = Stats.enabled,
                singleLine = false,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                    top = 2.dp,
                    bottom = 2.dp,
                    start = 4.dp,
                    end = 2.dp
                ),
                border = {
                    Box(
                        modifier = Modifier.border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp).wrapContentHeight(align = textAlignment)
                    ) {
                        innerTextField()
                    }
                }
            )
        },

        value = value,
        onValueChange = onValueChange
    )
}

@Preview
@Composable
fun PreviewDesktopEditText() {
    Column(modifier = Modifier.background(Color.Gray).padding(10.dp)) {
        CustomEditText("Hello", {})
        for (i in 6..15) {
            Row {
                Text(text = "${i * 2}")
                CustomEditText(
                    modifier = Modifier.width(100.dp).height((i * 3).dp),
                    value = "Hello2", onValueChange = {}
                )
            }
        }
    }
}