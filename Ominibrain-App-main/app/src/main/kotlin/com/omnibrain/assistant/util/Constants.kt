package com.omnibrain.assistant.util

object Constants {
    const val DATASTORE_NAME = "omnibrain_preferences"
    const val API_KEY_PREF = "gemini_api_key"
    const val VOICE_ENABLED_PREF = "voice_enabled"
    
    // Voice commands - Apps
    const val COMMAND_OPEN = "open"
    const val COMMAND_LAUNCH = "launch"
    const val COMMAND_START = "start"
    
    // Voice commands - Calls
    const val COMMAND_CALL = "call"
    const val COMMAND_PHONE = "phone"
    const val COMMAND_DIAL = "dial"
    
    // System prompts
    val SYSTEM_PROMPT = """You are OmniBrain, a helpful AI assistant. 
        |Provide concise, accurate, and friendly responses. 
        |Keep answers brief but informative.""".trimMargin()
}
