package com.jiraia.llm

import dev.langchain4j.model.ollama.OllamaLanguageModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object LlmParser {

    private val model = OllamaLanguageModel.builder()
        .baseUrl("http://localhost:11434")
        .modelName("llama3")
        .build()

    fun parseCommand(userInput: String): JiraCommand {
        val prompt = """
            You are a Jira assistant. Your task is to extract structured commands from the user's natural-language input.
            
            Always return a **compact JSON object** with the following fields:
            - action: "create", "update", or "transition"
            - summary: a short, Jira-style title (e.g., "Crash on save", "Add dark mode")
            - issueType: "bug", "story", or "task"
            - description (optional): a longer explanation of the issue or feature
            - issueKey (for update/transition actions)
            - field and value (for updates)
            - dueDate (optional, for creation with format YYYY-MM-dd)
            
            🔸 If the user describes an issue or bug with detail, extract:
            - A **clear summary** (title for the Jira issue)
            - A **richer description** (cause, context, or what happened)
            
            📌 Output must ONLY be a JSON object, with no extra explanation.
            
            Here is the user's input:
            "$userInput"
            """.trimIndent()

        val response:dev.langchain4j.model.output.Response<String> = model.generate(prompt)

        val raw = response.content().trim()
        val cleaned = raw.removeSurrounding("```json", "```").trim().removePrefix("```").removeSuffix("```")
        val extraCleaned = extractJsonOnly(cleaned)
        print(extraCleaned)
        val responseCommand = try {
            if (isValidJson(extraCleaned)) {
                Json.decodeFromString(JiraCommand.serializer(), extraCleaned)
            } else {
                JiraCommand(action = "not-created", summary = "invalid llm output")
            }
        } catch (e: Exception) {
            println("⚠️ Failed to decode: ${e.message}")
            JiraCommand(action = "create", summary = "Invalid LLM output")
        }
        print(responseCommand.action)
        return responseCommand
    }

    private fun extractJsonOnly(output: String): String {
        // Try to find the first "{" and the last "}" to extract JSON block
        val start = output.indexOf('{')
        val end = output.lastIndexOf('}')
        return if (start >= 0 && end >= 0 && end > start) {
            output.substring(start, end + 1)
        } else {
            throw IllegalArgumentException("No valid JSON found in: $output")
        }
    }

    private fun isValidJson(json: String): Boolean {
        return try {
            Json.parseToJsonElement(json)
            true
        } catch (e: Exception) {
            false
        }
    }
}

@Serializable
class JiraCommand(
    val action: String,
    val summary: String = "",
    val issueType: String = "task",
    val description: String? = null,
    val issueKey: String? = null,
    val field: String? = null,
    val value: String? = null,
    val dueDate: String? = null,
)