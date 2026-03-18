package com.omnibrain.assistant.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.omnibrain.assistant.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GeminiRepository(private val apiKey: String) {
    
    private val model = GenerativeModel(
        modelName = "gemini-2.5-pro",
        apiKey = apiKey
    )
    
    private val chat = model.startChat(
        history = listOf(
            content("user") { text("You are OmniBrain, a helpful AI assistant. Provide concise, accurate, and friendly responses.") },
            content("model") { text("Understood! I'm OmniBrain, ready to help you with information and tasks.") }
        )
    )
    
    suspend fun sendMessage(message: String): Flow<String> = flow {
        try {
            val response = chat.sendMessage(message)
            emit(response.text ?: "I couldn't generate a response.")
        } catch (e: Exception) {
            emit("Error: ${e.message ?: "Unknown error occurred"}")
        }
    }
    
    fun clearHistory() {
        // Chat history is maintained by the chat instance
        // To clear, we'd need to create a new chat instance
    }
}
