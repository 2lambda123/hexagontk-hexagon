
module com.hexagonkt.http.test {

    requires transitive kotlin.test;
    requires kotlinx.coroutines.core.jvm;

    requires transitive com.hexagonkt.core;
    requires transitive com.hexagonkt.logging.slf4j.jul;
    requires transitive com.hexagonkt.http.client;
    requires transitive com.hexagonkt.http.server;

    requires transitive org.junit.jupiter.api;
    requires io.swagger.v3.oas.models;
    requires swagger.parser.v3;

    exports com.hexagonkt.http.test.examples;
    exports com.hexagonkt.http.test.openapi;
}
