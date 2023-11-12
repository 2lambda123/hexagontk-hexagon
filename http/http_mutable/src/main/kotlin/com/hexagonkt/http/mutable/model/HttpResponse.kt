package com.hexagonkt.http.mutable.model

import com.hexagonkt.http.mutable.checkHeaders
import com.hexagonkt.http.mutable.model.ws.WsSession

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
}
