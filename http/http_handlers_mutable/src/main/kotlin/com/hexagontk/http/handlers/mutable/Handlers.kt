@file:Suppress("FunctionName") // Uppercase functions are used for providing named constructors

package com.hexagontk.http.handlers.mutable

import com.hexagontk.core.error
import com.hexagontk.core.loggerOf
import com.hexagontk.handlers.mutable.Context
import com.hexagontk.http.mutable.model.*
import com.hexagontk.http.mutable.model.HttpMethod.*
import com.hexagontk.http.mutable.model.HttpProtocol.HTTP
import com.hexagontk.http.mutable.model.HttpCall
import com.hexagontk.http.mutable.model.HttpRequest
import java.lang.IllegalStateException
import java.lang.System.Logger
import java.math.BigInteger
import java.security.cert.X509Certificate
import kotlin.reflect.KClass
import kotlin.reflect.cast

typealias HttpCallback = HttpContext.() -> Unit
typealias HttpExceptionCallback<T> = HttpContext.(T) -> Unit

private val logger: Logger by lazy { loggerOf(HttpHandler::class.java.packageName) }
private val BODY_TYPES_NAMES: String by lazy {
    val bodyTypes = setOf(String::class, ByteArray::class, Int::class, Long::class)
    bodyTypes.joinToString(", ") { it.simpleName.toString() }
}

internal fun toCallback(block: HttpCallback): (Context<HttpCall>) -> Unit =
    { context: Context<HttpCall> -> HttpContext(context).block() }

fun HttpCallback.process(
    request: HttpRequest,
    attributes: Map<*, *> = emptyMap<Any, Any>()
) {
    this(HttpContext(request = request, attributes = attributes))
}

fun HttpCallback.process(
    method: HttpMethod = GET,
    protocol: HttpProtocol = HTTP,
    host: String = "localhost",
    port: Int = 80,
    path: String = "",
    queryParameters: QueryParameters = QueryParameters(),
    headers: Headers = Headers(),
    body: Any = "",
    parts: List<HttpPart> = emptyList(),
    formParameters: FormParameters = FormParameters(),
    cookies: List<Cookie> = emptyList(),
    contentType: ContentType? = null,
    certificateChain: List<X509Certificate> = emptyList(),
    accept: List<ContentType> = emptyList(),
    contentLength: Long = -1L,
    attributes: Map<*, *> = emptyMap<Any, Any>(),
) {
    this.process(
        HttpRequest(
            method,
            protocol,
            host,
            port,
            path,
            queryParameters,
            headers,
            body,
            parts,
            formParameters,
            cookies,
            contentType,
            certificateChain,
            accept,
            contentLength,
        ),
        attributes,
    )
}

fun path(pattern: String = "", block: HandlerBuilder.() -> Unit): PathHandler {
    val builder = HandlerBuilder()
    builder.block()
    return path(pattern, builder.handlers)
}

fun path(contextPath: String = "", handlers: List<HttpHandler>): PathHandler =
    handlers
        .let {
            if (it.size == 1 && it[0] is PathHandler)
                (it[0] as PathHandler).addPrefix(contextPath) as PathHandler
            else
                PathHandler(contextPath, it)
        }

fun <T : Exception> Exception(
    exception: KClass<T>? = null,
    status: HttpStatus? = null,
    clear: Boolean = true,
    callback: HttpExceptionCallback<T>,
): AfterHandler =
    AfterHandler(emptySet(), "*", exception, status) {
        callback(this.exception.castException(exception))
        if (clear) this.exception = null
    }

inline fun <reified T : Exception> Exception(
    status: HttpStatus? = null,
    clear: Boolean = true,
    noinline callback: HttpExceptionCallback<T>,
): AfterHandler =
    Exception(T::class, status, clear, callback)

internal fun <T : Exception> Exception?.castException(exception: KClass<T>?) =
    this?.let { exception?.cast(this) } ?: error("Exception 'null' or incorrect type")

fun Get(pattern: String = "", callback: HttpCallback): OnHandler =
    OnHandler(GET, pattern, callback)

fun Ws(pattern: String = "", callback: HttpCallback): OnHandler =
    Get(pattern, callback)

fun Head(pattern: String = "", callback: HttpCallback): OnHandler =
    OnHandler(HEAD, pattern, callback)

fun Post(pattern: String = "", callback: HttpCallback): OnHandler =
    OnHandler(POST, pattern, callback)

fun Put(pattern: String = "", callback: HttpCallback): OnHandler =
    OnHandler(PUT, pattern, callback)

fun Delete(pattern: String = "", callback: HttpCallback): OnHandler =
    OnHandler(DELETE, pattern, callback)

fun Trace(pattern: String = "", callback: HttpCallback): OnHandler =
    OnHandler(TRACE, pattern, callback)

fun Options(pattern: String = "", callback: HttpCallback): OnHandler =
    OnHandler(OPTIONS, pattern, callback)

fun Patch(pattern: String = "", callback: HttpCallback): OnHandler =
    OnHandler(PATCH, pattern, callback)

fun bodyToBytes(body: Any): ByteArray =
    when (body) {
        is String -> body.toByteArray()
        is ByteArray -> body
        is Int -> BigInteger.valueOf(body.toLong()).toByteArray()
        is Long -> BigInteger.valueOf(body).toByteArray()
        else -> {
            val className = body.javaClass.simpleName
            val message = "Unsupported body type: $className. Must be: $BODY_TYPES_NAMES"
            val exception = IllegalStateException(message)

            logger.error(exception)
            throw exception
        }
    }
