package com.jiraia

import com.jiraia.api.registerRoutes
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                call.respond(mapOf("error" to (cause.message ?: "Unknown error")))
            }
        }

        install(ContentNegotiation) {
            json()
        }
        install(CallLogging)
        install(CORS) {
            anyHost()
            allowHeader("*")
            allowMethod(io.ktor.http.HttpMethod.Post)
        }
        routing {
            registerRoutes()
        }
    }.start(wait = true)
}