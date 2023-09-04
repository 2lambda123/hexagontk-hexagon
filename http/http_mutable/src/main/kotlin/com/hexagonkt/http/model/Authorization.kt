package com.hexagonkt.http.model

data class Authorization(
    var type: String,
    var value: String,
) {
    val text: String by lazy { "$type $value" }
}
