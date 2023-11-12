package com.hexagonkt.http.mutable.model

import com.hexagonkt.http.mutable.model.HttpProtocol.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class HttpProtocolTest {

    @Test fun `HTTP protocols are tested (only for coverage)`() {
        assertEquals("http", HTTP.schema)
        assertEquals("https", HTTPS.schema)
        assertEquals("https", HTTP2.schema)
        assertEquals("http", H2C.schema)
    }
}
