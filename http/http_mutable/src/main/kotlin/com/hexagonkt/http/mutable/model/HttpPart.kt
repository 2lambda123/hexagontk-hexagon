package com.hexagonkt.http.mutable.model

data class HttpPart (
    var name: String,
    override var body: Any,
    override var headers: Headers = Headers(),
    override var contentType: ContentType? = null,
    var size: Long = -1L,
    var submittedFileName: String? = null
) : HttpBase {

    constructor(name: String, value: String) :
        this(name, value, size = value.toByteArray().size.toLong())

    constructor(name: String, body: ByteArray, submittedFileName: String) :
        this(name, body, size = body.size.toLong(), submittedFileName = submittedFileName)
}
