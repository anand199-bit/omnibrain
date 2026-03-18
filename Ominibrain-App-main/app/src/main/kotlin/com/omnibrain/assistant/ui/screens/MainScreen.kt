package com.omnibrain.assistant.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.omnibrain.assistant.ui.components.ChatInput
import com.omnibrain.assistant.ui.components.MessageBubble
import com.omnibrain.assistant.ui.components.MicrophoneButton
import com.omnibrain.assistant.ui.components.WelcomeScreen
import com.omnibrain.assistant.ui.theme.*
import com.omnibrain.assistant.util.PermissionHelper
import com.omnibrain.assistant.viewmodel.AssistantViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: AssistantViewModel,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isListening by viewModel.voiceRecognitionService.isListening.collectAsState()
    val recognizedText by viewModel.voiceRecognitionService.recognizedText.collectAsState()
    val voiceEnabled by viewModel.voiceEnabled.collectAsState()
    
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.RECORD_AUDIO] == true) {
            viewModel.voiceRecognitionService.startListening()
        }
    }
    
    LaunchedEffect(recognizedText) {
        if (recognizedText.isNotEmpty() && !isListening) {
            viewModel.sendMessage(recognizedText)
            viewModel.voiceRecognitionService.clearRecognizedText()
        }
    }
    
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "OmniBrain",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    ) 
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleVoice() }) {
                        Icon(
                            imageVector = if (voiceEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Toggle voice",
                            tint = if (voiceEnabled) ElectricBlue else androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f)
                        )
                    }
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(
                            Icons.Default.Delete, 
                            contentDescription = "Clear chat",
                            tint = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings, 
                            contentDescription = "Settings",
                            tint = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DeepPurple, DarkPurple)
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Main content area
                if (messages.size <= 1) {
                    // Show welcome screen for initial state
                    WelcomeScreen(modifier = Modifier.weight(1f))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        state = listState,
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(messages, key = { it.id }) { message ->
                            MessageBubble(message = message)
                        }
                        
                        if (isLoading) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(BrightPurple, ElectricBlue)
                                                ),
                                                shape = RoundedCornerShape(18.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = androidx.compose.ui.graphics.Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "Thinking...", 
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
                
                AnimatedVisibility(visible = isListening) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .shadow(12.dp, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = androidx.compose.ui.graphics.Color.Transparent
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            ElectricBlue.copy(alpha = 0.3f),
                                            BrightPurple.copy(alpha = 0.3f)
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = ElectricBlue
                                )
                                Text(
                                    text = recognizedText.ifEmpty { "Listening..." },
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = androidx.compose.ui.graphics.Color.White
                                )
                            }
                        }
                    }
                }
                
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        MicrophoneButton(
                            isListening = isListening,
                            onClick = {
                                if (isListening) {
                                    viewModel.voiceRecognitionService.stopListening()
                                } else {
                                    if (PermissionHelper.hasAudioPermission(context)) {
                                        viewModel.voiceRecognitionService.startListening()
                                    } else {
                                        permissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.RECORD_AUDIO,
                                                Manifest.permission.READ_CONTACTS,
                                                Manifest.permission.CALL_PHONE
                                            )
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
                
                ChatInput(
                    onSendMessage = { viewModel.sendMessage(it) }
                )
            }
        }
    }
}
