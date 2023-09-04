package com.hexagonkt.http.model

import com.hexagonkt.http.model.ws.WsSession

interface HttpResponsePort : HttpMessage {
    var status: HttpStatus

    var contentLength: Long                        // length of response.body (or 0)

    var onConnect: WsSession.() -> Unit
    var onBinary: WsSession.(data: ByteArray) -> Unit
    var onText: WsSession.(text: String) -> Unit
    var onPing: WsSession.(data: ByteArray) -> Unit
    var onPong: WsSession.(data: ByteArray) -> Unit
    var onClose: WsSession.(status: Int, reason: String) -> Unit

    fun with(
        status: HttpStatus = this.status,
        body: Any = this.body,
        headers: Headers = this.headers,
        contentType: ContentType? = this.contentType,
        cookies: List<Cookie> = this.cookies,
        onConnect: WsSession.() -> Unit = this.onConnect,
        onBinary: WsSession.(data: ByteArray) -> Unit = this.onBinary,
        onText: WsSession.(text: String) -> Unit = this.onText,
        onPing: WsSession.(data: ByteArray) -> Unit = this.onPing,
        onPong: WsSession.(data: ByteArray) -> Unit = this.onPong,
        onClose: WsSession.(status: Int, reason: String) -> Unit = this.onClose,
    ): HttpResponsePort

    operator fun plus(header: Header): HttpResponsePort =
        with(headers = headers + header)

    operator fun plus(cookie: Cookie): HttpResponsePort =
        with(cookies = cookies + cookie)

    operator fun plus(headers: Headers): HttpResponsePort =
        with(headers = this.headers + headers)

    operator fun plus(cookies: List<Cookie>): HttpResponsePort =
        with(cookies = this.cookies + cookies)
}
