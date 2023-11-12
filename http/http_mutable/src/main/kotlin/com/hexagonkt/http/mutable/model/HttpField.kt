package com.hexagonkt.http.mutable.model

/**
 * HTTP multi-value field. Used in headers, query parameters and form parameters.
 */
interface HttpField {
    var name: String
    var value: Any?
    var values: List<Any>

    fun string(): String? =
        value?.toString()

    fun strings(): List<String> =
        values.map(Any::toString)

    operator fun plus(value: Any): HttpField

    operator fun minus(element: Any): HttpField
}
