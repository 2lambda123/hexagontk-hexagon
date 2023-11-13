package com.hexagonkt.http.handlers.mutable

import com.hexagonkt.handlers.mutable.AfterHandler
import com.hexagonkt.handlers.mutable.Handler
import com.hexagonkt.http.mutable.model.HttpMethod
import com.hexagonkt.http.mutable.model.HttpStatus
import com.hexagonkt.http.mutable.model.HttpCall
import kotlin.reflect.KClass

data class AfterHandler(
    override var handlerPredicate: HttpPredicate = HttpPredicate(),
    val block: HttpCallback
) : HttpHandler, Handler<HttpCall> by AfterHandler(handlerPredicate, toCallback(block)) {

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

    override fun addPrefix(prefix: String) =
        copy(handlerPredicate = handlerPredicate.addPrefix(prefix))
}
