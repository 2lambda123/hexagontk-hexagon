package com.hexagonkt.handlers.mutable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.fail

internal class HandlerTest {

    internal companion object {
        fun <T : Any> Handler<T>.process(event: T): T =
            EventContext(event, predicate).apply { this@process.process(this) }.event
    }

    @Test fun `Calling next in the last handler returns the last context`() {

        val chain = ChainHandler<String>(
            OnHandler { it.event += "_" },
            FilterHandler {
                it.next()
                it.next()
            },
        )

        assertEquals("a_", chain.process("a"))
        assertEquals("b_", chain.process("b"))
    }

    @Test fun `Error in a filter returns proper exception in context`() {
        val filter = FilterHandler<String> { error("failure") }
        val context = EventContext("a", filter.predicate)
        filter.process(context)
        assertEquals("failure", context.exception?.message)
    }

    @Test fun `When a callback completes, then the result is returned`() {
        val filter = FilterHandler<String> { it.event += ":OK" }
        assertEquals("Message:OK", filter.process("Message"))
    }

    @Test fun `When a callback fail, then the context contains the exception`() {
        listOf<Handler<Unit>>(
            FilterHandler { error("Filter Failure") },
            OnHandler { error("Before Failure") },
            AfterHandler { error("After Failure") },
        )
        .forEach {
            val context = EventContext(Unit, it.predicate)
            it.process(context)
            val exception = context.exception
            val message = exception?.message ?: fail("Exception message missing")
            assertIs<IllegalStateException>(exception)
            assertTrue(message.matches("(Filter|Before|After) Failure".toRegex()))
        }
    }
}
