package co.there4.hexagon.web.integration

import co.there4.hexagon.web.*
import org.testng.annotations.Test

@Test class HexagonIT : ItTest() {
    override fun initialize(srv: Server) {
        srv.get ("/books/{id}") {
            ok ("${request ["id"]}:${request.body}")
        }
        srv.get ("/books/{id}/{title}") { ok ("${request ["id"]}:${request ["title"]} ${request.body}") }
        srv.trace ("/books/{id}/{title}") { ok ("${request ["id"]}:${request ["title"]} ${request.body}") }
        srv.patch ("/books/{id}/{title}") { ok ("${request ["id"]}:${request ["title"]} ${request.body}") }
        srv.head ("/books/{id}/{title}") {
            response.addHeader("id", request.parameter("id"))
            response.addHeader("title", request.parameter("title"))
        }
    }

    fun foo () {
        withClients {
            assertResponseContains (get ("/books/101"), 200, "101")
        }
    }

    fun getBook () {
        withClients {
            assertResponseContains (get ("/books/101/Hamlet"), 200, "101", "Hamlet")
            assertResponseContains (trace ("/books/101/Hamlet"), 200, "101", "Hamlet")
            assertResponseContains (patch ("/books/101/Hamlet"), 200, "101", "Hamlet")
            assertResponseContains (head ("/books/101/Hamlet"), 200)

            assertResponseContains (get ("/books/101/Hamlet", "body"), 200, "101", "Hamlet", "body")
            assertResponseContains (trace ("/books/101/Hamlet", "body"), 200, "101", "Hamlet", "body")
            assertResponseContains (patch ("/books/101/Hamlet", "body"), 200, "101", "Hamlet", "body")
            assertResponseContains (head ("/books/101/Hamlet", "body"), 200)
        }
    }
}
