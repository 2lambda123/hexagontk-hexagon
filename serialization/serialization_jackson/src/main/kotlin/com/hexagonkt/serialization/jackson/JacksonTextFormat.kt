package com.hexagontk.serialization.jackson

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.util.DefaultIndenter.SYSTEM_LINEFEED_INSTANCE
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.hexagontk.serialization.SerializationFormat
import com.hexagontk.serialization.jackson.JacksonHelper.createObjectMapper
import com.hexagontk.serialization.jackson.JacksonHelper.mapNode
import java.io.InputStream
import java.io.OutputStream

abstract class JacksonTextFormat(
    factoryGenerator: () -> JsonFactory = { JsonFactory() },
    prettyPrint: Boolean = true,
) : SerializationFormat {

    private val mapper by lazy {
        createObjectMapper(factoryGenerator())
    }

    override val textFormat = true

    private val writer by lazy {
        if (prettyPrint)
            mapper.writer(DefaultPrettyPrinter().withArrayIndenter(SYSTEM_LINEFEED_INSTANCE))
        else
            mapper.writer()
    }

    override fun serialize(instance: Any, output: OutputStream) =
        writer.writeValue(output, instance)

    override fun serialize(instance: Any): String =
        writer.writeValueAsString(instance)

    override fun parse(input: InputStream): Any =
        mapNode(mapper.readTree(input))
}
