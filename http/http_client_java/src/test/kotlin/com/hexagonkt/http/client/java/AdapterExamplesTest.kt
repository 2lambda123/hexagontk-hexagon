package com.hexagonkt.http.client.java

import com.hexagonkt.http.server.jetty.JettyServletAdapter
import com.hexagonkt.http.test.examples.*
import com.hexagonkt.serialization.jackson.JacksonTextFormat
import com.hexagonkt.serialization.jackson.json.Json
import com.hexagonkt.serialization.jackson.yaml.Yaml
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS.WINDOWS

val clientAdapter: () -> JavaClientAdapter = ::JavaClientAdapter
val serverAdapter: () -> JettyServletAdapter = ::JettyServletAdapter
val formats: List<JacksonTextFormat> = listOf(Json, Yaml)

//internal class AdapterBooksTest : BooksTest(clientAdapter, serverAdapter)
//internal class AdapterErrorsTest : ErrorsTest(clientAdapter, serverAdapter)
//internal class AdapterFiltersTest : FiltersTest(clientAdapter, serverAdapter)
//internal class AdapterClientTest : ClientTest(clientAdapter, serverAdapter, formats)
//@DisabledOnOs(WINDOWS) // TODO Make this work on GitHub runners
//internal class AdapterHttpsTest : HttpsTest(clientAdapter, serverAdapter)
//internal class AdapterZipTest : ZipTest(clientAdapter, serverAdapter)
//internal class AdapterCookiesTest : CookiesTest(clientAdapter, serverAdapter)
//internal class AdapterFilesTest : FilesTest(clientAdapter, serverAdapter)
//internal class AdapterCorsTest : CorsTest(clientAdapter, serverAdapter)
//internal class AdapterSamplesTest : SamplesTest(clientAdapter, serverAdapter)
//internal class AdapterBenchmarkIT : BenchmarkIT(clientAdapter, serverAdapter)
// TODO Implement also in Jetty
//internal class AdapterSseTest : SseTest(clientAdapter, serverAdapter)
