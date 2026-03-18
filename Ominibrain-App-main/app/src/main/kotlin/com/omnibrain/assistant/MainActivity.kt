package com.omnibrain.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.omnibrain.assistant.ui.screens.MainScreen
import com.omnibrain.assistant.ui.screens.SettingsScreen
import com.omnibrain.assistant.ui.theme.OmniBrainTheme
import com.omnibrain.assistant.util.Constants
import com.omnibrain.assistant.viewmodel.AssistantViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val android.content.Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_NAME)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OmniBrainTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OmniBrainApp()
                }
            }
        }
    }
}

@Composable
fun OmniBrainApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var currentScreen by remember { mutableStateOf("main") }
    
    // Default API key - users can change it in settings if needed
    val defaultApiKey = "AIzaSyD8rAgyZ_ORf16z-jWgw1LX3TVbTJYC6GA"
    
    var apiKey by remember { mutableStateOf(defaultApiKey) }
    
    val apiKeyFlow = context.dataStore.data.map { preferences ->
        preferences[stringPreferencesKey(Constants.API_KEY_PREF)] ?: ""
    }
    
    LaunchedEffect(Unit) {
        apiKeyFlow.collect { key ->
            if (key.isNotEmpty()) {
                apiKey = key
            } else {
                // Use default API key
                apiKey = defaultApiKey
            }
        }
    }
    
    val viewModel = remember(apiKey) {
        AssistantViewModel(context, apiKey)
    }
    
    when (currentScreen) {
        "main" -> {
            MainScreen(
                viewModel = viewModel,
                onNavigateToSettings = { currentScreen = "settings" }
            )
        }
        "settings" -> {
            SettingsScreen(
                onNavigateBack = { currentScreen = "main" },
                onSaveApiKey = { key ->
                    scope.launch {
                        context.dataStore.edit { preferences ->
                            preferences[stringPreferencesKey(Constants.API_KEY_PREF)] = key
                        }
                        apiKey = key
                    }
                }
            )
        }
    }
}
