package com.hexagonkt.http.handlers.mutable

import com.hexagonkt.core.media.TEXT_HTML
import com.hexagonkt.core.media.TEXT_PLAIN
import com.hexagonkt.http.mutable.model.*
import com.hexagonkt.http.mutable.model.HttpMethod.*
import com.hexagonkt.http.mutable.model.HttpProtocol.HTTPS
import com.hexagonkt.http.mutable.model.FOUND_302
import com.hexagonkt.http.mutable.model.BAD_GATEWAY_502
import com.hexagonkt.http.mutable.model.INTERNAL_SERVER_ERROR_500
import com.hexagonkt.http.mutable.patterns.TemplatePathPattern
import com.hexagonkt.http.mutable.model.HttpCall
import com.hexagonkt.http.mutable.model.HttpRequest
import com.hexagonkt.http.mutable.model.HttpResponse
import org.junit.jupiter.api.Test
import java.lang.RuntimeException
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

internal class HttpContextTest {

    private fun httpServerRequest(): HttpRequest =
        HttpRequest(
            method = POST,
            protocol = HTTPS,
            host = "127.0.0.1",
            port = 9999,
            path = "/path/v1",
            queryParameters = QueryParameters(QueryParameter("k", "v")),
            headers = Headers(Header("h1", "h1v1", "h1v2")),
            body = "request",
            parts = listOf(HttpPart("n", "b")),
            formParameters = FormParameters(FormParameter("fp1", "fp1v1", "fp1v2")),
            cookies = listOf(Cookie("cn", "cv")),
            contentType = ContentType(TEXT_PLAIN),
            certificateChain = emptyList(),
            accept = listOf(ContentType(TEXT_HTML)),
        )

    @Test fun `Context helper methods work properly`() {
        val context = HttpContext(
            request = httpServerRequest(),
            predicate = HttpPredicate(pathPattern = TemplatePathPattern("/path/{p1}")),
        )

        assertSame(context.method, context.event.request.method)
        assertSame(context.protocol, context.event.request.protocol)
        assertSame(context.host, context.event.request.host)
        assertEquals(context.port, context.event.request.port)
        assertSame(context.path, context.event.request.path)
        assertSame(context.queryParameters, context.event.request.queryParameters)
        assertSame(context.parts, context.event.request.parts)
        assertSame(context.formParameters, context.event.request.formParameters)
        assertSame(context.accept, context.event.request.accept)
        assertSame(context.certificateChain, context.event.request.certificateChain)
        assertEquals(context.partsMap, context.event.request.partsMap())
        assertEquals(context.url, context.event.request.url())
        assertSame(context.userAgent, context.event.request.userAgent())
        assertSame(context.referer, context.event.request.referer())
        assertSame(context.origin, context.event.request.origin())
        assertSame(context.certificate, context.event.request.certificate())
        assertSame(context.status, context.event.response.status)
    }

    @Test fun `Send without parameters return the same response`() {
        val serverContext = HttpContext(
            request = httpServerRequest(),
            predicate = HttpPredicate(pathPattern = TemplatePathPattern("/path/{p1}", true)),
        )
        serverContext.send()
        assertEquals(serverContext, serverContext)
    }

    @Test fun `Response helpers return correct values`() {
        val path = PathHandler(
            OnHandler("/ok") { ok() },
            OnHandler("/notFound") { notFound() },
            OnHandler("/noContent") { send(HttpResponse(status = NO_CONTENT_204)) },
            OnHandler("/accepted") { send(status = ACCEPTED_202) },
            OnHandler("/created") { created() },
            OnHandler("/badRequest") { badRequest() },
            OnHandler("/clientError") { send(FORBIDDEN_403) },
            OnHandler("/success") { send(NO_CONTENT_204) },
            OnHandler("/serverError") { send(BAD_GATEWAY_502) },
            OnHandler("/redirect") { send(FOUND_302) },
            OnHandler("/internalServerError") { internalServerError() },
            OnHandler("/internalServerErrorException") { internalServerError(RuntimeException()) },
            OnHandler("/serverErrorException") { serverError(BAD_GATEWAY_502, RuntimeException()) },
        )

        assertEquals(OK_200, path.processContext(HttpRequest(path = "/ok")).status)
        assertEquals(NOT_FOUND_404, path.processContext(HttpRequest(path = "/notFound")).status)
        assertEquals(NO_CONTENT_204, path.processContext(HttpRequest(path = "/noContent")).status)
        assertEquals(ACCEPTED_202, path.processContext(HttpRequest(path = "/accepted")).status)
        assertEquals(CREATED_201, path.processContext(HttpRequest(path = "/created")).status)
        assertEquals(BAD_REQUEST_400, path.processContext(HttpRequest(path = "/badRequest")).status)
        assertEquals(FORBIDDEN_403, path.processContext(HttpRequest(path = "/clientError")).status)
        assertEquals(NO_CONTENT_204, path.processContext(HttpRequest(path = "/success")).status)
        assertEquals(BAD_GATEWAY_502, path.processContext(HttpRequest(path = "/serverError")).status)
        assertEquals(FOUND_302, path.processContext(HttpRequest(path = "/redirect")).status)
        assertEquals(
            INTERNAL_SERVER_ERROR_500,
            path.processContext(HttpRequest(path = "/internalServerError")).status
        )

        path.processContext(HttpRequest(path = "/internalServerErrorException")).response.let {
            assertEquals(INTERNAL_SERVER_ERROR_500, it.status)
            assertTrue(it.bodyString().contains(RuntimeException::class.java.name))
            assertEquals(ContentType(TEXT_PLAIN), it.contentType)
        }

        path.processContext(HttpRequest(path = "/serverErrorException")).response.let {
            assertEquals(BAD_GATEWAY_502, it.status)
            assertTrue(it.bodyString().contains(RuntimeException::class.java.name))
            assertEquals(ContentType(TEXT_PLAIN), it.contentType)
        }
    }

    @Test fun `'next' executes the next handler in the chain`() {
        val context = HttpContext(
            HttpCall(httpServerRequest(), HttpResponse()),
            HttpPredicate(),
            listOf(
                OnHandler("*") { ok() },
            )
        )

        context.next()

        assertEquals(OK_200, context.response.status)
    }

    @Test fun `Client errors helpers returns proper status`() {
        val httpContext = HttpContext()

        httpContext.unauthorized()
        assertEquals(UNAUTHORIZED_401, httpContext.status)
        httpContext.forbidden()
        assertEquals(FORBIDDEN_403, httpContext.status)
    }

    @Test fun `Context can change the request`() {
        val path = PathHandler(
            BeforeHandler("*") { receive(body = request.bodyString() + "_modified") },
            OnHandler("/test") { ok(body = request.body) },
        )

        path.processContext(HttpRequest(path = "/test")).apply {
            assertEquals(OK_200, status)
            assertEquals("_modified", response.bodyString())
        }

        path.processContext(HttpRequest(path = "/test", body = "body")).apply {
            assertEquals(OK_200, status)
            assertEquals("body_modified", response.bodyString())
        }
    }
}
