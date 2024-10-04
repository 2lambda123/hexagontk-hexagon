package com.hexagontk.http.mutable.model

data class HttpCall(
    var request: HttpRequestPort = HttpRequest(),
    var response: HttpResponsePort = HttpResponse(),
)
