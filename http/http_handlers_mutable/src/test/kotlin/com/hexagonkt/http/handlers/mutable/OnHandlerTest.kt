package com.hexagonkt.http.handlers.mutable

import com.hexagonkt.http.mutable.model.HttpMethod.GET
import com.hexagonkt.http.mutable.model.HttpRequest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class OnHandlerTest {

    @Test fun `OnHandler constructors works properly`() {
        val handler1 = OnHandler(GET) { ok() }
        val handler2 = OnHandler(setOf(GET)) { ok() }
        val context = HttpContext(HttpRequest())

        assertEquals(handler1.predicate, handler2.predicate)
        handler1.process(context)
        assert(context.handled)
    }
}
