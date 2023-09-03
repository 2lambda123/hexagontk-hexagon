package com.hexagonkt.handlers.mutable

data class BeforeHandler<T : Any>(
    override val predicate: (Context<T>) -> Boolean = { true },
    override val callback: (Context<T>) -> Unit,
) : Handler<T> {

    override fun process(context: Context<T>) =
        try {
            callback(context)
        }
        catch (e: Exception) {
            context.exception = e
        }
        finally {
            context.next()
        }
}
