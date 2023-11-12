package com.hexagonkt.http.mutable.model

import kotlin.collections.Map.Entry

data class Headers(
    var httpFields: Map<String, Header>
) : Map<String, Header> {

    constructor(fields: List<Header>) : this(fields.associateBy { it.name.lowercase() })

    constructor(vararg fields: Header) : this(fields.toList())

    operator fun plus(element: Header): Headers =
        copy(httpFields = httpFields + (element.name to element))

    operator fun plus(element: Headers): Headers =
        copy(httpFields = httpFields + element.httpFields)

    operator fun minus(name: String): Headers =
        copy(httpFields = httpFields - name.lowercase())

    override operator fun get(key: String): Header? =
        httpFields[key.lowercase()]

    override val entries: Set<Entry<String, Header>>
        get() = httpFields.entries

    override val keys: Set<String>
        get() = httpFields.keys

    override val size: Int
        get() = httpFields.size

    override val values: Collection<Header>
        get() = httpFields.values

    override fun isEmpty(): Boolean =
        httpFields.isEmpty()

    override fun containsValue(value: Header): Boolean =
        httpFields.containsValue(value)

    override fun containsKey(key: String): Boolean =
        httpFields.containsKey(key)
}
