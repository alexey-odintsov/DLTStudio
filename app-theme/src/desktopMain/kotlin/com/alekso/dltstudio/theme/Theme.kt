package com.alekso.dltstudio.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

interface Theme {
    fun colorScheme(): ColorScheme

    @Composable
    fun typography(): Typography

    @Composable
    fun shapes(): Shapes
}

class LightTheme : Theme {
    override fun colorScheme(): ColorScheme {
        return lightColorScheme()
    }

    @Composable
    override fun typography(): Typography {
        return MaterialTheme.typography
    }

    @Composable
    override fun shapes(): Shapes {
        return MaterialTheme.shapes
    }
}

class DarkTheme : Theme {
    override fun colorScheme(): ColorScheme {
        return darkColorScheme()
    }

    @Composable
    override fun typography(): Typography {
        return MaterialTheme.typography
    }

    @Composable
    override fun shapes(): Shapes {
        return MaterialTheme.shapes
    }
}

class SystemTheme(
    val isDark: Boolean
) : Theme {
    override fun colorScheme(): ColorScheme {
        return if (isDark) darkColorScheme() else lightColorScheme()
    }

    @Composable
    override fun typography(): Typography {
        return MaterialTheme.typography
    }

    @Composable
    override fun shapes(): Shapes {
        return MaterialTheme.shapes
    }
}

