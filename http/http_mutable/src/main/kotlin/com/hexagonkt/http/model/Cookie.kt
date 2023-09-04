package com.hexagonkt.http.model

import java.time.Instant

data class Cookie(
    var name: String,
    var value: String = "",
    var maxAge: Long = -1,
    var secure: Boolean = false,
    var path: String = "/",
    var httpOnly: Boolean = true,
    var domain: String = "",
    var sameSite: Boolean = true,
    var expires: Instant? = null,
) {
    val deleted: Boolean by lazy { value == "" && maxAge <= 0L }

    init {
        require(name.isNotBlank()) { "Cookie name can not be blank: $name" }
    }

    fun delete(): Cookie =
        copy(value = "", maxAge = 0)
}
