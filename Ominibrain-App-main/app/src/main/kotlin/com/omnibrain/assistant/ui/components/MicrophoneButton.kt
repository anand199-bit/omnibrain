package com.omnibrain.assistant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.omnibrain.assistant.ui.theme.BrightPurple
import com.omnibrain.assistant.ui.theme.ElectricBlue
import com.omnibrain.assistant.ui.theme.SoftBlue

@Composable
fun MicrophoneButton(
    isListening: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (isListening) {
            // Outer glow ring
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(scale)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ElectricBlue.copy(alpha = 0.4f),
                                BrightPurple.copy(alpha = 0.2f),
                                androidx.compose.ui.graphics.Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
                    .blur(20.dp)
            )
            
            // Middle ring
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale * 0.8f)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                BrightPurple.copy(alpha = 0.5f),
                                ElectricBlue.copy(alpha = 0.3f),
                                androidx.compose.ui.graphics.Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }
        
        // Main button with gradient and shadow
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .size(90.dp)
                .shadow(
                    elevation = if (isListening) 24.dp else 16.dp,
                    shape = CircleShape,
                    ambientColor = if (isListening) ElectricBlue else BrightPurple,
                    spotColor = if (isListening) ElectricBlue else BrightPurple
                ),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (isListening) {
                                listOf(ElectricBlue, SoftBlue, BrightPurple)
                            } else {
                                listOf(BrightPurple, SoftBlue)
                            }
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Microphone",
                    modifier = Modifier.size(40.dp),
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}
