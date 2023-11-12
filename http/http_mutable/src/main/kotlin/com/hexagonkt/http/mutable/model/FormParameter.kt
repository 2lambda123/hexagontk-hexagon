package com.hexagonkt.http.mutable.model

data class FormParameter(
    override var name: String,
    override var values: List<Any>,
) : HttpField {

    override var value: Any? = values.firstOrNull()

    constructor(name: String, vararg values: Any) : this(name, values.map(Any::toString))

    override operator fun plus(value: Any): FormParameter =
        copy(values = values + value.toString())

    override operator fun minus(element: Any): FormParameter =
        copy(values = values - element.toString())
}
