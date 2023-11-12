package com.hexagonkt.http.mutable.model

import com.hexagonkt.http.mutable.model.ws.WsSession

interface HttpResponsePort : HttpMessage {
    var status: HttpStatus

    var contentLength: Long                        // length of response.body (or 0)

    var onConnect: WsSession.() -> Unit
    var onBinary: WsSession.(data: ByteArray) -> Unit
    var onText: WsSession.(text: String) -> Unit
    var onPing: WsSession.(data: ByteArray) -> Unit
    var onPong: WsSession.(data: ByteArray) -> Unit
    var onClose: WsSession.(status: Int, reason: String) -> Unit

    operator fun plus(header: Header) {
        this.headers += header
    }

    operator fun plus(cookie: Cookie) {
        this.cookies += cookie
    }

    operator fun plus(headers: Headers) {
        this.headers += headers
    }

    operator fun plus(cookies: List<Cookie>) {
        this.cookies += cookies
    }
}
