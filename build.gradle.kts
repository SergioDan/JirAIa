plugins {
    kotlin("jvm") version "1.9.0"
   // id("kotlinx-serialization")// version "1.3.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.4.32"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.langchain4j:langchain4j:0.24.0")
    implementation("dev.langchain4j:langchain4j-ollama:0.24.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation("io.ktor:ktor-server-core:2.3.4")
    implementation("io.ktor:ktor-server-netty:2.3.4")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-server-call-logging:2.3.4")
    implementation("io.ktor:ktor-server-cors:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("io.ktor:ktor-server-status-pages:2.3.4")
    implementation("io.ktor:ktor-server-host-common:2.3.4")
    implementation("io.ktor:ktor-server-resources:2.3.4")
    implementation("io.ktor:ktor-server-default-headers:2.3.4")


    val ktorVersion = "2.3.4"
    implementation("io.ktor:ktor-client-core:2.3.4")
    implementation("io.ktor:ktor-client-cio:${ktorVersion}")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
}

application {
    mainClass.set("com.jiraia.ServerKt")
}