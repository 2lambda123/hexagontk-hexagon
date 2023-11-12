package com.hexagonkt.http.mutable.model

import kotlin.collections.Map.Entry

data class FormParameters(
    var httpFields: Map<String, FormParameter>
) : Map<String, FormParameter> {

    constructor(fields: List<FormParameter>) : this(fields.associateBy { it.name })

    constructor(vararg fields: FormParameter) : this(fields.toList())

    operator fun plus(element: FormParameter): FormParameters =
        copy(httpFields = httpFields + (element.name to element))

    operator fun plus(element: FormParameters): FormParameters =
        copy(httpFields = httpFields + element.httpFields)

    operator fun minus(name: String): FormParameters =
        copy(httpFields = httpFields - name)

    override val entries: Set<Entry<String, FormParameter>>
        get() = httpFields.entries

    override val keys: Set<String>
        get() = httpFields.keys

    override val size: Int
        get() = httpFields.size

    override val values: Collection<FormParameter>
        get() = httpFields.values

    override fun isEmpty(): Boolean =
        httpFields.isEmpty()

    override fun get(key: String): FormParameter? =
        httpFields[key]

    override fun containsValue(value: FormParameter): Boolean =
        httpFields.containsValue(value)

    override fun containsKey(key: String): Boolean =
        httpFields.containsKey(key)
}
