package com.hexagontk.http.handlers.mutable

import com.hexagontk.core.require
import com.hexagontk.http.mutable.model.METHOD_NOT_ALLOWED_405
import com.hexagontk.http.mutable.model.NOT_FOUND_404
import com.hexagontk.http.mutable.model.HttpMethod.GET
import com.hexagontk.http.mutable.model.HttpMethod.PUT
import com.hexagontk.http.mutable.model.*
import com.hexagontk.http.mutable.model.HttpRequest
import com.hexagontk.http.mutable.model.HttpResponse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class HttpServerTest {

    @Test fun `Handlers proof of concept`() {
        val path = PathHandler(
            BeforeHandler { ok() },
            AfterHandler {},
            FilterHandler { next() },

            PathHandler("/a",
                BeforeHandler { notFound() },
                BeforeHandler("/{p}") {
                    send(HttpResponse(
                        status = OK_200,
                        body = pathParameters.require("p")
                    ))
                },

                PathHandler("/b",
                    BeforeHandler { send(status = METHOD_NOT_ALLOWED_405) },
                    BeforeHandler(GET) { send(status = NO_CONTENT_204) },
                    BeforeHandler("/{p}") {
                        send(HttpResponse(
                            status = OK_200,
                            body = pathParameters.require("p")
                        ))
                    }
                )
            )
        )

        assertEquals(NOT_FOUND_404, path.processContext(HttpRequest(path = "/a")).status)
        assertEquals(NO_CONTENT_204, path.processContext(HttpRequest(path = "/a/b")).status)
        assertEquals(METHOD_NOT_ALLOWED_405, path.processContext(HttpRequest(PUT, path = "/a/b")).status)
        assertEquals(OK_200, path.processContext(HttpRequest(path = "/a/x")).status)
        assertEquals("x", path.processContext(HttpRequest(path = "/a/x")).response.body)
        assertEquals(OK_200, path.processContext(HttpRequest(path = "/a/b/value")).status)
        assertEquals("value", path.processContext(HttpRequest(path = "/a/b/value")).response.body)
    }

    @Test fun `Builder proof of concept`() {

        val path = path {
            path("/a") {
                on { ok() }
                path("/b") {
                    on { send(status = ACCEPTED_202) }
                }
            }
        }

        assertEquals(OK_200, path.processContext(HttpRequest(path = "/a")).status)
        assertEquals(ACCEPTED_202, path.processContext(HttpRequest(path = "/a/b")).status)

        val contextPath = path("/p") {
            path("/a") {
                on { ok() }
                path("/b") {
                    on { send(status = ACCEPTED_202) }
                }
            }
        }

        assertEquals(NOT_FOUND_404, contextPath.processContext(HttpRequest(path = "/a")).status)
        assertEquals(OK_200, contextPath.processContext(HttpRequest(path = "/p/a")).status)
        assertEquals(ACCEPTED_202, contextPath.processContext(HttpRequest(path = "/p/a/b")).status)
    }
}
