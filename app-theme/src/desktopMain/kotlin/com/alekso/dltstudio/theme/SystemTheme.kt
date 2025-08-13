package com.alekso.dltstudio.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Default app theme that automatically applies dark mode based on the system settings.
 *
 * https://m3.material.io/styles/color/roles
 *
 * Surface – A role used for backgrounds and large, low-emphasis areas of the screen.
 *      Use surface roles for more neutral backgrounds, and container colors for components like cards, sheets, and dialogs.
 *
 * Primary, Secondary, Tertiary – Accent color roles used to emphasize or de-emphasize foreground elements.
 *      Primary roles are for important actions and elements needing the most emphasis, like a FAB to start a new message.
 *      Secondary roles are for elements that don’t need immediate attention and don’t need emphasis, like the selected state of a navigation icon or a dismissive button.
 *      Tertiary roles are for smaller elements that need special emphasis but don't require immediate attention, such as a badge or notification.
 * Container – Roles used as a fill color for foreground elements like buttons. They should not be used for text or icons.
 * On – Roles starting with this term indicate a color for text or icons on top of its paired parent color. For example, on primary is used for text and icons against the primary fill color.
 * Variant – Roles ending with this term offer a lower emphasis alternative to its non-variant pair. For example, outline variant is a less emphasized version of the outline color.
 */
class SystemTheme(
    private val isDark: Boolean
) : Theme {

    // primary,
    val primaryLight = Color(0xFF415F91) // Accent + bg tone
    val onPrimaryLight = Color(0xFFFFFFFF)
    val primaryContainerLight = Color(0xFFD7E3FF) // FAB bg
    val onPrimaryContainerLight = Color(0xFF284777)

    // secondary
    val secondaryLight = Color(0xFF9d9db0)
    val onSecondaryLight = Color(0xFFFFFFFF)
    val secondaryContainerLight = Color(0xFFDAE2F9)
    val onSecondaryContainerLight = Color(0xFF3E4759)

    // tertiary
    val tertiaryLight = Color(0xFF1d47f0)
    val onTertiaryLight = Color(0xFFFFFFFF)
    val tertiaryContainerLight = Color(0xFFE4E892)
    val onTertiaryContainerLight = Color(0xFF464A02)

    // error
    val errorLight = Color(0xFFBA1A1A)
    val onErrorLight = Color(0xFFFFFFFF)
    val errorContainerLight = Color(0xFFFFDAD6)
    val onErrorContainerLight = Color(0xFF93000A)

    // surface & outline
    val backgroundLight = Color(0xFFFFFFFF) // ?
    val onBackgroundLight = Color(0xFF191C20)

    val surfaceLight = Color(0xFFF3F4F4) // background
    val onSurfaceLight = Color(0xFF191C20)
    val surfaceVariantLight = Color(0xFFE0E2EC)
    val onSurfaceVariantLight = Color(0xFF44474E)

    val outlineLight = Color(0xFF74777F) // Important boundaries, such as a text field outline
    val outlineVariantLight = Color(0xFFC4C6D0) // Decorative elements, such as dividers, and when other elements provide 4.5:1 contrast
    val scrimLight = Color(0xFF000000)
    val inverseSurfaceLight = Color(0xFF2E3036)
    val inverseOnSurfaceLight = Color(0xFFF0F0F7)
    val inversePrimaryLight = Color(0xFFAAC7FF)
    val surfaceDimLight = Color(0xFFD9D9E0)
    val surfaceBrightLight = Color(0xFFFFFFFF)

    val surfaceContainerLowestLight = Color(0xFFFFFFFF)
    val surfaceContainerLowLight = Color(0xFFF3F3FA)
    val surfaceContainerLight = Color(0xFFEDEDF4)
    val surfaceContainerHighLight = Color(0xFFE7E8EE)
    val surfaceContainerHighestLight = Color(0xFFE2E2E9)

    private val lightScheme = lightColorScheme(
        primary = primaryLight,
        onPrimary = onPrimaryLight,
        primaryContainer = primaryContainerLight,
        onPrimaryContainer = onPrimaryContainerLight,

        secondary = secondaryLight,
        onSecondary = onSecondaryLight,
        secondaryContainer = secondaryContainerLight,
        onSecondaryContainer = onSecondaryContainerLight,

        tertiary = tertiaryLight,
        onTertiary = onTertiaryLight,
        tertiaryContainer = tertiaryContainerLight,
        onTertiaryContainer = onTertiaryContainerLight,

        error = errorLight,
        onError = onErrorLight,
        errorContainer = errorContainerLight,
        onErrorContainer = onErrorContainerLight,

        background = backgroundLight,
        onBackground = onBackgroundLight,
        surface = surfaceLight,
        onSurface = onSurfaceLight,

        surfaceVariant = surfaceVariantLight,
        onSurfaceVariant = onSurfaceVariantLight,
        outline = outlineLight,
        outlineVariant = outlineVariantLight,

        scrim = scrimLight,
        inverseSurface = inverseSurfaceLight,
        inverseOnSurface = inverseOnSurfaceLight,
        inversePrimary = inversePrimaryLight,
        surfaceDim = surfaceDimLight,
        surfaceBright = surfaceBrightLight,
        surfaceContainerLowest = surfaceContainerLowestLight,
        surfaceContainerLow = surfaceContainerLowLight,
        surfaceContainer = surfaceContainerLight,
        surfaceContainerHigh = surfaceContainerHighLight,
        surfaceContainerHighest = surfaceContainerHighestLight,
    )

    val primaryDark = Color(0xFFAAC7FF)
    val onPrimaryDark = Color(0xFF0B305F)
    val primaryContainerDark = Color(0xFF284777)
    val onPrimaryContainerDark = Color(0xFFD7E3FF)
    val secondaryDark = Color(0xFFBEC6DC)
    val onSecondaryDark = Color(0xFF283141)
    val secondaryContainerDark = Color(0xFF3E4759)
    val onSecondaryContainerDark = Color(0xFFDAE2F9)
    val tertiaryDark = Color(0xFF5373f5)
    val onTertiaryDark = Color(0xFF303300)
    val tertiaryContainerDark = Color(0xFF464A02)
    val onTertiaryContainerDark = Color(0xFFE4E892)
    val errorDark = Color(0xFFFFB4AB)
    val onErrorDark = Color(0xFF690005)
    val errorContainerDark = Color(0xFF93000A)
    val onErrorContainerDark = Color(0xFFFFDAD6)
    val backgroundDark = Color(0xFF111318)
    val onBackgroundDark = Color(0xFFE2E2E9)
    val surfaceDark = Color(0xFF2b2d30)
    val onSurfaceDark = Color(0xFFE2E2E9)
    val surfaceVariantDark = Color(0xFF44474E)
    val onSurfaceVariantDark = Color(0xFFC4C6D0)
    val outlineDark = Color(0xFF8E9099)
    val outlineVariantDark = Color(0xFF44474E)
    val scrimDark = Color(0xFF000000)
    val inverseSurfaceDark = Color(0xFFE2E2E9)
    val inverseOnSurfaceDark = Color(0xFF2E3036)
    val inversePrimaryDark = Color(0xFF415F91)
    val surfaceDimDark = Color(0xFF111318)
    val surfaceBrightDark = Color(0xFF37393E)
    val surfaceContainerLowestDark = Color(0xFF0C0E13)
    val surfaceContainerLowDark = Color(0xFF191C20)
    val surfaceContainerDark = Color(0xFF1E2025)
    val surfaceContainerHighDark = Color(0xFF282A2F)
    val surfaceContainerHighestDark = Color(0xFF33353A)

    private val darkScheme = darkColorScheme(
        primary = primaryDark,
        onPrimary = onPrimaryDark,
        primaryContainer = primaryContainerDark,
        onPrimaryContainer = onPrimaryContainerDark,
        secondary = secondaryDark,
        onSecondary = onSecondaryDark,
        secondaryContainer = secondaryContainerDark,
        onSecondaryContainer = onSecondaryContainerDark,
        tertiary = tertiaryDark,
        onTertiary = onTertiaryDark,
        tertiaryContainer = tertiaryContainerDark,
        onTertiaryContainer = onTertiaryContainerDark,
        error = errorDark,
        onError = onErrorDark,
        errorContainer = errorContainerDark,
        onErrorContainer = onErrorContainerDark,
        background = backgroundDark,
        onBackground = onBackgroundDark,
        surface = surfaceDark,
        onSurface = onSurfaceDark,
        surfaceVariant = surfaceVariantDark,
        onSurfaceVariant = onSurfaceVariantDark,
        outline = outlineDark,
        outlineVariant = outlineVariantDark,
        scrim = scrimDark,
        inverseSurface = inverseSurfaceDark,
        inverseOnSurface = inverseOnSurfaceDark,
        inversePrimary = inversePrimaryDark,
        surfaceDim = surfaceDimDark,
        surfaceBright = surfaceBrightDark,
        surfaceContainerLowest = surfaceContainerLowestDark,
        surfaceContainerLow = surfaceContainerLowDark,
        surfaceContainer = surfaceContainerDark,
        surfaceContainerHigh = surfaceContainerHighDark,
        surfaceContainerHighest = surfaceContainerHighestDark,
    )

    override fun isDark(): Boolean {
        return isDark
    }

    override fun colorScheme(): ColorScheme {
        return if (isDark) darkScheme else lightScheme
    }

    override fun colors(): Colors {
        return if (isDark)
            Colors(
                logRow = Color(0xff1e1f22),
                onLogRow = Color(0xffbcbec3),
            )
        else
            Colors(
                logRow = Color.White,
                onLogRow = Color.Black,
            )
    }

    @Composable
    override fun typography(): Typography {
        return MaterialTheme.typography.copy(
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 14.sp, // Text, Badge, Tab
                lineHeight = 16.sp,
            ),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 14.sp,
            )
        )
    }

    @Composable
    override fun shapes(): Shapes {
        return MaterialTheme.shapes.copy(
            extraLarge = RoundedCornerShape(2.dp),
            medium = RoundedCornerShape(2.dp),
            small = RoundedCornerShape(2.dp),
            extraSmall = RoundedCornerShape(2.dp),

            // MaterialTheme.shapes.medium.copy()
        )
    }

}