package com.hexagontk.http.handlers.mutable

import com.hexagontk.handlers.mutable.FilterHandler
import com.hexagontk.handlers.mutable.Handler
import com.hexagontk.http.mutable.model.HttpMethod
import com.hexagontk.http.mutable.model.HttpStatus
import com.hexagontk.http.mutable.model.HttpCall
import kotlin.reflect.KClass

data class FilterHandler(
    override var handlerPredicate: HttpPredicate = HttpPredicate(),
    val block: HttpCallback
) : HttpHandler, Handler<HttpCall> by FilterHandler(handlerPredicate, toCallback(block)) {

    constructor(
        methods: Set<HttpMethod> = emptySet(),
        pattern: String = "",
        exception: KClass<out Exception>? = null,
        status: HttpStatus? = null,
        block: HttpCallback,
    ) :
        this(HttpPredicate(methods, pattern, exception, status), block)

    constructor(method: HttpMethod, pattern: String = "", block: HttpCallback) :
        this(setOf(method), pattern, block = block)

    constructor(pattern: String, block: HttpCallback) :
        this(emptySet(), pattern, block = block)

    override fun addPrefix(prefix: String): HttpHandler =
        copy(handlerPredicate = handlerPredicate.addPrefix(prefix))
}
