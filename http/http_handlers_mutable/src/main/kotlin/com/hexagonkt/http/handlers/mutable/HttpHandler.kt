package com.hexagonkt.http.handlers.mutable

import com.hexagonkt.handlers.mutable.Handler
import com.hexagonkt.http.mutable.model.*
import com.hexagonkt.http.mutable.model.HttpCall

sealed interface HttpHandler : Handler<HttpCall> {
    val handlerPredicate: HttpPredicate

    fun addPrefix(prefix: String): HttpHandler

    fun byMethod(): Map<HttpMethod, HttpHandler> =
        handlerPredicate.methods.associateWith { filter(it) }

    fun filter(method: HttpMethod): HttpHandler =
        when (this) {
            is PathHandler ->
                copy(
                    handlerPredicate = handlerPredicate.clearMethods(),
                    handlers = handlers
                        .filter {
                            val methods = it.handlerPredicate.methods
                            method in methods || methods.isEmpty()
                        }
                        .map { it.filter(method) }
                )

            is OnHandler ->
                copy(handlerPredicate = handlerPredicate.clearMethods())

            is FilterHandler ->
                copy(handlerPredicate = handlerPredicate.clearMethods())

            is AfterHandler ->
                copy(handlerPredicate = handlerPredicate.clearMethods())

            is BeforeHandler ->
                copy(handlerPredicate = handlerPredicate.clearMethods())
        }

    fun process(request: HttpRequestPort) {
        HttpContext(HttpCall(request = request), handlerPredicate).let { context ->
            if (handlerPredicate(context)) process(context)
        }
    }
}
