package com.hexagonkt.handlers.mutable

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class ExceptionHandlerTest {

    @Test fun `Exceptions are cleared properly`() {
        var context = EventContext("test", { true })

        ChainHandler(
            ExceptionHandler<String, Exception>(Exception::class) { c, _ -> c.event = "ok" },
            OnHandler { error("Error") }
        )
        .process(context)

        assertEquals("ok", context.event)
        assertNull(context.exception)

        context = EventContext("test", { true })

        ChainHandler(
            ExceptionHandler<String, Exception>(Exception::class) { _, _ -> },
            OnHandler { error("Error") }
        )
        .process(context)

        assertEquals("test", context.event)
        assertNull(context.exception)

        context = EventContext("test", { true })

        ChainHandler(
            ExceptionHandler<String, Exception>(Exception::class, false) { c, _ -> c.event = "ok" },
            OnHandler { error("Error") }
        )
        .process(context)

        assertEquals("Error", context.exception?.message)
        assert(context.exception is IllegalStateException)

        context = EventContext("test", { true })

        ChainHandler(
            ExceptionHandler<String, Exception>(Exception::class, false) { c, _ -> c.event = "ok" },
            OnHandler { it.event = "no problem" }
        )
        .process(context)

        assertNull(context.exception)
        assertEquals("no problem", context.event)

        context = EventContext("test", { true })

        ChainHandler(
            ExceptionHandler<String, Exception>(Exception::class, false) { c, _ -> c.event = "ok" },
            ExceptionHandler(Exception::class, false) { _, _ -> error("Fail") },
            OnHandler { error("Error") }
        )
        .process(context)

        assertEquals("ok", context.event)
        assertEquals("Fail", context.exception?.message)
        assert(context.exception is IllegalStateException)
    }
}
