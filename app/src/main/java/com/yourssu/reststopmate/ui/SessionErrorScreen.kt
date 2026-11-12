package com.yourssu.reststopmate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SessionErrorScreen(message: String, onRetry: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text(message)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("다시 시도") }
    }
}
