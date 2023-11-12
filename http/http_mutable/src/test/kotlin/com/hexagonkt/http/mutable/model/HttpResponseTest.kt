package com.hexagonkt.http.mutable.model

import com.hexagonkt.core.media.TEXT_HTML
import com.hexagonkt.core.media.TEXT_RICHTEXT
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

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
        val httpResponse = httpResponseData()

//        val header = Header("h", "v")
//        assertEquals(
//            httpResponse + header,
//            httpResponse.copy(headers = httpResponse.headers + header)
//        )
//        assertEquals(
//            httpResponse + Headers(header),
//            httpResponse.copy(headers = httpResponse.headers + header)
//        )
//
//        val cookie = Cookie("n", "v")
//        assertEquals(
//            httpResponse + cookie,
//            httpResponse.copy(cookies = httpResponse.cookies + cookie)
//        )
//        assertEquals(
//            httpResponse + listOf(cookie),
//            httpResponse.copy(cookies = httpResponse.cookies + cookie)
//        )
    }
}
