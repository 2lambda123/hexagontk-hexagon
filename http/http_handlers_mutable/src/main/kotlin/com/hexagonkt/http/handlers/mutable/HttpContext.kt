package com.hexagonkt.http.handlers.mutable

import com.hexagonkt.handlers.mutable.Context
import com.hexagonkt.core.assertEnabled
import com.hexagonkt.core.media.TEXT_EVENT_STREAM
import com.hexagonkt.core.media.TEXT_PLAIN
import com.hexagonkt.core.toText
import com.hexagonkt.handlers.mutable.Handler
import com.hexagonkt.http.mutable.model.*
import com.hexagonkt.http.mutable.model.INTERNAL_SERVER_ERROR_500
import com.hexagonkt.http.mutable.model.ServerEvent
import com.hexagonkt.http.mutable.model.ws.WsSession
import java.net.URL
import java.security.cert.X509Certificate
import java.util.concurrent.Flow.Publisher

data class HttpContext(
    override var event: HttpCall,
    override var predicate: (Context<HttpCall>) -> Boolean,
    override var nextHandlers: List<Handler<HttpCall>> = emptyList(),
    override var nextHandler: Int = 0,
    override var exception: Exception? = null,
    override var attributes: Map<*, *> = emptyMap<Any, Any>(),
    override var handled: Boolean = false,
): Context<HttpCall> {
    val request: HttpRequestPort = event.request
    val response: HttpResponsePort = event.response

    val method: HttpMethod by lazy { request.method }
    val protocol: HttpProtocol by lazy { request.protocol }
    val host: String by lazy { request.host }
    val port: Int by lazy { request.port }
    val path: String by lazy { request.path }
    val queryParameters: QueryParameters by lazy { request.queryParameters }
    val parts: List<HttpPart> by lazy { request.parts }
    val formParameters: FormParameters by lazy { request.formParameters }
    val accept: List<ContentType> by lazy { request.accept }
    val authorization: Authorization? by lazy { request.authorization }
    val certificateChain: List<X509Certificate> by lazy { request.certificateChain }

    val partsMap: Map<String, HttpPart> by lazy { request.partsMap() }
    val url: URL by lazy { request.url() }
    val userAgent: String? by lazy { request.userAgent() }
    val referer: String? by lazy { request.referer() }
    val origin: String? by lazy { request.origin() }
    val certificate: X509Certificate? by lazy { request.certificate() }

    val status: HttpStatus = response.status

    val pathParameters: Map<String, String> by lazy {
        val httpHandler = predicate as HttpPredicate
        val pattern = httpHandler.pathPattern

        if (assertEnabled)
            check(!pattern.prefix) { "Loading path parameters not allowed for paths" }

        pattern.extractParameters(request.path)
    }

    constructor(context: Context<HttpCall>) : this(
        event = context.event,
        predicate = context.predicate,
        nextHandlers = context.nextHandlers,
        nextHandler = context.nextHandler,
        exception = context.exception,
        attributes = context.attributes,
    )

    constructor(
        request: HttpRequestPort = HttpRequest(),
        response: HttpResponsePort = HttpResponse(),
        predicate: HttpPredicate = HttpPredicate(),
        attributes: Map<*, *> = emptyMap<Any, Any>(),
    ) : this(HttpCall(request, response), predicate, attributes = attributes)

    override fun next() {
        for (index in nextHandler until nextHandlers.size) {
            val handler = nextHandlers[index]
            val p = handler.predicate
            if (handler is OnHandler) {
                if ((!handled) && p(this)) {
                    predicate = p
                    nextHandler = index + 1
                    handler.process(this)
                }
            }
            else {
                if (p(this)) {
                    predicate = p
                    nextHandler = index + 1
                    handler.process(this)
                }
            }
        }
    }

    fun unauthorized(
        body: Any = response.body,
        headers: Headers = response.headers,
        contentType: ContentType? = response.contentType,
        cookies: List<Cookie> = response.cookies,
        attributes: Map<*, *> = this.attributes,
    ) {
        send(UNAUTHORIZED_401, body, headers, contentType, cookies, attributes)
    }

    fun forbidden(
        body: Any = response.body,
        headers: Headers = response.headers,
        contentType: ContentType? = response.contentType,
        cookies: List<Cookie> = response.cookies,
        attributes: Map<*, *> = this.attributes,
    ) {
        send(FORBIDDEN_403, body, headers, contentType, cookies, attributes)
    }

    fun internalServerError(
        body: Any = response.body,
        headers: Headers = response.headers,
        contentType: ContentType? = response.contentType,
        cookies: List<Cookie> = response.cookies,
        attributes: Map<*, *> = this.attributes,
    ) {
        send(INTERNAL_SERVER_ERROR_500, body, headers, contentType, cookies, attributes)
    }

    fun serverError(
        status: HttpStatus,
        exception: Exception,
        headers: Headers = response.headers,
        attributes: Map<*, *> = this.attributes,
    ) {
        send(
            status = status,
            body = exception.toText(),
            headers = headers,
            contentType = ContentType(TEXT_PLAIN),
            attributes = attributes,
        )
    }

    fun internalServerError(
        exception: Exception,
        headers: Headers = response.headers,
        attributes: Map<*, *> = this.attributes,
    ) {
        serverError(INTERNAL_SERVER_ERROR_500, exception, headers, attributes)
    }

    fun ok(
        body: Any = response.body,
        headers: Headers = response.headers,
        contentType: ContentType? = response.contentType,
        cookies: List<Cookie> = response.cookies,
        attributes: Map<*, *> = this.attributes,
    ) {
        send(OK_200, body, headers, contentType, cookies, attributes)
    }

    fun sse(body: Publisher<ServerEvent>) {
        ok(
            body = body,
            headers = response.headers + Header("cache-control", "no-cache"),
            contentType = ContentType(TEXT_EVENT_STREAM)
        )
    }

    fun badRequest(
        body: Any = response.body,
        headers: Headers = response.headers,
        contentType: ContentType? = response.contentType,
        cookies: List<Cookie> = response.cookies,
        attributes: Map<*, *> = this.attributes,
    ) {
        send(BAD_REQUEST_400, body, headers, contentType, cookies, attributes)
    }

    fun notFound(
        body: Any = response.body,
        headers: Headers = response.headers,
        contentType: ContentType? = response.contentType,
        cookies: List<Cookie> = response.cookies,
        attributes: Map<*, *> = this.attributes,
    ) {
        send(NOT_FOUND_404, body, headers, contentType, cookies, attributes)
    }

    fun created(
        body: Any = response.body,
        headers: Headers = response.headers,
        contentType: ContentType? = response.contentType,
        cookies: List<Cookie> = response.cookies,
        attributes: Map<*, *> = this.attributes,
    ) {
        send(CREATED_201, body, headers, contentType, cookies, attributes)
    }

    fun redirect(
        status: HttpStatus,
        location: String,
        headers: Headers = response.headers,
        cookies: List<Cookie> = response.cookies,
        attributes: Map<*, *> = this.attributes,
    ) {
        send(
            status,
            headers = headers + Header("location", location),
            cookies = cookies,
            attributes = attributes
        )
    }

    fun found(
        location: String,
        headers: Headers = response.headers,
        cookies: List<Cookie> = response.cookies,
        attributes: Map<*, *> = this.attributes,
    ) {
        redirect(FOUND_302, location, headers, cookies, attributes)
    }

    fun accepted(
        onConnect: WsSession.() -> Unit = {},
        onBinary: WsSession.(data: ByteArray) -> Unit = {},
        onText: WsSession.(text: String) -> Unit = {},
        onPing: WsSession.(data: ByteArray) -> Unit = {},
        onPong: WsSession.(data: ByteArray) -> Unit = {},
        onClose: WsSession.(status: Int, reason: String) -> Unit = { _, _ -> },
    ) {
        response.status = ACCEPTED_202
        response.onConnect = onConnect
        response.onBinary = onBinary
        response.onText = onText
        response.onPing = onPing
        response.onPong = onPong
        response.onClose = onClose
    }

    fun send(
        status: HttpStatus = response.status,
        body: Any = response.body,
        headers: Headers = response.headers,
        contentType: ContentType? = response.contentType,
        cookies: List<Cookie> = response.cookies,
        attributes: Map<*, *> = this.attributes,
    ) {
        send(
            response.apply {
                this.body = body
                this.headers = headers
                this.contentType = contentType
                this.cookies = cookies
                this.status = status
            },
            attributes
        )
    }

    fun send(response: HttpResponsePort, attributes: Map<*, *> = this.attributes) {
        this.event.response.status = response.status

        this.event.response.contentLength = response.contentLength

        this.event.response.onConnect = response.onConnect
        this.event.response.onBinary = response.onBinary
        this.event.response.onText = response.onText
        this.event.response.onPing = response.onPing
        this.event.response.onPong = response.onPong
        this.event.response.onClose = response.onClose

        this.attributes += attributes
    }

    fun send(request: HttpRequestPort, attributes: Map<*, *> = this.attributes) {
        this.event.request = request
        this.attributes += attributes
    }

    fun receive(
        body: Any = response.body,
        headers: Headers = response.headers,
        contentType: ContentType? = response.contentType,
        cookies: List<Cookie> = response.cookies,
        attributes: Map<*, *> = this.attributes,
    ) {
        send(
            request.apply {
                this.body = body
                this.headers = headers
                this.contentType = contentType
                this.cookies = cookies
            },
            attributes
        )
    }
}
