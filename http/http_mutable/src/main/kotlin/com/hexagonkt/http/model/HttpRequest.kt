package com.hexagonkt.http.model

import com.hexagonkt.http.*
import com.hexagonkt.http.model.HttpMethod.GET
import com.hexagonkt.http.model.HttpProtocol.HTTP
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

    override fun with(
        body: Any,
        headers: Headers,
        contentType: ContentType?,
        method: HttpMethod,
        protocol: HttpProtocol,
        host: String,
        port: Int,
        path: String,
        queryParameters: QueryParameters,
        parts: List<HttpPart>,
        formParameters: FormParameters,
        cookies: List<Cookie>,
        accept: List<ContentType>,
        authorization: Authorization?,
        certificateChain: List<X509Certificate>,
    ): HttpRequestPort =
        copy(
            body = body,
            headers = headers,
            contentType = contentType,
            method = method,
            protocol = protocol,
            host = host,
            port = port,
            path = path,
            queryParameters = queryParameters,
            parts = parts,
            formParameters = formParameters,
            cookies = cookies,
            accept = accept,
            authorization = authorization,
            certificateChain = certificateChain,
        )
}
