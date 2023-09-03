package com.hexagonkt.handlers.mutable

data class EventContext<T : Any>(
    override var event: T,
    override var predicate: (Context<T>) -> Boolean,
    override var nextHandlers: List<Handler<T>> = emptyList(),
    override var nextHandler: Int = 0,
    override var exception: Exception? = null,
    override var attributes: Map<*, *> = emptyMap<Any, Any>(),
    override var handled: Boolean = false,
) : Context<T>
