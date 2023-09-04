package com.hexagonkt.http.model

import com.hexagonkt.http.checkHeaders
import com.hexagonkt.http.model.ws.WsSession

data class HttpResponse(
    override var body: Any = "",
    override var headers: Headers = Headers(),
    override var contentType: ContentType? = null,
    override var cookies: List<Cookie> = emptyList(),
    override var status: HttpStatus = NOT_FOUND_404,
    override var contentLength: Long = -1L,
    override var onConnect: WsSession.() -> Unit = {},
    override var onBinary: WsSession.(data: ByteArray) -> Unit = {},
    override var onText: WsSession.(text: String) -> Unit = {},
    override var onPing: WsSession.(data: ByteArray) -> Unit = {},
    override var onPong: WsSession.(data: ByteArray) -> Unit = {},
    override var onClose: WsSession.(status: Int, reason: String) -> Unit = { _, _ -> },
) : HttpResponsePort {

    init {
        checkHeaders(headers)
    }

    override fun with(
        status: HttpStatus,
        body: Any,
        headers: Headers,
        contentType: ContentType?,
        cookies: List<Cookie>,
        onConnect: WsSession.() -> Unit,
        onBinary: WsSession.(data: ByteArray) -> Unit,
        onText: WsSession.(text: String) -> Unit,
        onPing: WsSession.(data: ByteArray) -> Unit,
        onPong: WsSession.(data: ByteArray) -> Unit,
        onClose: WsSession.(status: Int, reason: String) -> Unit,
    ): HttpResponsePort =
        copy(
            status = status,
            body = body,
            headers = headers,
            contentType = contentType,
            cookies = cookies,
            onConnect = onConnect,
            onBinary = onBinary,
            onText = onText,
            onPing = onPing,
            onPong = onPong,
            onClose = onClose,
        )
}
