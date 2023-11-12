package com.hexagonkt.http.mutable.model

import com.hexagonkt.core.urlOf
import com.hexagonkt.http.mutable.formatQueryString
import java.net.URL
import java.security.cert.X509Certificate

// TODO 'formParameters' are a kind of 'part' and both are handled as part of the 'body'
//  they could be handled as a special kind of type in body processing (List<HttpPartPort>)
interface HttpRequestPort : HttpMessage {
    var method: HttpMethod                        // "GET"
    var protocol: HttpProtocol                    // "http"
    var host: String                              // "example.com"
    var port: Int                                 // 80
    var path: String                              // "/foo" servlet path + path info
    var queryParameters: QueryParameters
    var parts: List<HttpPart>                     // hash of multipart parts
    var formParameters: FormParameters
    var accept: List<ContentType>
    var authorization: Authorization?

    var certificateChain: List<X509Certificate>
    var contentLength: Long                       // length of request.body (or 0)

    operator fun plus(header: Header) {
        this.headers += header
    }

    operator fun plus(queryParameter: QueryParameter) {
        this.queryParameters += queryParameter
    }

    operator fun plus(part: HttpPart) {
        this.parts += part
    }

    operator fun plus(formParameter: FormParameter) {
        this.formParameters += formParameter
    }

    operator fun plus(cookie: Cookie) {
        this.cookies += cookie
    }

    operator fun plus(headers: Headers) {
        this.headers += headers
    }

    operator fun plus(queryParameters: QueryParameters) {
        this.queryParameters += queryParameters
    }

    operator fun plus(parts: List<HttpPart>) {
        this.parts += parts
    }

    operator fun plus(formParameters: FormParameters) {
        this.formParameters += formParameters
    }

    fun certificate(): X509Certificate? =
        certificateChain.firstOrNull()

    fun partsMap(): Map<String, HttpPart> =
        parts.associateBy { it.name }

    fun url(): URL =
        when {
            queryParameters.isEmpty() && port == 80 -> "${protocol.schema}://$host/$path"
            queryParameters.isEmpty() -> "${protocol.schema}://$host:$port/$path"
            else -> "${protocol.schema}://$host:$port/$path?${formatQueryString(queryParameters)}"
        }
        .let(::urlOf)

    fun userAgent(): String? =
        headers["user-agent"]?.value as? String

    fun referer(): String? =
        headers["referer"]?.value as? String

    fun origin(): String? =
        headers["origin"]?.value as? String

    fun authorization(): Authorization? =
        (headers["authorization"]?.value as? String)
            ?.split(" ", limit = 2)
            ?.let { Authorization(it.first(), it.last()) }
}
