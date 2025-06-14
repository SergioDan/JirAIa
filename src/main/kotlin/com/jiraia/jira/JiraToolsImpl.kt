package com.jiraia.jira

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Base64
import java.util.Date

class JiraToolsImpl {

    private val baseUrl = System.getenv("JIRA_BASE_URL") ?: "https://your-domain.atlassian.net"
    private val email = System.getenv("JIRA_EMAIL") ?: "your-email@example.com"
    private val apiToken = System.getenv("JIRA_API_TOKEN") ?: "your-api-token"
    private val projectKey = System.getenv("JIRA_PROJECT_KEY") ?: "PROJ"

    private val authHeader = "Basic " + Base64.getEncoder().encodeToString("$email:$apiToken".toByteArray())

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    fun createIssue(summary: String, description: String?, issueType: String, dueDate: String?): String = runBlocking {
        val response: HttpResponse = client.post("$baseUrl/rest/api/3/issue") {
            headers {
                append(HttpHeaders.Authorization, authHeader)
                append(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            setBody(JiraCreateRequest(
                JiraFields.createFieldsBasedOnProjectKey(
                    projectKey = projectKey,
                    summary,
                    JiraDescription.createJiraDescriptionBasedOnDescription(description ?: ""),
                    JiraIssueType(issueType),
                    dueDate
                )
            ))
        }

        val json = response.body<String>()
        "Issue created: $json"
    }

    fun updateIssue(issueKey: String, field: String, value: String): String = runBlocking {
        val body = mapOf("fields" to mapOf(field to value))
        val response = client.put("$baseUrl/rest/api/3/issue/$issueKey") {
            headers {
                append(HttpHeaders.Authorization, authHeader)
                append(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            setBody(body)
        }
        "Issue updated: ${response.status}"
    }

    fun transitionIssue(issueKey: String, transition: String): String = runBlocking {
        val body = mapOf("transition" to mapOf("id" to transition))
        val response = client.post("$baseUrl/rest/api/3/issue/$issueKey/transitions") {
            headers {
                append(HttpHeaders.Authorization, authHeader)
                append(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            setBody(body)
        }
        "Issue transitioned: ${response.status}"
    }
}

@Serializable
data class JiraCreateRequest(val fields: JiraFields)

@Serializable
data class JiraFields(
    val project: JiraProject,
    val summary: String,
    val description: JiraDescription,
    val issuetype: JiraIssueType,
    @SerialName("duedate")
    val dueDate: String,
    val environment: JiraDescription,
    @Contextual
    val components: List<Map<String, String>>,
) {
    companion object {
        fun createFieldsBasedOnProjectKey(projectKey: String, summary: String, description: JiraDescription, issuetype: JiraIssueType, dueDate: String?) : JiraFields {
            return JiraFields(
                project = JiraProject(projectKey), summary, description, issuetype,
                dueDate = dueDate ?: SimpleDateFormat("YYYY-MM-DD").format(Date.from(Instant.ofEpochMilli(System.currentTimeMillis() + (7*24*60*1000)))),
                environment = JiraDescription.createJiraDescriptionBasedOnDescription("Mobile, Web, Backend, Everything mix together"),
                components = listOf(mapOf("name" to "Development")),
            )
        }
    }
}

@Serializable
data class JiraProject(val key: String)

@Serializable
data class JiraIssueType(val name: String)

@Serializable
data class JiraDescription(val type: String = "doc", val version: Int = 1, val content: List<Content>) {
    companion object {
        fun createJiraDescriptionBasedOnDescription(description: String) =
            JiraDescription(content = listOf(Content(content = listOf(TextContent(text = description)))))

    }
}

@Serializable
data class Content(val type: String = "paragraph", val content: List<TextContent>)

@Serializable
data class TextContent(val type: String = "text", val text: String)