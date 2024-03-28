package com.hexagonkt.handlers

data class ChainHandler<T : Any>(
    val handlers: List<Handler<T>>,
    override val predicate: (Context<T>) -> Boolean = { true },
    override val parent: Handler<T>? = null,
) : Handler<T> {

    override val callback: (Context<T>) -> Context<T> = { it }

    constructor(
        filter: (Context<T>) -> Boolean,
        vararg handlers: Handler<T>,
    ) : this(handlers.toList(), filter)

    constructor(
        handlers: List<Handler<T>>,
        filter: (Context<T>) -> Boolean,
    ) : this(handlers, filter, null)

    constructor(vararg handlers: Handler<T>) : this(handlers.toList(), { true })

    override fun process(context: Context<T>): Context<T> {
        val nestedContext = context.with(nextHandlers = handlers, nextHandler = 0)
        val nestedResult = nestedContext.next()
        val followUpContext = nestedResult.with(
            predicate = predicate,
            nextHandlers = context.nextHandlers,
            nextHandler = context.nextHandler
        )
        return followUpContext.next()
    }
}
