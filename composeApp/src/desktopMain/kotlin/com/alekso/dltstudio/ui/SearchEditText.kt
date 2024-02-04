package com.alekso.dltstudio.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SearchEditText(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.White,
        focusedIndicatorColor = Color.Gray
    )
) {
    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    @OptIn(ExperimentalMaterialApi::class)
    (BasicTextField(
        value = value,
        modifier = modifier
            //.background(colors.backgroundColor(enabled).value, shape)
            .indicatorLine(
                enabled, isError, interactionSource, colors,
                focusedIndicatorLineThickness = 0.dp,
                unfocusedIndicatorLineThickness = 0.dp
            )
            .defaultMinSize(
                minWidth = 100.dp,
                minHeight = 30.dp
            ).padding(start = 1.dp, top = 6.dp, end = 1.dp, bottom = 6.dp),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(colors.cursorColor(isError).value),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = true,
        maxLines = 1,
        minLines = 1,
        decorationBox = @Composable { innerTextField ->
            // places leading icon, text field with label and placeholder, trailing icon
            TextFieldDefaults.OutlinedTextFieldDecorationBox(
                value = value,
                visualTransformation = visualTransformation,
                border = {
                    TextFieldDefaults.BorderBox(
                        enabled = enabled,
                        isError = false,
                        colors = colors,
                        interactionSource = interactionSource,
                        shape = RoundedCornerShape(4.dp),
                        unfocusedBorderThickness = 1.dp,
                        focusedBorderThickness = 1.dp
                    )
                },
                innerTextField = innerTextField,
                placeholder = placeholder,
                label = label,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                singleLine = true,
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors,
                contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                    top = 0.dp,
                    bottom = 0.dp,
                    start = 2.dp,
                    end = 2.dp
                )
            )
        }
    ))
}

@Preview
@Composable
fun PreviewSearchEditText() {
    SearchEditText(modifier = Modifier.width(100.dp).height(32.dp),
        value = "Test One Two Three", onValueChange = {})
}