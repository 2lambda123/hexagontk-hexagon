package com.hexagontk.http.mutable.model

data class ServerEvent(
    var event: String? = null,
    var data: String? = null,
    var id: String? = null,
    var retry: Long? = null,
) {
    val eventData: String by lazy {
        if (event == null && data == null && id == null && retry == null)
            ":\n\n"
        else
            listOf(
                "event" to event,
                "data" to data,
                "id" to id,
                "retry" to retry,
            )
            .filter { it.second != null }
            .joinToString("\n", postfix = "\n\n") { (k, v) -> "$k: $v" }
    }
}
