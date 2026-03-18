package com.omnibrain.assistant.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omnibrain.assistant.data.model.Message
import com.omnibrain.assistant.data.repository.GeminiRepository
import com.omnibrain.assistant.service.AppLauncherService
import com.omnibrain.assistant.service.ContactService
import com.omnibrain.assistant.service.TextToSpeechService
import com.omnibrain.assistant.service.VoiceRecognitionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssistantViewModel(context: Context, apiKey: String) : ViewModel() {
    
    private val geminiRepository = GeminiRepository(apiKey)
    private val appLauncherService = AppLauncherService(context)
    private val contactService = ContactService(context)
    val voiceRecognitionService = VoiceRecognitionService(context)
    val ttsService = TextToSpeechService(context)
    
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _voiceEnabled = MutableStateFlow(true)
    val voiceEnabled: StateFlow<Boolean> = _voiceEnabled.asStateFlow()
    
    init {
        addWelcomeMessage()
    }
    
    private fun addWelcomeMessage() {
        val welcomeMessage = Message(
            content = "Hi! I'm OmniBrain. I can help you with information or open apps. Try saying 'Open Chrome' or ask me anything!",
            isUser = false
        )
        _messages.value = listOf(welcomeMessage)
    }
    
    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        // Add user message
        val userMessage = Message(content = text, isUser = true)
        _messages.value = _messages.value + userMessage
        
        // Check if it's a call command first
        val callResult = contactService.parseAndCallContact(text)
        
        if (callResult.isNotEmpty()) {
            // It was a call command
            val responseMessage = Message(content = callResult, isUser = false)
            _messages.value = _messages.value + responseMessage
            
            if (_voiceEnabled.value) {
                ttsService.speak(callResult)
            }
            return
        }
        
        // Check if it's an app launch command
        val launchResult = appLauncherService.parseAndLaunchApp(text)
        
        if (launchResult.isNotEmpty()) {
            // It was an app launch command
            val responseMessage = Message(content = launchResult, isUser = false)
            _messages.value = _messages.value + responseMessage
            
            if (_voiceEnabled.value) {
                ttsService.speak(launchResult)
            }
        } else {
            // Send to Gemini AI
            _isLoading.value = true
            viewModelScope.launch {
                try {
                    geminiRepository.sendMessage(text).collect { response ->
                        _isLoading.value = false
                        val aiMessage = Message(content = response, isUser = false)
                        _messages.value = _messages.value + aiMessage
                        
                        if (_voiceEnabled.value) {
                            ttsService.speak(response)
                        }
                    }
                } catch (e: Exception) {
                    _isLoading.value = false
                    val errorMessage = Message(
                        content = "Sorry, I encountered an error: ${e.message}",
                        isUser = false
                    )
                    _messages.value = _messages.value + errorMessage
                }
            }
        }
    }
    
    fun clearChat() {
        _messages.value = emptyList()
        addWelcomeMessage()
    }
    
    fun toggleVoice() {
        _voiceEnabled.value = !_voiceEnabled.value
        if (!_voiceEnabled.value) {
            ttsService.stop()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        voiceRecognitionService.destroy()
        ttsService.destroy()
    }
}
