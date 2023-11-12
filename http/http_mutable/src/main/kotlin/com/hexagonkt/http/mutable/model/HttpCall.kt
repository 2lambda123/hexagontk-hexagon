package com.hexagonkt.http.mutable.model

data class HttpCall(
    var request: HttpRequestPort = HttpRequest(),
    var response: HttpResponsePort = HttpResponse(),
)
