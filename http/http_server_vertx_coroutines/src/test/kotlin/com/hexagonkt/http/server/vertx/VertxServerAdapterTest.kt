package com.hexagonkt.http.server.vertx

import com.hexagonkt.http.server.coroutines.HttpServer
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

internal class VertxServerAdapterTest {

    @Test fun `Nima starts`() {
        main()
        assert(server.started())
        server.stop()
        assertFalse(server.started())
    }
}

lateinit var server: HttpServer

fun main() {
    server = serve {
        get("/ey") { ok("Done!") }
    }
}
