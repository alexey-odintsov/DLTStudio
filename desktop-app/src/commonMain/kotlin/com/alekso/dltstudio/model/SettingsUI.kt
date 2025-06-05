package com.alekso.dltstudio.model

import androidx.compose.ui.text.font.FontFamily
import com.alekso.dltstudio.db.settings.SettingsUIEntity

enum class SupportedFontFamilies(
    val id: Int,
    val fontFamily: FontFamily
) {
    Monospaced(0, FontFamily.Monospace),
    Serif(1, FontFamily.Serif),
    SanSerif(2, FontFamily.SansSerif);

    companion object {
        fun getFontFamilyById(id: Int): FontFamily {
            return entries.firstOrNull { it.id == id }?.fontFamily ?: Monospaced.fontFamily
        }

        fun getIdByFontFamily(fontFamily: FontFamily): Int {
            return entries.firstOrNull { it.fontFamily == fontFamily }?.id ?: 0
        }
    }
}

data class SettingsUI(
    val fontSize: Int,
    val fontFamily: FontFamily,
    val lineHeight: Float = 1f,
) {
    companion object {
        val Default = SettingsUI(
            fontSize = 10,
            fontFamily = FontFamily.Monospace
        )
    }
}

fun SettingsUIEntity.toSettingsUI() =
    SettingsUI(fontSize, SupportedFontFamilies.getFontFamilyById(fontType))

fun SettingsUI.toSettingsUIEntity() =
    SettingsUIEntity(fontSize, SupportedFontFamilies.getIdByFontFamily(fontFamily))
