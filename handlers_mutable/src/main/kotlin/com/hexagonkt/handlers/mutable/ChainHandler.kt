package com.hexagonkt.handlers.mutable

data class ChainHandler<T : Any>(
    val handlers: List<Handler<T>>,
    override val predicate: (Context<T>) -> Boolean = { true },
) : Handler<T> {

    override val callback: (Context<T>) -> Unit = {}

    constructor(
        filter: (Context<T>) -> Boolean,
        vararg handlers: Handler<T>,
    ) : this(handlers.toList(), filter)

    constructor(vararg handlers: Handler<T>) : this(handlers.toList(), { true })

    override fun process(context: Context<T>) {
        val nextHandlers = context.nextHandlers
        val nextHandler = context.nextHandler

        context.nextHandlers = handlers
        context.nextHandler = 0
        context.next()

        context.predicate = predicate
        context.nextHandlers = nextHandlers
        context.nextHandler = nextHandler

        context.next()
    }
}
