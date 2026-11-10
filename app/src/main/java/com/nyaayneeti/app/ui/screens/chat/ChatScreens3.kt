package com.nyaayneeti.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyaayneeti.app.ui.theme.*

@Composable
fun CallScreen(peerName: String, onEnd: () -> Unit) {
    var muted by remember { mutableStateOf(false) }
    var videoOff by remember { mutableStateOf(false) }
    var seconds by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) { while (true) { kotlinx.coroutines.delay(1000); seconds++ } }

    Box(Modifier.fillMaxSize().background(ObsidianBackground)) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            Spacer(Modifier.height(40.dp))
            Surface(Modifier.size(96.dp), shape = CircleShape, color = ObsidianSurfaceElevated, border = androidx.compose.foundation.BorderStroke(2.dp, GoldPrimary)) {
                Box(contentAlignment = Alignment.Center) { Text("⚖", fontSize = 40.sp) }
            }
            Text(peerName, style = MaterialTheme.typography.titleLarge)
            Text(if (videoOff) "Audio call · ${fmt(seconds)}" else "Video call · ${fmt(seconds)}", style = MaterialTheme.typography.bodyMedium, color = GreenSuccess)

            Surface(Modifier.fillMaxWidth().height(240.dp).clip(RoundedCornerShape(24.dp)), color = ObsidianSurface, border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)) {
                Box(contentAlignment = Alignment.Center) {
                    if (videoOff) Text("Camera off", color = TextSub) else Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.Videocam, null, tint = GoldPrimary, modifier = Modifier.size(40.dp)); Text("Secure WebRTC session", color = TextSub, fontSize = 12.sp) }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { muted = !muted }, modifier = Modifier.size(56.dp).background(if (muted) RedContainer else ObsidianSurfaceElevated, CircleShape)) {
                    Icon(if (muted) Icons.Default.MicOff else Icons.Default.Mic, null, tint = if (muted) RedUrgent else TextMain)
                }
                IconButton(onClick = onEnd, modifier = Modifier.size(68.dp).background(RedUrgent, CircleShape)) {
                    Icon(Icons.Default.CallEnd, "End", tint = Color.White)
                }
                IconButton(onClick = { videoOff = !videoOff }, modifier = Modifier.size(56.dp).background(if (videoOff) RedContainer else ObsidianSurfaceElevated, CircleShape)) {
                    Icon(if (videoOff) Icons.Default.VideocamOff else Icons.Default.Videocam, null, tint = if (videoOff) RedUrgent else TextMain)
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

private fun fmt(s: Int): String = "%02d:%02d".format(s / 60, s % 60)
