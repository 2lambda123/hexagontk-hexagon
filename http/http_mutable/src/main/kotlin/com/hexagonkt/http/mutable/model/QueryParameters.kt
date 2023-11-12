package com.hexagonkt.http.mutable.model

import kotlin.collections.Map.Entry

data class QueryParameters(
    var httpFields: Map<String, QueryParameter>
) : Map<String, QueryParameter> {

    constructor(fields: List<QueryParameter>) : this(fields.associateBy { it.name })

    constructor(vararg fields: QueryParameter) : this(fields.toList())

    operator fun plus(element: QueryParameter): QueryParameters =
        copy(httpFields = httpFields + (element.name to element))

    operator fun plus(element: QueryParameters): QueryParameters =
        copy(httpFields = httpFields + element.httpFields)

    operator fun minus(name: String): QueryParameters =
        copy(httpFields = httpFields - name)

    override val entries: Set<Entry<String, QueryParameter>>
        get() = httpFields.entries

    override val keys: Set<String>
        get() = httpFields.keys

    override val size: Int
        get() = httpFields.size

    override val values: Collection<QueryParameter>
        get() = httpFields.values

    override fun isEmpty(): Boolean =
        httpFields.isEmpty()

    override fun get(key: String): QueryParameter? =
        httpFields[key]

    override fun containsValue(value: QueryParameter): Boolean =
        httpFields.containsValue(value)

    override fun containsKey(key: String): Boolean =
        httpFields.containsKey(key)
}
