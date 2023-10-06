package com.hexagonkt.handlers.mutable

import kotlin.reflect.KClass

/**
 * After handlers are executed even if a filter don't call next handler (if after was added before
 * filter).
 *
 * After handlers' filters are always true because they are meant to be evaluated on the **return**.
 * If they are not called in first place, they won't be executed on the return of the next handler.
 * Their filter is evaluated after the `next` call, not before.
 */
data class ExceptionHandler<T : Any, E : Exception>(
    val exception: KClass<E>,
    val clear: Boolean = true,
    val exceptionCallback: (Context<T>, E) -> Unit,
) : Handler<T> {

    override val predicate: (Context<T>) -> Boolean = { true }
    override val callback: (Context<T>) -> Unit = { context ->
        exceptionCallback(context, castException(context.exception, exception))
        if (clear)
            context.exception = null
    }

    override fun process(context: Context<T>) {
        context.next()
        context.predicate = ::afterPredicate
        try {
            if (afterPredicate(context))
                callback(context)
        }
        catch (e: Exception) {
            context.exception = e
        }
    }

    private fun afterPredicate(context: Context<T>): Boolean {
        val exceptionClass = context.exception?.javaClass ?: return false
        return exception.java.isAssignableFrom(exceptionClass)
    }
}
