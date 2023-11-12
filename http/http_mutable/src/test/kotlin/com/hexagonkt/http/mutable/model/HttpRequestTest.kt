package com.hexagonkt.http.mutable.model

import com.hexagonkt.core.fail
import com.hexagonkt.core.media.TEXT_CSS
import com.hexagonkt.core.media.TEXT_HTML
import com.hexagonkt.core.media.TEXT_PLAIN
import com.hexagonkt.core.media.TEXT_RICHTEXT
import com.hexagonkt.core.security.loadKeyStore
import com.hexagonkt.core.urlOf
import com.hexagonkt.http.mutable.model.HttpMethod.*
import com.hexagonkt.http.mutable.model.HttpProtocol.*
import java.security.cert.X509Certificate
import org.junit.jupiter.api.Test
import kotlin.test.*

internal class HttpRequestTest {

    private companion object {
        const val HOST: String = "localhost"
        const val PORT: Int = 80
        const val PATH: String = "path"

        val testProtocol: HttpProtocol = HTTP
        val testQueryParameters: QueryParameters = QueryParameters(
            QueryParameter("qp1", "value1", "value2")
        )
    }

    private object TestEmptyRequest: HttpRequestPort {
        override var method: HttpMethod = GET
        override var protocol: HttpProtocol = testProtocol
        override var host: String = HOST
        override var port: Int = PORT
        override var path: String = PATH
        override var queryParameters: QueryParameters = testQueryParameters
        override var formParameters: FormParameters = FormParameters()
        override var body: Any = ""
        override var headers: Headers = Headers()
        override var contentType: ContentType? = ContentType(TEXT_PLAIN)
        override var accept: List<ContentType> = emptyList()
        override var authorization: Authorization? = authorization()
        override var certificateChain: List<X509Certificate> = emptyList()
        override var contentLength: Long = 0

        override var cookies: List<Cookie> =
            listOf(Cookie("name1", "value1"), Cookie("name2", "value2"))

        override var parts: List<HttpPart> =
            listOf(HttpPart("name1", "value1"), HttpPart("name2", "value2"))
    }

    private object TestRequest : HttpRequestPort {
        override var method: HttpMethod = GET
        override var protocol: HttpProtocol = testProtocol
        override var host: String = HOST
        override var port: Int = PORT
        override var path: String = PATH
        override var queryParameters: QueryParameters = testQueryParameters
        override var formParameters: FormParameters = FormParameters()
        override var body: Any = ""
        override var headers: Headers = Headers(
            Header("user-agent", "User Agent"),
            Header("referer", "Referer"),
            Header("origin", "Origin"),
            Header("authorization", "Basic value"),
        )
        override var contentType: ContentType? = ContentType(TEXT_PLAIN)
        override var accept: List<ContentType> = emptyList()
        override var authorization: Authorization? = authorization()
        override var certificateChain: List<X509Certificate> = emptyList()
        override var contentLength: Long = 0

        override var cookies: List<Cookie> =
            listOf(Cookie("name1", "value1"), Cookie("name2", "value2"))

        override var parts: List<HttpPart> =
            listOf(HttpPart("name1", "value1"), HttpPart("name2", "value2"))
    }

    private val keyStoreResource = "hexagonkt.p12"
    private val keyStoreUrl = urlOf("classpath:$keyStoreResource")
    private val keyStore = loadKeyStore(keyStoreUrl, keyStoreResource.reversed())
    private val certificate = keyStore.getCertificate("hexagonkt")
    private val certificates = listOf(certificate as X509Certificate)

    private fun httpRequestData(): HttpRequest =
        HttpRequest(
            method = POST,
            protocol = HTTPS,
            host = "127.0.0.1",
            port = 9999,
            path = "/path",
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

    @Test fun `HTTP Request comparison works ok`() {
        val httpRequest = httpRequestData()

        assertEquals(httpRequest, httpRequest)
        assertEquals(httpRequestData(), httpRequestData())
        assertFalse(httpRequest.equals(""))

        assertNotEquals(
            httpRequest,
            httpRequest.copy(queryParameters = QueryParameters(QueryParameter("k", "v", "v2")))
        )

        assertEquals(httpRequest.hashCode(), httpRequestData().hashCode())
        assertEquals(
            httpRequest.copy(contentType = null).hashCode(),
            httpRequestData().copy(contentType = null).hashCode()
        )
    }

    @Test fun `'certificate' returns the first chain certificate`() {
        val requestData = httpRequestData()
        assertNull(requestData.certificate())
        assertEquals(certificate,requestData.copy(certificateChain = certificates).certificate())
    }

    @Test fun `Common headers access methods work as expected`() {
        val requestData = httpRequestData()

        assertNull(requestData.userAgent())
        assertNull(requestData.referer())
        assertNull(requestData.origin())

        requestData.copy(
            headers = Headers(
                Header("user-agent", "ua"),
                Header("referer", "r"),
                Header("origin", "o"),
            )
        ).let {
            assertEquals("ua", it.userAgent())
            assertEquals("r", it.referer())
            assertEquals("o", it.origin())
        }
    }

    @Test fun `Header convenience methods works properly`() {
        assertEquals("User Agent", TestRequest.userAgent())
        assertEquals("Referer", TestRequest.referer())
        assertEquals("Origin", TestRequest.origin())

        assertNull(TestEmptyRequest.userAgent())
        assertNull(TestEmptyRequest.referer())
        assertNull(TestEmptyRequest.origin())
    }

    @Test fun `Request authorization header is parsed properly`() {
        assertEquals("Basic", TestRequest.authorization()?.type)
        assertEquals("value", TestRequest.authorization()?.value)

        val invalidAuthorization = "Basic words header"
        TestRequest.headers += Header("authorization", invalidAuthorization)
        assertEquals("Basic", TestRequest.authorization()?.type)
        assertEquals("words header", TestRequest.authorization()?.value)

        assertNull(TestEmptyRequest.authorization)
    }

    @Test fun `Cookies map works properly`() {
        assertEquals(Cookie("name1", "value1"), TestRequest.cookiesMap()["name1"])
        assertEquals(Cookie("name2", "value2"), TestRequest.cookiesMap()["name2"])
        assertNull(TestRequest.cookiesMap()["name3"])
    }

    @Test fun `Parts map works properly`() {
        assertEquals(HttpPart("name1", "value1"), TestRequest.partsMap()["name1"])
        assertEquals(HttpPart("name2", "value2"), TestRequest.partsMap()["name2"])
        assertNull(TestRequest.partsMap()["name3"])
    }

    @Test fun `URL is generated correctly`() {
        assertEquals(urlOf("http://localhost/path?qp1=value1&qp1=value2"), TestRequest.url())
        TestRequest.port = 9999
        assertEquals(urlOf("http://localhost:9999/path?qp1=value1&qp1=value2"), TestRequest.url())
        TestRequest.queryParameters = QueryParameters()
        assertEquals(urlOf("http://localhost:9999/path"), TestRequest.url())
    }

    @Test fun `HTTP Request operators work ok`() {
        httpRequestData().let {
            val o = it.copy()
            val header = Header("h", "v")
            it + header

            assertEquals(it, o.copy(headers = it.headers + header))
        }
//        assertEquals(
//            httpRequest + Headers(header),
//            httpRequest.copy(headers = httpRequest.headers + header)
//        )
//
//        val queryParameter = QueryParameter("h", "v")
//        assertEquals(
//            httpRequest + queryParameter,
//            httpRequest.copy(queryParameters = httpRequest.queryParameters + queryParameter)
//        )
//        assertEquals(
//            httpRequest + QueryParameters(queryParameter),
//            httpRequest.copy(queryParameters = httpRequest.queryParameters + queryParameter)
//        )
//
//        val httpPart = HttpPart("h", "v")
//        assertEquals(
//            httpRequest + httpPart,
//            httpRequest.copy(parts = httpRequest.parts + httpPart)
//        )
//        assertEquals(
//            httpRequest + listOf(httpPart),
//            httpRequest.copy(parts = httpRequest.parts + httpPart)
//        )
//
//        val formParameter = FormParameter("h", "v")
//        assertEquals(
//            httpRequest + formParameter,
//            httpRequest.copy(formParameters = httpRequest.formParameters + formParameter)
//        )
//        assertEquals(
//            httpRequest + FormParameters(formParameter),
//            httpRequest.copy(formParameters = httpRequest.formParameters + formParameter)
//        )
//
//        val cookie = Cookie("n", "v")
//        assertEquals(
//            httpRequest + cookie,
//            httpRequest.copy(cookies = httpRequest.cookies + cookie)
//        )
    }
}
