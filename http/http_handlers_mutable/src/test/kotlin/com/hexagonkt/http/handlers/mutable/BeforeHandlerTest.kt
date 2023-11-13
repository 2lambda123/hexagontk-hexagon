package com.hexagonkt.http.handlers.mutable

import com.hexagonkt.http.mutable.model.HttpMethod.GET
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class BeforeHandlerTest {

    @Test fun `OnHandler constructors works properly`() {
        val handler1 = BeforeHandler(GET) { ok() }
        val handler2 = BeforeHandler(setOf(GET)) { ok() }
        val context = HttpContext()

        assertEquals(handler1.predicate, handler2.predicate)
        handler1.process(context)
        assert(!context.handled)
    }
}
