# JirAI 🧠📋

An AI-powered assistant for automating Jira workflows — built with Kotlin, LangChain4j, and Ollama. This project includes:

- 🧠 AI natural language interface for creating/updating Jira issues
- 🔗 Integration with LangChain4j and local LLMs via Ollama
- 🚀 REST API backend (Ktor)

---

## 🛠 Features

- Create, update, and transition Jira tickets via natural language
- Supports local LLMs (e.g. LLaMA 3 via [Ollama](https://ollama.com/))
- REST API with structured error handling
- Parse and extract summaries/descriptions from human prompts
---

## 📦 Tech Stack

| Layer       | Tech                          |
|-------------|-------------------------------|
| Backend     | Kotlin + Ktor + LangChain4j   |
| AI Engine   | Ollama (LLaMA 3, etc.)        |
| Integration | Jira REST API                 |
---

## 🚀 Getting Started

### Prerequisites

- JDK 17+
- Gradle
- [Ollama](https://ollama.com) installed locally
- Valid Jira API credentials:
  - `JIRA_BASE_URL`
  - `JIRA_EMAIL`
  - `JIRA_API_TOKEN`
  - `JIRA_PROJECT_KEY`

### Run the API

```bash
export JIRA_BASE_URL=https://your-domain.atlassian.net
export JIRA_EMAIL=you@example.com
export JIRA_API_TOKEN=your_api_token
export JIRA_PROJECT_KEY=PROJECT

./gradlew run
