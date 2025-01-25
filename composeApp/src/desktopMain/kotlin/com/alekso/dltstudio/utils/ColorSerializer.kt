package com.alekso.dltstudio.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.alekso.dltparser.toHex
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Color) {
        val string = value.toArgb().toHex(4)
        encoder.encodeString(string)
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun deserialize(decoder: Decoder): Color {
        val string = decoder.decodeString()
        return Color(string.hexToUInt().toInt())
    }
}