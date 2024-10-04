package com.hexagontk.http.mutable

import com.hexagontk.core.assertEnabled
import com.hexagontk.core.Platform
import com.hexagontk.core.error
import com.hexagontk.core.loggerOf
import com.hexagontk.core.media.MediaType
import com.hexagontk.http.mutable.model.*
import java.lang.IllegalStateException
import java.lang.System.Logger
import java.math.BigInteger
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME

val CHECKED_HEADERS: List<String> = listOf("content-type", "accept", "set-cookie", "authorization")

val GMT_ZONE: ZoneId = ZoneId.of("GMT")

val HTTP_DATE_FORMATTER: DateTimeFormatter = RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC)

val BODY_TYPES = setOf(String::class, ByteArray::class, Int::class, Long::class)

val BODY_TYPES_NAMES = BODY_TYPES.joinToString(", ") { it.simpleName.toString() }

private val logger: Logger = loggerOf(SslSettings::class.java.packageName)

fun checkHeaders(headers: Headers) {
    if (!assertEnabled)
        return

    val headersKeys = headers.httpFields.keys
    val invalidHeaders = CHECKED_HEADERS.filter { headersKeys.contains(it) }

    check(invalidHeaders.isEmpty()) {
        val invalidHeadersText = invalidHeaders.joinToString(",") { "'$it'" }

        """
        Special headers should be handled with their respective properties (i.e.: contentType)
        instead setting them in the headers map. Ignored headers: $invalidHeadersText
        """.trimIndent()
    }
}

/**
 * Parse query string such as `paramA=valueA&paramB=valueB` into a map of several key-value pairs
 * separated by '&' where *key* is the param name before '=' as String and *value* is the string
 * after '=' as a list of String (as a query parameter may have many values).
 *
 * Note: Missing the '=' sign, or missing value after '=' (e.g. `foo=` or `foo`) will result into an
 * empty string value.
 *
 * @param query URL query string. E.g.: `param=value&foo=bar`.
 * @return Map with query parameter keys bound to a list with their values.
 *
 */
fun parseQueryString(query: String): QueryParameters =
    if (query.isBlank())
        QueryParameters()
    else
        QueryParameters(
            query
                .split("&".toRegex())
                .map {
                    val keyValue = it.split("=").map(String::trim)
                    val key = keyValue[0]
                    val value = if (keyValue.size == 2) keyValue[1] else ""
                    key.urlDecode() to value.urlDecode()
                }
                .filter { it.first.isNotBlank() }
                .groupBy { it.first }
                .mapValues { pair -> pair.value.map { it.second } }
                .map { (k, v) -> QueryParameter(k, v) }
        )

fun formatQueryString(parameters: QueryParameters): String =
    parameters
        .flatMap { (k, v) -> v.strings().map { k to it } }
        .filter { it.first.isNotBlank() }
        .joinToString("&") { (k, v) ->
            if (v.isBlank()) k.urlEncode()
            else "${k.urlEncode()}=${v.urlEncode()}"
        }

fun String.urlDecode(): String =
    URLDecoder.decode(this, Platform.charset)

fun String.urlEncode(): String =
    URLEncoder.encode(this, Platform.charset)

fun LocalDateTime.toHttpFormat(): String =
    HTTP_DATE_FORMATTER.format(ZonedDateTime.of(this, Platform.zoneId).withZoneSameInstant(GMT_ZONE))

fun Instant.toHttpFormat(): String =
    HTTP_DATE_FORMATTER.format(this)

fun parseContentType(contentType: String): com.hexagontk.http.mutable.model.ContentType {
    val typeParameter = contentType.split(";")
    val fullType = typeParameter.first().trim()
    val mimeType = MediaType(fullType)

    return when (typeParameter.size) {
        1 -> com.hexagontk.http.mutable.model.ContentType(mimeType)
        2 -> {
            val parameter = typeParameter.last()
            val nameValue = parameter.split("=")
            if (nameValue.size != 2)
                error("Invalid content type format: $contentType")

            val name = nameValue.first().trim()
            val value = nameValue.last().trim()

            when (name.trim().lowercase()) {
                "boundary" -> com.hexagontk.http.mutable.model.ContentType(mimeType, boundary = value)
                "charset" -> com.hexagontk.http.mutable.model.ContentType(mimeType, charset = Charset.forName(value))
                "q" -> com.hexagontk.http.mutable.model.ContentType(mimeType, q = value.toDouble())
                else -> error("Invalid content type format: $contentType")
            }
        }
        else -> error("Invalid content type format: $contentType")
    }
}

fun bodyToBytes(body: Any): ByteArray =
    when (body) {
        is String -> body.toByteArray()
        is ByteArray -> body
        is Int -> BigInteger.valueOf(body.toLong()).toByteArray()
        is Long -> BigInteger.valueOf(body).toByteArray()
        else -> {
            val className = body.javaClass.simpleName
            val message = "Unsupported body type: $className. Must be: $BODY_TYPES_NAMES"
            val exception = IllegalStateException(message)

            logger.error(exception)
            throw exception
        }
    }
