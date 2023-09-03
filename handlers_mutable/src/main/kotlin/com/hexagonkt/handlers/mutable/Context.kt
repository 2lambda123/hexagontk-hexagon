package com.hexagonkt.handlers.mutable

/**
 * Context for an event.
 *
 * @param T Event type.
 */
interface Context<T : Any> {
    var event: T
    var predicate: (Context<T>) -> Boolean
    var nextHandlers: List<Handler<T>>
    var nextHandler: Int
    var exception: Exception?
    var attributes: Map<*, *>
    var handled: Boolean

    fun next() {
        for (index in nextHandler until nextHandlers.size) {
            val handler = nextHandlers[index]
            val p = handler.predicate
            if (handler is OnHandler) {
                if ((!handled) && p(this)) {
                    predicate = p
                    nextHandler = index + 1
                    handler.process(this)
                    return
                }
            }
            else {
                if (p(this)) {
                    predicate = p
                    nextHandler = index + 1
                    handler.process(this)
                    return
                }
            }
        }
    }
}
