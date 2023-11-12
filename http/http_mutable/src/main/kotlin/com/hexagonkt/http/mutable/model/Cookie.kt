package com.hexagonkt.http.mutable.model

import java.time.Instant

/**
 * TODO .
 *
 * @property name
 * @property value
 * @property maxAge '-1' is the same as empty
 * @property secure
 * @property path '/' is the same as empty
 * @property httpOnly
 * @property domain
 * @property sameSite
 * @property expires
 */
data class Cookie(
    var name: String,
    var value: String = "",
    var maxAge: Long = -1,
    var secure: Boolean = false,
    var path: String = "/",
    var httpOnly: Boolean = true,
    var domain: String = "",
    var sameSite: CookieSameSite? = null,
    var expires: Instant? = null,
) {
    val deleted: Boolean by lazy { value == "" && maxAge <= 0L }

    init {
        require(name.isNotBlank()) { "Cookie name can not be blank: $name" }
    }

    fun delete(): Cookie =
        copy(value = "", maxAge = 0)
}
