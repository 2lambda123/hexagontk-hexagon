
plugins {
    id("java-library")
}

apply(from = "../gradle/kotlin.gradle")
apply(from = "../gradle/publish.gradle")
apply(from = "../gradle/dokka.gradle")
apply(from = "../gradle/native.gradle")

dependencies {
    val nettyVersion = properties["nettyVersion"]

    "api"(project(":http_server"))
    "api"(platform("io.netty:netty-bom:$nettyVersion"))
    "api"("io.netty:netty-codec-http") { exclude(group = "org.slf4j") }
    "api"("io.netty:netty-codec-http2") { exclude(group = "org.slf4j") }

    if (System.getProperty("os.name").lowercase().contains("mac"))
        "api"("io.netty:netty-tcnative:2.0.60.Final:osx-x86_64") { exclude(group = "org.slf4j") }

    "testImplementation"(project(":http_test"))
    "testImplementation"(project(":http_client_jetty_ws"))
    "testImplementation"(project(":serialization_jackson_json"))
    "testImplementation"(project(":serialization_jackson_yaml"))
}
