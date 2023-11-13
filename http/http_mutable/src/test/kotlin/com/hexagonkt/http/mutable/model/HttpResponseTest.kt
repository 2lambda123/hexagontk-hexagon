package com.hexagonkt.http.mutable.model

import com.hexagonkt.core.media.TEXT_HTML
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class HttpResponseTest {

    private fun httpResponseData(contentType: ContentType? = ContentType(TEXT_HTML)): HttpResponse =
        HttpResponse(
            body = "response",
            headers = Headers(Header("hr1", "hr1v1", "hr1v2")),
            contentType = contentType,
            cookies = listOf(Cookie("cn", "cv")),
            status = NOT_FOUND_404,
        )

    @Test fun `HTTP Response comparison works ok`() {
        val httpResponse = httpResponseData()

        assertEquals(httpResponse, httpResponse)
        assertEquals(httpResponseData(), httpResponseData())
        assertFalse(httpResponse.equals(""))

        assertEquals(httpResponse.hashCode(), httpResponseData().hashCode())
        assertEquals(
            httpResponse.copy(contentType = null).hashCode(),
            httpResponseData(null).hashCode()
        )
    }

    @Test fun `HTTP Response operators work ok`() {

        val header = Header("h", "v")
        httpResponseCopy().let { (o, r) ->
            r + header
            assertEquals(r, o.copy(headers = o.headers + header))
        }
        httpResponseCopy().let { (o, r) ->
            r + Headers(header)
            assertEquals(r, o.copy(headers = o.headers + header))
        }

        val cookie = Cookie("n", "v")
        httpResponseCopy().let { (o, r) ->
            r + cookie
            assertEquals(r, o.copy(cookies = o.cookies + cookie))
        }
        httpResponseCopy().let { (o, r) ->
            r + listOf(cookie)
            assertEquals(r, o.copy(cookies = o.cookies + cookie))
        }
    }

    private fun httpResponseCopy(): Pair<HttpResponse, HttpResponse> =
        httpResponseData().let { it.copy() to it }
}
