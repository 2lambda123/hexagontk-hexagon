package com.hexagonkt.http.handlers.mutable

import com.hexagonkt.http.mutable.model.HttpMethod.GET
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class AfterHandlerTest {

    @Test fun `AfterHandler constructors without path pattern works properly`() {
        val handler1 = AfterHandler(GET) { ok() }
        val handler2 = AfterHandler(setOf(GET)) { ok() }

        assertEquals(handler1.predicate, handler2.predicate)
    }

    @Test fun `AfterHandler constructors with pattern works properly`() {
        val handler1 = AfterHandler(emptySet(), "/a") { ok() }
        val handler2 = AfterHandler("/a") { ok() }

        assertEquals(handler1.predicate, handler2.predicate)
    }
}
