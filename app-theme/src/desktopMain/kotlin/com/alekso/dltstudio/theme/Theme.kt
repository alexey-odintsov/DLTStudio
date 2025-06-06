package com.alekso.dltstudio.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

interface Theme {
    fun colorScheme(): ColorScheme

    fun colors(): Colors

    @Composable
    fun typography(): Typography

    @Composable
    fun shapes(): Shapes
}
