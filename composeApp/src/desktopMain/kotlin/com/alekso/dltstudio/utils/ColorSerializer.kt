package com.alekso.dltstudio.utils

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

@OptIn(ExperimentalSerializationApi::class)
object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Color") {
            element("value", Long.serializer().descriptor)
        }

    override fun serialize(encoder: Encoder, value: Color) {
        val jsonEncoder = encoder as? kotlinx.serialization.json.JsonEncoder
            ?: throw SerializationException("This serializer only works with JSON format")

        val json = JsonObject(mapOf("value" to JsonPrimitive(value.value)))
        jsonEncoder.encodeJsonElement(json)
    }

    override fun deserialize(decoder: Decoder): Color {
        val jsonDecoder = decoder as? kotlinx.serialization.json.JsonDecoder
            ?: throw SerializationException("This serializer only works with JSON format")

        val element = jsonDecoder.decodeJsonElement() as? JsonObject
            ?: throw SerializationException("Expected JsonObject for Color")

        val value = element["value"]?.jsonPrimitive?.longOrNull
            ?: throw SerializationException("Expected 'value' as Long")

        return Color(value)
    }
}