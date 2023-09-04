package com.hexagonkt.http.model

interface HttpBase {
    var body: Any
    var headers: Headers
    var contentType: ContentType?

    fun bodyString(): String =
        when (body) {
            is String -> body as String
            is ByteArray -> String(body as ByteArray)
            else -> body.toString()
        }
}
