package com.omnibrain.assistant.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class TextToSpeechService(context: Context) {
    
    private var tts: TextToSpeech? = null
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking
    
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized
    
    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.0f)
                _isInitialized.value = true
                
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }
                    
                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                    
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                })
            }
        }
    }
    
    fun speak(text: String) {
        if (_isInitialized.value) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "OmniBrain")
        }
    }
    
    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }
    
    fun destroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
