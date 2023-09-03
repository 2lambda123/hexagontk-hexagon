package com.hexagonkt.handlers.mutable

/**
 * After handlers are executed even if a filter don't call next handler (if after was added before
 * filter).
 *
 * After handlers' filters are always true because they are meant to be evaluated on the **return**.
 * If they are not called in first place, they won't be executed on the return of the next handler.
 * Their filter is evaluated after the `next` call, not before.
 */
data class AfterHandler<T : Any>(
    val afterPredicate: (Context<T>) -> Boolean = { true },
    override val callback: (Context<T>) -> Unit,
) : Handler<T> {

    override val predicate: (Context<T>) -> Boolean = { true }

    override fun process(context: Context<T>) {
        context.next()
        context.predicate = afterPredicate
        try {
            if (afterPredicate.invoke(context))
                callback(context)
        }
        catch (e: Exception) {
            context.exception = e
        }
    }
}
