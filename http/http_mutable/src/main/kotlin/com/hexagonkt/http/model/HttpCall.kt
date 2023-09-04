package com.hexagonkt.http.model

data class HttpCall(
    var request: HttpRequestPort = HttpRequest(),
    var response: HttpResponsePort = HttpResponse(),
)
