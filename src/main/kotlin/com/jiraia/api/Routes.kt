package com.jiraia.api

import com.jiraia.llm.LlmParser
import com.jiraia.llm.JiraCommand
import com.jiraia.jira.JiraToolsImpl
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerRoutes() {
    val jira = JiraToolsImpl()

    route("/api/jira") {
        post("/command") {
            val request = call.receive<JiraCommand>()
            val response = when (request.action) {
                "create" -> jira.createIssue(request.summary, request.description, request.issueType, dueDate = request.dueDate)
                "update" -> jira.updateIssue(request.issueKey ?: "", request.field ?: "", request.value ?: "")
                "transition" -> jira.transitionIssue(request.issueKey ?: "", request.value ?: "")
                else -> "Unsupported action"
            }
            call.respond(mapOf("result" to response))
        }

        post("/parse") {
            try {
                val input = call.receive<Map<String, String>>()["prompt"] ?: ""
                val command = LlmParser.parseCommand(input)
                call.respond(command)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "{\"error\":\"something happened ${e.localizedMessage}\"")
            }
        }
    }
}