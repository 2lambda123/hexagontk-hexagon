
plugins {
    id("java-library")
}

apply(from = "$rootDir/gradle/kotlin.gradle")
apply(from = "$rootDir/gradle/publish.gradle")
apply(from = "$rootDir/gradle/dokka.gradle")
apply(from = "$rootDir/gradle/native.gradle")
apply(from = "$rootDir/gradle/detekt.gradle")

dependencies {
    val vertxVersion = properties["vertxVersion"]

    "api"(project(":http:http_server_coroutines"))
    "api"("io.vertx:vertx-web:$vertxVersion")
//    "api"("io.vertx:vertx-lang-kotlin")
    "api"("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")

    "testImplementation"(project(":http:http_test_coroutines"))
    "testImplementation"(project(":http:http_client_jetty"))
    "testImplementation"(project(":serialization:serialization_jackson_json"))
    "testImplementation"(project(":serialization:serialization_jackson_yaml"))
}
