package com.hexagonkt.http.mutable.model

import com.hexagonkt.http.mutable.*
import com.hexagonkt.http.mutable.model.HttpMethod.GET
import com.hexagonkt.http.mutable.model.HttpProtocol.HTTP
import java.security.cert.X509Certificate

data class HttpRequest(
    override var method: HttpMethod = GET,
    override var protocol: HttpProtocol = HTTP,
    override var host: String = "localhost",
    override var port: Int = 80,
    override var path: String = "",
    override var queryParameters: QueryParameters = QueryParameters(),
    override var headers: Headers = Headers(),
    override var body: Any = "",
    override var parts: List<HttpPart> = emptyList(),
    override var formParameters: FormParameters = FormParameters(),
    override var cookies: List<Cookie> = emptyList(),
    override var contentType: ContentType? = null,
    override var certificateChain: List<X509Certificate> = emptyList(),
    override var accept: List<ContentType> = emptyList(),
    override var contentLength: Long = -1L,
    override var authorization: Authorization? = null,
) : HttpRequestPort {

    init {
        checkHeaders(headers)
    }
}
